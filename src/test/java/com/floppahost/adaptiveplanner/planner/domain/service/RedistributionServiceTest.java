package com.floppahost.adaptiveplanner.planner.domain.service;

import com.floppahost.adaptiveplanner.planner.domain.dto.RedistributionResult;
import com.floppahost.adaptiveplanner.planner.domain.model.Block;
import com.floppahost.adaptiveplanner.planner.domain.model.DayPlan;
import com.floppahost.adaptiveplanner.planner.domain.model.FixedEvent;
import com.floppahost.adaptiveplanner.planner.domain.value.UserProfile;
import com.floppahost.adaptiveplanner.planner.domain.service.PlanningEngine;
import com.floppahost.adaptiveplanner.planner.domain.service.RedistributionService;
import com.floppahost.adaptiveplanner.planner.domain.value.BlockKind;
import com.floppahost.adaptiveplanner.planner.domain.value.BlockStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@DisplayName("RedistributionService Tests")
class RedistributionServiceTest {

    private RedistributionService redistributionService;
    private PlanningEngine mockEngine;
    private UserProfile profile;
    private UUID userId;
    private LocalDate monday;

    @BeforeEach
    void setUp() {
        mockEngine = mock(PlanningEngine.class);
        redistributionService = new RedistributionService(mockEngine);

        userId = UUID.randomUUID();
        monday = LocalDate.of(2024, 2, 19); // Monday

        profile = new UserProfile(
                LocalTime.of(7, 0),
                LocalTime.of(23, 0),
                7.0,
                40,
                10,
                6,
                8 * 60,
                10 * 60,
                true
        );
    }

    @Test
    @DisplayName("Should calculate deficit correctly when behind target")
    void shouldCalculateDeficitWhenBehindTarget() {
        LocalDate thursday = monday.plusDays(3);
        int weeklyTarget = 600;

        Map<LocalDate, DayPlan> existingPlans = Map.of(
                monday, createPlanWithStudyMinutes(monday, 100),
                monday.plusDays(1), createPlanWithStudyMinutes(monday.plusDays(1), 50),
                monday.plusDays(2), createPlanWithStudyMinutes(monday.plusDays(2), 50)
        );

        when(mockEngine.generateDayPlan(any())).thenReturn(
                createPlanWithStudyMinutes(thursday, 100)
        );

        RedistributionResult result = redistributionService.redistributeWeekStudyMinutes(
                userId,
                profile,
                thursday,
                Map.of(),
                existingPlans,
                weeklyTarget
        );

        assertThat(result.deficitMinutes()).isEqualTo(400);
        assertThat(result.updatedPlans()).hasSize(4);
        verify(mockEngine, times(4)).generateDayPlan(any());
    }

    @Test
    @DisplayName("Should distribute deficit evenly across remaining days")
    void shouldDistributeDeficitEvenly() {
        LocalDate thursday = monday.plusDays(3);

        Map<LocalDate, DayPlan> existingPlans = Map.of(
                monday, createPlanWithStudyMinutes(monday, 100),
                monday.plusDays(1), createPlanWithStudyMinutes(monday.plusDays(1), 50),
                monday.plusDays(2), createPlanWithStudyMinutes(monday.plusDays(2), 50)
        );

        when(mockEngine.generateDayPlan(any())).thenReturn(
                createPlanWithStudyMinutes(thursday, 100)
        );

        redistributionService.redistributeWeekStudyMinutes(
                userId,
                profile,
                thursday,
                Map.of(),
                existingPlans,
                600
        );

        verify(mockEngine, times(4)).generateDayPlan(argThat(inputs ->
                inputs.targetStudyMinutes() == 100
        ));
    }

    @Test
    @DisplayName("Should handle remainder when deficit doesn't divide evenly")
    void shouldHandleRemainder() {
        LocalDate thursday = monday.plusDays(3);

        Map<LocalDate, DayPlan> existingPlans = Map.of(
                monday, createPlanWithStudyMinutes(monday, 100),
                monday.plusDays(1), createPlanWithStudyMinutes(monday.plusDays(1), 150)
        );

        when(mockEngine.generateDayPlan(any())).thenReturn(
                createPlanWithStudyMinutes(thursday, 60)
        );

        redistributionService.redistributeWeekStudyMinutes(
                userId,
                profile,
                thursday,
                Map.of(),
                existingPlans,
                500
        );

        verify(mockEngine).generateDayPlan(argThat(inputs ->
                inputs.targetStudyMinutes() == 63 && inputs.day().equals(thursday)
        ));
        verify(mockEngine).generateDayPlan(argThat(inputs ->
                inputs.targetStudyMinutes() == 63 && inputs.day().equals(thursday.plusDays(1))
        ));
        verify(mockEngine).generateDayPlan(argThat(inputs ->
                inputs.targetStudyMinutes() == 62 && inputs.day().equals(thursday.plusDays(2))
        ));
        verify(mockEngine).generateDayPlan(argThat(inputs ->
                inputs.targetStudyMinutes() == 62 && inputs.day().equals(thursday.plusDays(3))
        ));
    }

    @Test
    @DisplayName("Should return empty result when no deficit")
    void shouldReturnEmptyWhenNoDeficit() {
        LocalDate thursday = monday.plusDays(3);

        Map<LocalDate, DayPlan> existingPlans = Map.of(
                monday, createPlanWithStudyMinutes(monday, 200),
                monday.plusDays(1), createPlanWithStudyMinutes(monday.plusDays(1), 200),
                monday.plusDays(2), createPlanWithStudyMinutes(monday.plusDays(2), 200)
        );

        RedistributionResult result = redistributionService.redistributeWeekStudyMinutes(
                userId,
                profile,
                thursday,
                Map.of(),
                existingPlans,
                600
        );

        assertThat(result.deficitMinutes()).isEqualTo(0);
        assertThat(result.updatedPlans()).isEmpty();
        verify(mockEngine, never()).generateDayPlan(any());
    }

    @Test
    @DisplayName("Should return empty result when today is Sunday (no remaining days)")
    void shouldReturnEmptyOnSunday() {
        LocalDate sunday = monday.plusDays(6);

        Map<LocalDate, DayPlan> existingPlans = Map.of(
                monday, createPlanWithStudyMinutes(monday, 100)
        );

        RedistributionResult result = redistributionService.redistributeWeekStudyMinutes(
                userId,
                profile,
                sunday,
                Map.of(),
                existingPlans,
                600
        );

        assertThat(result.deficitMinutes()).isEqualTo(0);
        assertThat(result.updatedPlans()).isEmpty();
        verify(mockEngine, never()).generateDayPlan(any());
    }

    @Test
    @DisplayName("Should cap daily target at profile maximum")
    void shouldCapDailyTarget() {
        LocalDate thursday = monday.plusDays(3);
        UserProfile limited = profile.withPlanningConstraints(
                profile.maxHeavyBlocksPerDay(),
                100,
                profile.weeklyStudyTargetMinutes(),
                profile.strictEnforcement()
        );

        Map<LocalDate, DayPlan> existingPlans = Map.of(
                monday, createPlanWithStudyMinutes(monday, 50)
        );

        when(mockEngine.generateDayPlan(any())).thenReturn(
                createPlanWithStudyMinutes(thursday, 100)
        );

        redistributionService.redistributeWeekStudyMinutes(
                userId,
                limited,
                thursday,
                Map.of(),
                existingPlans,
                1000
        );

        verify(mockEngine, times(4)).generateDayPlan(argThat(inputs ->
                inputs.targetStudyMinutes() <= 100
        ));
    }

    @Test
    @DisplayName("Should only count completed study minutes")
    void shouldOnlyCountCompletedStudy() {
        LocalDate tuesday = monday.plusDays(1);

        Block completedBlock = createStudyBlock(monday, 9, 0, 10, 0, BlockStatus.DONE);
        Block plannedBlock = createStudyBlock(monday, 11, 0, 12, 0, BlockStatus.PLANNED);

        DayPlan mondayPlan = DayPlan.builder()
                .userId(userId)
                .day(monday)
                .blocks(List.of(completedBlock, plannedBlock))
                .build();

        Map<LocalDate, DayPlan> existingPlans = Map.of(monday, mondayPlan);

        when(mockEngine.generateDayPlan(any())).thenReturn(
                createPlanWithStudyMinutes(tuesday, 50)
        );

        RedistributionResult result = redistributionService.redistributeWeekStudyMinutes(
                userId,
                profile,
                tuesday,
                Map.of(),
                existingPlans,
                200
        );

        assertThat(result.deficitMinutes()).isEqualTo(140);
    }

    @Test
    @DisplayName("Should handle missing days in existing plans")
    void shouldHandleMissingDays() {
        LocalDate thursday = monday.plusDays(3);

        Map<LocalDate, DayPlan> existingPlans = Map.of(
                monday, createPlanWithStudyMinutes(monday, 100)
        );

        when(mockEngine.generateDayPlan(any())).thenReturn(
                createPlanWithStudyMinutes(thursday, 100)
        );

        RedistributionResult result = redistributionService.redistributeWeekStudyMinutes(
                userId,
                profile,
                thursday,
                Map.of(),
                existingPlans,
                600
        );

        assertThat(result.deficitMinutes()).isEqualTo(500);
    }

    @Test
    @DisplayName("Should generate plans for correct days")
    void shouldGeneratePlansForCorrectDays() {
        LocalDate wednesday = monday.plusDays(2);

        Map<LocalDate, DayPlan> existingPlans = Map.of(
                monday, createPlanWithStudyMinutes(monday, 100)
        );

        when(mockEngine.generateDayPlan(any())).thenReturn(
                createPlanWithStudyMinutes(wednesday, 100)
        );

        redistributionService.redistributeWeekStudyMinutes(
                userId,
                profile,
                wednesday,
                Map.of(),
                existingPlans,
                600
        );

        verify(mockEngine, times(5)).generateDayPlan(any());

        verify(mockEngine).generateDayPlan(argThat(inputs ->
                inputs.day().equals(wednesday)
        ));
        verify(mockEngine).generateDayPlan(argThat(inputs ->
                inputs.day().equals(wednesday.plusDays(1))
        ));
        verify(mockEngine).generateDayPlan(argThat(inputs ->
                inputs.day().equals(wednesday.plusDays(4)) // Sunday
        ));
    }

    @Test
    @DisplayName("Should use ISO week boundaries (Monday-Sunday)")
    void shouldUseIsoWeekBoundaries() {
        LocalDate weekTwoTuesday = monday.plusDays(8); // Feb 27
        LocalDate weekTwoMonday = monday.plusDays(7);

        Map<LocalDate, DayPlan> existingPlans = Map.of(
                weekTwoMonday, createPlanWithStudyMinutes(weekTwoMonday, 100)
        );

        when(mockEngine.generateDayPlan(any())).thenReturn(
                createPlanWithStudyMinutes(weekTwoTuesday, 100)
        );

        redistributionService.redistributeWeekStudyMinutes(
                userId,
                profile,
                weekTwoTuesday,
                Map.of(),
                existingPlans,
                600
        );

        verify(mockEngine, times(6)).generateDayPlan(any());
    }

    @Test
    @DisplayName("Should pass fixed events to planning engine")
    void shouldPassFixedEventsToEngine() {
        LocalDate thursday = monday.plusDays(3);
        FixedEvent event = mock(FixedEvent.class);

        Map<LocalDate, List<FixedEvent>> fixedEventsByDay = Map.of(
                thursday, List.of(event)
        );

        when(mockEngine.generateDayPlan(any())).thenReturn(
                createPlanWithStudyMinutes(thursday, 100)
        );

        redistributionService.redistributeWeekStudyMinutes(
                userId,
                profile,
                thursday,
                fixedEventsByDay,
                Map.of(),
                600
        );

        verify(mockEngine).generateDayPlan(argThat(inputs ->
                inputs.day().equals(thursday) &&
                        inputs.fixedEvents().contains(event)
        ));
    }

    @Test
    @DisplayName("Should handle empty fixed events map")
    void shouldHandleEmptyFixedEvents() {
        LocalDate thursday = monday.plusDays(3);

        when(mockEngine.generateDayPlan(any())).thenReturn(
                createPlanWithStudyMinutes(thursday, 100)
        );

        redistributionService.redistributeWeekStudyMinutes(
                userId,
                profile,
                thursday,
                Map.of(),
                Map.of(),
                600
        );

        // Thu..Sun = 4 calls, all should have empty fixedEvents
        verify(mockEngine, times(4)).generateDayPlan(argThat(inputs ->
                inputs.fixedEvents().isEmpty()
        ));
    }

    // -----------------------
    // Helpers (LocalDateTime)
    // -----------------------

    private DayPlan createPlanWithStudyMinutes(LocalDate day, int completedMinutes) {
        List<Block> blocks = new ArrayList<>();

        LocalDateTime cursor = day.atTime(9, 0);
        int remaining = completedMinutes;

        while (remaining > 0) {
            int duration = Math.min(60, remaining);
            LocalDateTime end = cursor.plusMinutes(duration);

            blocks.add(Block.builder()
                    .userId(userId)
                    .kind(BlockKind.STUDY)
                    .title("Study")
                    .startsAt(cursor)
                    .endsAt(end)
                    .status(BlockStatus.DONE)
                    .build());

            cursor = end;
            remaining -= duration;
        }

        return DayPlan.builder()
                .userId(userId)
                .day(day)
                .blocks(blocks)
                .build();
    }

    private Block createStudyBlock(
            LocalDate day, int startHour, int startMin,
            int endHour, int endMin, BlockStatus status
    ) {
        LocalDateTime start = LocalDateTime.of(day, LocalTime.of(startHour, startMin));
        LocalDateTime end = LocalDateTime.of(day, LocalTime.of(endHour, endMin));

        return Block.builder()
                .userId(userId)
                .kind(BlockKind.STUDY)
                .title("Study")
                .startsAt(start)
                .endsAt(end)
                .status(status)
                .build();
    }
}
