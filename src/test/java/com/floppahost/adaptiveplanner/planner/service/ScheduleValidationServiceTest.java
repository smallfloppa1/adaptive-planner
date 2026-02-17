package com.floppahost.adaptiveplanner.planner.service;

import com.floppahost.adaptiveplanner.planner.domain.exception.PlanValidationException;
import com.floppahost.adaptiveplanner.planner.domain.model.Block;
import com.floppahost.adaptiveplanner.planner.domain.model.DayPlan;
import com.floppahost.adaptiveplanner.planner.domain.model.UserProfile;
import com.floppahost.adaptiveplanner.planner.domain.service.ScheduleValidationService;
import com.floppahost.adaptiveplanner.planner.domain.value.BlockKind;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.*;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("ScheduleValidationService Tests")
class ScheduleValidationServiceTest {

    private ScheduleValidationService validator;
    private UserProfile profile;
    private UUID userId;
    private LocalDate testDay;
    private ZoneId timezone;

    @BeforeEach
    void setUp() {
        validator = new ScheduleValidationService();
        userId = UUID.randomUUID();
        testDay = LocalDate.of(2024, 2, 19);
        timezone = ZoneId.of("Europe/Warsaw");

        profile = UserProfile.builder()
            .id(userId)
            .wakeTime(LocalTime.of(7, 0))
            .sleepTime(LocalTime.of(23, 0))
            .focusMinutes(40)
            .breakMinutes(10)
            .maxHeavyBlocksPerDay(3)
            .maxTotalPlannedMinutesPerDay(300)
            .build();
    }

    @Test
    @DisplayName("Should validate a valid day plan")
    void shouldValidateValidDayPlan() {
        // Given
        List<Block> blocks = List.of(
            createBlock(9, 0, 10, 0, BlockKind.STUDY),
            createBlock(10, 30, 11, 30, BlockKind.STUDY),
            createBlock(14, 0, 15, 0, BlockKind.BREAK)
        );

        DayPlan plan = DayPlan.builder()
            .userId(userId)
            .day(testDay)
            .blocks(blocks)
            .build();

        // When/Then
        assertThatCode(() -> validator.validateDayPlan(profile, plan))
            .doesNotThrowAnyException();
    }

    @Test
    @DisplayName("Should reject plan with overlapping blocks")
    void shouldRejectOverlappingBlocks() {
        // Given
        List<Block> blocks = List.of(
            createBlock(9, 0, 10, 0, BlockKind.STUDY),
            createBlock(9, 30, 10, 30, BlockKind.STUDY) // Overlaps with first
        );

        DayPlan plan = DayPlan.builder()
            .userId(userId)
            .day(testDay)
            .blocks(blocks)
            .build();

        // When/Then
        assertThatThrownBy(() -> validator.validateDayPlan(profile, plan))
            .isInstanceOf(PlanValidationException.class)
            .hasMessage("Blocks overlap.");
    }

    @Test
    @DisplayName("Should reject block starting before wake time")
    void shouldRejectBlockBeforeWakeTime() {
        // Given
        List<Block> blocks = List.of(
            createBlock(6, 0, 7, 0, BlockKind.STUDY) // Before 7:00 wake time
        );

        DayPlan plan = DayPlan.builder()
            .userId(userId)
            .day(testDay)
            .blocks(blocks)
            .build();

        // When/Then
        assertThatThrownBy(() -> validator.validateDayPlan(profile, plan))
            .isInstanceOf(PlanValidationException.class)
            .hasMessage("Block outside planning window.");
    }

    @Test
    @DisplayName("Should reject block ending after sleep time")
    void shouldRejectBlockAfterSleepTime() {
        // Given
        List<Block> blocks = List.of(
            createBlock(22, 0, 23, 30, BlockKind.STUDY) // Past 23:00 sleep time
        );

        DayPlan plan = DayPlan.builder()
            .userId(userId)
            .day(testDay)
            .blocks(blocks)
            .build();

        // When/Then
        assertThatThrownBy(() -> validator.validateDayPlan(profile, plan))
            .isInstanceOf(PlanValidationException.class)
            .hasMessage("Block outside planning window.");
    }

    @Test
    @DisplayName("Should reject block with negative duration")
    void shouldRejectNegativeDuration() {
        // Given
        LocalDateTime start = LocalDateTime.of(testDay, LocalTime.of(10, 0));
        LocalDateTime end = LocalDateTime.of(testDay, LocalTime.of(9, 0));

        Block invalidBlock = Block.builder()
            .userId(userId)
            .kind(BlockKind.STUDY)
            .title("Invalid Block")
            .startsAt(start)
            .endsAt(end)
            .build();

        DayPlan plan = DayPlan.builder()
            .userId(userId)
            .day(testDay)
            .blocks(List.of(invalidBlock))
            .build();

        // When/Then
        assertThatThrownBy(() -> validator.validateDayPlan(profile, plan))
            .isInstanceOf(PlanValidationException.class)
            .hasMessage("Block duration must be positive.");
    }

    @Test
    @DisplayName("Should reject plan exceeding max heavy blocks")
    void shouldRejectExcessHeavyBlocks() {
        // Given - 4 heavy blocks when max is 3
        List<Block> blocks = List.of(
            createBlock(8, 0, 9, 0, BlockKind.STUDY),
            createBlock(9, 0, 10, 0, BlockKind.STUDY),
            createBlock(10, 0, 11, 0, BlockKind.TASK),
            createBlock(11, 0, 12, 0, BlockKind.PROGRAM)
        );

        DayPlan plan = DayPlan.builder()
            .userId(userId)
            .day(testDay)
            .blocks(blocks)
            .build();

        // When/Then
        assertThatThrownBy(() -> validator.validateDayPlan(profile, plan))
            .isInstanceOf(PlanValidationException.class)
            .hasMessage("Exceeded max heavy blocks per day.");
    }

    @Test
    @DisplayName("Should reject plan exceeding max flexible minutes")
    void shouldRejectExcessFlexibleMinutes() {
        // Given - 360 flexible minutes when max is 300
        List<Block> blocks = List.of(
            createBlock(8, 0, 10, 0, BlockKind.STUDY),   // 120 min
            createBlock(10, 0, 12, 0, BlockKind.STUDY),  // 120 min
            createBlock(14, 0, 16, 0, BlockKind.STUDY)   // 120 min
            // Total: 360 minutes > 300 max
        );

        DayPlan plan = DayPlan.builder()
            .userId(userId)
            .day(testDay)
            .blocks(blocks)
            .build();

        // When/Then
        assertThatThrownBy(() -> validator.validateDayPlan(profile, plan))
            .isInstanceOf(PlanValidationException.class)
            .hasMessage("Exceeded max flexible planned minutes per day.");
    }

    @Test
    @DisplayName("Should allow non-flexible blocks without counting against limit")
    void shouldNotCountNonFlexibleBlocks() {
        // Given - Work blocks don't count as flexible
        List<Block> blocks = List.of(
            createBlock(8, 0, 12, 0, BlockKind.WORK),    // 240 min - not flexible
            createBlock(14, 0, 18, 0, BlockKind.CLASS),  // 240 min - not flexible
            createBlock(19, 0, 20, 0, BlockKind.STUDY)   // 60 min - flexible
        );

        DayPlan plan = DayPlan.builder()
            .userId(userId)
            .day(testDay)
            .blocks(blocks)
            .build();

        // When/Then - Should pass because only 60 flexible minutes
        assertThatCode(() -> validator.validateDayPlan(profile, plan))
            .doesNotThrowAnyException();
    }

    @Test
    @DisplayName("Should validate empty plan")
    void shouldValidateEmptyPlan() {
        // Given
        DayPlan plan = DayPlan.builder()
            .userId(userId)
            .day(testDay)
            .blocks(List.of())
            .build();

        // When/Then
        assertThatCode(() -> validator.validateDayPlan(profile, plan))
            .doesNotThrowAnyException();
    }

    @Test
    @DisplayName("Should validate adjacent blocks without overlap")
    void shouldValidateAdjacentBlocks() {
        // Given - Blocks that touch but don't overlap
        List<Block> blocks = List.of(
            createBlock(9, 0, 10, 0, BlockKind.STUDY),
            createBlock(10, 0, 11, 0, BlockKind.BREAK), // Starts exactly when previous ends
            createBlock(11, 0, 12, 0, BlockKind.STUDY)
        );

        DayPlan plan = DayPlan.builder()
            .userId(userId)
            .day(testDay)
            .blocks(blocks)
            .build();

        // When/Then
        assertThatCode(() -> validator.validateDayPlan(profile, plan))
            .doesNotThrowAnyException();
    }

    @Test
    @DisplayName("Should count heavy blocks correctly")
    void shouldCountHeavyBlocksCorrectly() {
        // Given - Exactly 3 heavy blocks (at the limit)
        List<Block> blocks = List.of(
            createBlock(8, 0, 9, 0, BlockKind.STUDY),    // Heavy
            createBlock(9, 0, 10, 0, BlockKind.TASK),    // Heavy
            createBlock(10, 0, 10, 30, BlockKind.BREAK), // Not heavy
            createBlock(11, 0, 12, 0, BlockKind.PROGRAM) // Heavy
        );

        DayPlan plan = DayPlan.builder()
            .userId(userId)
            .day(testDay)
            .blocks(blocks)
            .build();

        // When/Then
        assertThatCode(() -> validator.validateDayPlan(profile, plan))
            .doesNotThrowAnyException();
    }

    // Helper method
    private Block createBlock(int startHour, int startMinute, int endHour, int endMinute, BlockKind kind) {
        LocalDateTime start = LocalDateTime.of(testDay, LocalTime.of(startHour, startMinute));
        LocalDateTime end = LocalDateTime.of(testDay, LocalTime.of(endHour, endMinute));

        return Block.builder()
            .userId(userId)
            .kind(kind)
            .title(kind.name())
            .startsAt(start)
            .endsAt(end)
            .build();
    }
}
