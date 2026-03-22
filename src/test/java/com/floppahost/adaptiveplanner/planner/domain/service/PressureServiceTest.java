package com.floppahost.adaptiveplanner.planner.domain.service;

import com.floppahost.adaptiveplanner.planner.domain.academic.Exam;
import com.floppahost.adaptiveplanner.planner.domain.planning.engine.PressureService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.within;

@DisplayName("PressureService Tests")
class PressureServiceTest {

    private PressureService pressureService;
    private UUID userId;
    private UUID mathSubjectId;
    private UUID physicsSubjectId;
    private LocalDate today;

    @BeforeEach
    void setUp() {
        pressureService = new PressureService();
        userId = UUID.randomUUID();
        mathSubjectId = UUID.randomUUID();
        physicsSubjectId = UUID.randomUUID();
        today = LocalDate.of(2024, 2, 19);
    }

    @Test
    @DisplayName("Should calculate pressure for exam in 1 date")
    void shouldCalculatePressureForExamInOneDay() {
        // Given
        Exam exam = createExam(mathSubjectId, today.plusDays(1), LocalTime.of(8, 30), 5, 5);

        // When
        double pressure = pressureService.calculateExamPressure(today, exam);

        // Then
        assertThat(pressure).isEqualTo(25.0); // (5 * 5) / 1
    }

    @Test
    @DisplayName("Should calculate pressure for exam in 7 days")
    void shouldCalculatePressureForExamInSevenDays() {
        // Given
        Exam exam = createExam(mathSubjectId, today.plusDays(7), LocalTime.of(8, 30), 5, 5);

        // When
        double pressure = pressureService.calculateExamPressure(today, exam);

        // Then
        assertThat(pressure).isCloseTo(3.571, within(0.001)); // (5 * 5) / 7
    }

    @Test
    @DisplayName("Should return zero pressure for past exams")
    void shouldReturnZeroForPastExams() {
        // Given
        Exam pastExam = createExam(mathSubjectId, today.minusDays(5), LocalTime.of(8, 30), 5, 5);

        // When
        double pressure = pressureService.calculateExamPressure(today, pastExam);

        // Then
        assertThat(pressure).isEqualTo(0.0);
    }

    @Test
    @DisplayName("Should return zero pressure for exam today that already passed")
    void shouldReturnZeroForExamToday() {
        // Given - Exam is today but time has passed
        Exam examToday = createExam(mathSubjectId, today.minusDays(1), LocalTime.of(8, 30), 5, 5);

        // When
        double pressure = pressureService.calculateExamPressure(today, examToday);

        // Then
        assertThat(pressure).isEqualTo(0.0);
    }

    @Test
    @DisplayName("Should calculate higher pressure for difficult/important exams")
    void shouldCalculateHigherPressureForDifficultExams() {
        // Given
        Exam easyExam = createExam(mathSubjectId, today.plusDays(7), LocalTime.of(8, 30), 1, 1);
        Exam hardExam = createExam(mathSubjectId, today.plusDays(7), LocalTime.of(10, 15), 5, 5);

        // When
        double easyPressure = pressureService.calculateExamPressure(today, easyExam);
        double hardPressure = pressureService.calculateExamPressure(today, hardExam);

        // Then
        assertThat(hardPressure).isGreaterThan(easyPressure);
        assertThat(easyPressure).isCloseTo(0.143, within(0.001)); // (1 * 1) / 7
        assertThat(hardPressure).isCloseTo(3.571, within(0.001)); // (5 * 5) / 7
    }

    @Test
    @DisplayName("Should aggregate pressure by subject")
    void shouldAggregatePressureBySubject() {
        // Given - Multiple exams for same subject
        List<Exam> exams = List.of(
                createExam(mathSubjectId, today.plusDays(3), LocalTime.of(8, 30), 4, 5),   // pressure = 20/3 = 6.67
                createExam(mathSubjectId, today.plusDays(7), LocalTime.of(10, 15), 3, 3),   // pressure = 9/7 = 1.29
                createExam(physicsSubjectId, today.plusDays(5), LocalTime.of(12, 15), 5, 4) // pressure = 20/5 = 4.0
        );

        // When
        Map<UUID, Double> subjectPressure = pressureService.buildSubjectPressure(today, exams);

        // Then
        assertThat(subjectPressure).hasSize(2);
        assertThat(subjectPressure.get(mathSubjectId))
                .isCloseTo(7.952, within(0.001)); // 6.67 + 1.29
        assertThat(subjectPressure.get(physicsSubjectId))
                .isCloseTo(4.0, within(0.001));
    }

    @Test
    @DisplayName("Should exclude past exams from subject pressure")
    void shouldExcludePastExamsFromSubjectPressure() {
        // Given
        List<Exam> exams = List.of(
                createExam(mathSubjectId, today.plusDays(3), LocalTime.of(8, 30), 5, 5),   // Future - included
                createExam(mathSubjectId, today.minusDays(2), LocalTime.of(10, 15), 5, 5),  // Past - excluded
                createExam(physicsSubjectId, today.minusDays(7), LocalTime.of(12, 15), 5, 5) // Past - excluded
        );

        // When
        Map<UUID, Double> subjectPressure = pressureService.buildSubjectPressure(today, exams);

        // Then
        assertThat(subjectPressure).hasSize(1);
        assertThat(subjectPressure).containsKey(mathSubjectId);
        assertThat(subjectPressure).doesNotContainKey(physicsSubjectId);
    }

    @Test
    @DisplayName("Should return empty map when no upcoming exams")
    void shouldReturnEmptyMapWhenNoUpcomingExams() {
        // Given - Only past exams
        List<Exam> exams = List.of(
                createExam(mathSubjectId, today.minusDays(5), LocalTime.of(8, 30), 5, 5),
                createExam(physicsSubjectId, today.minusDays(10), LocalTime.of(10, 15), 5, 5)
        );

        // When
        Map<UUID, Double> subjectPressure = pressureService.buildSubjectPressure(today, exams);

        // Then
        assertThat(subjectPressure).isEmpty();
    }

    @Test
    @DisplayName("Should handle empty exam list")
    void shouldHandleEmptyExamList() {
        // Given
        List<Exam> exams = List.of();

        // When
        Map<UUID, Double> subjectPressure = pressureService.buildSubjectPressure(today, exams);

        // Then
        assertThat(subjectPressure).isEmpty();
    }

    @Test
    @DisplayName("Should calculate pressure correctly for exam on same date in future")
    void shouldCalculatePressureForSameDayFutureExam() {
        // Given - Exam is today (0 days left)
        Exam examToday = createExam(mathSubjectId, today, LocalTime.of(8, 30), 5, 5);

        // When
        double pressure = pressureService.calculateExamPressure(today, examToday);

        // Then - Uses max(days_left, 1) = max(0, 1) = 1
        assertThat(pressure).isEqualTo(25.0); // (5 * 5) / 1
    }

    @Test
    @DisplayName("Should handle multiple exams on different days for same subject")
    void shouldHandleMultipleExamsOnDifferentDays() {
        // Given
        List<Exam> exams = List.of(
                createExam(mathSubjectId, today.plusDays(1), LocalTime.of(8, 30), 5, 5),   // Very urgent
                createExam(mathSubjectId, today.plusDays(14), LocalTime.of(10, 15), 3, 3),  // Less urgent
                createExam(mathSubjectId, today.plusDays(30), LocalTime.of(12, 15), 2, 2)   // Least urgent
        );

        // When
        Map<UUID, Double> subjectPressure = pressureService.buildSubjectPressure(today, exams);

        // Then - Should sum all pressures
        double expectedPressure =
                (5.0 * 5.0) +      // 25.0
                        (3.0 * 3.0 / 14.0) +     // 0.643
                        (2.0 * 2.0 / 30.0);      // 0.133

        assertThat(subjectPressure.get(mathSubjectId))
                .isCloseTo(expectedPressure, within(0.001));
    }

    @Test
    @DisplayName("Should calculate different pressures for different difficulty levels")
    void shouldCalculateDifferentPressuresForDifficultyLevels() {
        // Given - Same days left, same importance, different difficulty
        Exam difficulty1 = createExam(mathSubjectId, today.plusDays(5), LocalTime.of(8, 30), 1, 5);
        Exam difficulty3 = createExam(mathSubjectId, today.plusDays(5), LocalTime.of(10, 15), 3, 5);
        Exam difficulty5 = createExam(mathSubjectId, today.plusDays(5), LocalTime.of(12, 15), 5, 5);

        // When
        double pressure1 = pressureService.calculateExamPressure(today, difficulty1);
        double pressure3 = pressureService.calculateExamPressure(today, difficulty3);
        double pressure5 = pressureService.calculateExamPressure(today, difficulty5);

        // Then
        assertThat(pressure1).isCloseTo(1.0, within(0.01));  // (1 * 5) / 5
        assertThat(pressure3).isCloseTo(3.0, within(0.01));  // (3 * 5) / 5
        assertThat(pressure5).isCloseTo(5.0, within(0.01));  // (5 * 5) / 5
        assertThat(pressure5).isGreaterThan(pressure3);
        assertThat(pressure3).isGreaterThan(pressure1);
    }

    @Test
    @DisplayName("Should calculate different pressures for different importance levels")
    void shouldCalculateDifferentPressuresForImportanceLevels() {
        // Given - Same days left, same difficulty, different importance
        Exam importance1 = createExam(mathSubjectId, today.plusDays(5), LocalTime.of(8, 30), 5, 1);
        Exam importance3 = createExam(mathSubjectId, today.plusDays(5), LocalTime.of(10, 15), 5, 3);
        Exam importance5 = createExam(mathSubjectId, today.plusDays(5), LocalTime.of(12, 15), 5, 5);

        // When
        double pressure1 = pressureService.calculateExamPressure(today, importance1);
        double pressure3 = pressureService.calculateExamPressure(today, importance3);
        double pressure5 = pressureService.calculateExamPressure(today, importance5);

        // Then
        assertThat(pressure1).isCloseTo(1.0, within(0.01));  // (5 * 1) / 5
        assertThat(pressure3).isCloseTo(3.0, within(0.01));  // (5 * 3) / 5
        assertThat(pressure5).isCloseTo(5.0, within(0.01));  // (5 * 5) / 5
        assertThat(pressure5).isGreaterThan(pressure3);
        assertThat(pressure3).isGreaterThan(pressure1);
    }

    private Exam createExam(UUID subjectId, LocalDate examDate, LocalTime time, int difficulty, int importance) {
        return Exam.builder()
                .userId(userId)
                .subjectId(subjectId)
                .startsAt(LocalDateTime.of(examDate, time))
                .difficulty(difficulty)
                .importance(importance)
                .build();
    }
}
