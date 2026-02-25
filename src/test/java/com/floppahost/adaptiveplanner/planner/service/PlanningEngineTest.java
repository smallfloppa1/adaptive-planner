package com.floppahost.adaptiveplanner.planner.service;

import com.floppahost.adaptiveplanner.planner.domain.dto.PlanInputs;
import com.floppahost.adaptiveplanner.planner.domain.model.Block;
import com.floppahost.adaptiveplanner.planner.domain.model.DayPlan;
import com.floppahost.adaptiveplanner.planner.domain.model.FixedEvent;
import com.floppahost.adaptiveplanner.planner.domain.value.UserProfile;
import com.floppahost.adaptiveplanner.planner.domain.service.BlockAllocationService;
import com.floppahost.adaptiveplanner.planner.domain.service.PlanningEngine;
import com.floppahost.adaptiveplanner.planner.domain.service.ScheduleValidationService;
import com.floppahost.adaptiveplanner.planner.domain.service.TimeGridService;
import com.floppahost.adaptiveplanner.planner.domain.value.BlockKind;
import com.floppahost.adaptiveplanner.planner.domain.value.FixedEventKind;
import com.floppahost.adaptiveplanner.planner.domain.value.TimeRange;
import com.floppahost.adaptiveplanner.planner.domain.value.Weekday;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;

@DisplayName("PlanningEngine Tests")
class PlanningEngineTest {

    private PlanningEngine planningEngine;
    private UserProfile profile;
    private UUID userId;
    private LocalDate monday;
    private FixedEventKind defaultEventKind;

    @BeforeEach
    void setUp() {
        TimeGridService timeGridService = new TimeGridService();
        BlockAllocationService allocationService = new BlockAllocationService();
        ScheduleValidationService validationService = new ScheduleValidationService();

        planningEngine = new PlanningEngine(
                timeGridService,
                allocationService,
                validationService
        );

        userId = UUID.randomUUID();
        monday = LocalDate.of(2024, 2, 19);
        defaultEventKind = FixedEventKind.OTHER;

        profile = UserProfile.builder()
                .id(userId)
                .wakeTime(LocalTime.of(7, 0))
                .sleepTime(LocalTime.of(23, 0))
                .focusMinutes(40)
                .breakMinutes(10)
                .maxHeavyBlocksPerDay(6)
                .maxTotalPlannedMinutesPerDay(480)
                .build();
    }

    @Test
    @DisplayName("Should generate valid day plan with no fixed events")
    void shouldGeneratePlanWithNoFixedEvents() {
        PlanInputs inputs = new PlanInputs(
                userId,
                profile,
                monday,
                List.of(),
                120
        );

        DayPlan plan = planningEngine.generateDayPlan(inputs);

        assertThat(plan).isNotNull();
        assertThat(plan.getUserId()).isEqualTo(userId);
        assertThat(plan.getDay()).isEqualTo(monday);
        assertThat(plan.getBlocks()).isNotEmpty();

        assertThat(plan.getBlocks())
                .anyMatch(b -> b.getKind() == BlockKind.STUDY);
    }

    @Test
    @DisplayName("Should generate plan with fixed events and study blocks")
    void shouldGeneratePlanWithFixedEventsAndStudy() {
        FixedEvent morningClass = FixedEvent.builder()
                .userId(userId)
                .kind(FixedEventKind.CLASS)
                .title("Math Class")
                .weekday(Weekday.MONDAY)
                .recurringTimeRange(new TimeRange(
                        LocalTime.of(9, 0),
                        LocalTime.of(11, 0)
                ))
                .build();

        PlanInputs inputs = new PlanInputs(
                userId,
                profile,
                monday,
                List.of(morningClass),
                120
        );

        DayPlan plan = planningEngine.generateDayPlan(inputs);

        assertThat(plan.getBlocks()).hasSizeGreaterThan(1);

        assertThat(plan.getBlocks())
                .anyMatch(b -> b.getKind() == BlockKind.CLASS &&
                        b.getTitle().equals("Math Class"));

        assertThat(plan.getBlocks())
                .anyMatch(b -> b.getKind() == BlockKind.STUDY);
    }

    @Test
    @DisplayName("Should create sorted blocks by start time")
    void shouldCreateSortedBlocks() {
        FixedEvent afternoonEvent = FixedEvent.builder()
                .userId(userId)
                .kind(defaultEventKind)
                .title("Meeting")
                .weekday(Weekday.MONDAY)
                .recurringTimeRange(new TimeRange(
                        LocalTime.of(14, 0),
                        LocalTime.of(15, 0)
                ))
                .build();

        PlanInputs inputs = new PlanInputs(
                userId,
                profile,
                monday,
                List.of(afternoonEvent),
                120
        );

        DayPlan plan = planningEngine.generateDayPlan(inputs);

        List<LocalDateTime> startTimes = plan.getBlocks().stream()
                .map(Block::getStartsAt)
                .toList();

        for (int i = 0; i < startTimes.size() - 1; i++) {
            assertThat(startTimes.get(i))
                    .isBeforeOrEqualTo(startTimes.get(i + 1));
        }
    }

    @Test
    @DisplayName("Should validate generated plan")
    void shouldValidateGeneratedPlan() {
        PlanInputs inputs = new PlanInputs(
                userId,
                profile,
                monday,
                List.of(),
                200
        );

        assertThatCode(() -> planningEngine.generateDayPlan(inputs))
                .doesNotThrowAnyException();
    }

    @Test
    @DisplayName("Should handle multiple fixed events")
    void shouldHandleMultipleFixedEvents() {
        List<FixedEvent> events = List.of(
                createRecurringEvent(Weekday.MONDAY, "Morning Class", 9, 0, 10, 0),
                createRecurringEvent(Weekday.MONDAY, "Lunch", 12, 0, 13, 0),
                createRecurringEvent(Weekday.MONDAY, "Afternoon Class", 14, 0, 16, 0)
        );

        PlanInputs inputs = new PlanInputs(
                userId,
                profile,
                monday,
                events,
                120
        );

        DayPlan plan = planningEngine.generateDayPlan(inputs);

        assertThat(plan.getBlocks()).hasSizeGreaterThanOrEqualTo(3);

        assertThat(plan.getBlocks())
                .filteredOn(b -> b.getRef() != null && b.getRef().getFixedEventId() != null)
                .hasSize(3);
    }

    @Test
    @DisplayName("Should allocate study blocks in free time between fixed events")
    void shouldAllocateStudyInFreeTime() {
        // Fixed busy slots: 07:00-08:00 and 14:00-15:00
        List<FixedEvent> events = List.of(
                createRecurringEvent(Weekday.MONDAY, "Morning", 7, 0, 8, 0),
                createRecurringEvent(Weekday.MONDAY, "Afternoon", 14, 0, 15, 0)
        );

        PlanInputs inputs = new PlanInputs(
                userId,
                profile,
                monday,
                events,
                100
        );

        DayPlan plan = planningEngine.generateDayPlan(inputs);

        LocalDateTime busy1Start = LocalDateTime.of(monday, LocalTime.of(7, 0));
        LocalDateTime busy1End = LocalDateTime.of(monday, LocalTime.of(8, 0));
        LocalDateTime busy2Start = LocalDateTime.of(monday, LocalTime.of(14, 0));
        LocalDateTime busy2End = LocalDateTime.of(monday, LocalTime.of(15, 0));

        // Study blocks should not overlap fixed events
        assertThat(plan.getBlocks())
                .filteredOn(b -> b.getKind() == BlockKind.STUDY)
                .allMatch(b -> {
                    boolean overlapsBusy1 = b.getStartsAt().isBefore(busy1End) && b.getEndsAt().isAfter(busy1Start);
                    boolean overlapsBusy2 = b.getStartsAt().isBefore(busy2End) && b.getEndsAt().isAfter(busy2Start);
                    return !overlapsBusy1 && !overlapsBusy2;
                });

        // and should be inside the planning window
        LocalDateTime windowStart = LocalDateTime.of(monday, profile.getWakeTime());
        LocalDateTime windowEnd = LocalDateTime.of(monday, profile.getSleepTime());

        assertThat(plan.getBlocks())
                .filteredOn(b -> b.getKind() == BlockKind.STUDY)
                .allMatch(b -> !b.getStartsAt().isBefore(windowStart) && !b.getEndsAt().isAfter(windowEnd));
    }

    @Test
    @DisplayName("Should handle zero study target")
    void shouldHandleZeroStudyTarget() {
        PlanInputs inputs = new PlanInputs(
                userId,
                profile,
                monday,
                List.of(),
                0
        );

        DayPlan plan = planningEngine.generateDayPlan(inputs);

        assertThat(plan).isNotNull();
        assertThat(plan.getBlocks())
                .noneMatch(b -> b.getKind() == BlockKind.STUDY);
    }

    @Test
    @DisplayName("Should handle day completely filled with fixed events")
    void shouldHandleDayFullOfFixedEvents() {
        List<FixedEvent> events = List.of(
                createRecurringEvent(Weekday.MONDAY, "All Day Event", 7, 0, 23, 0)
        );

        PlanInputs inputs = new PlanInputs(
                userId,
                profile,
                monday,
                events,
                120
        );

        DayPlan plan = planningEngine.generateDayPlan(inputs);

        assertThat(plan.getBlocks()).hasSize(1);
        assertThat(plan.getBlocks().getFirst().getKind()).isEqualTo(BlockKind.OTHER);
    }

    @Test
    @DisplayName("Should respect max heavy blocks constraint")
    void shouldRespectMaxHeavyBlocks() {
        UserProfile restrictive = profile.withMaxHeavyBlocksPerDay(2);

        PlanInputs inputs = new PlanInputs(
                userId,
                restrictive,
                monday,
                List.of(),
                500
        );

        DayPlan plan = planningEngine.generateDayPlan(inputs);

        long heavyBlocks = plan.getBlocks().stream()
                .filter(b -> b.getKind() == BlockKind.STUDY ||
                        b.getKind() == BlockKind.TASK ||
                        b.getKind() == BlockKind.PROGRAM)
                .count();

        assertThat(heavyBlocks).isLessThanOrEqualTo(2);
    }

    @Test
    @DisplayName("Should respect max flexible minutes constraint")
    void shouldRespectMaxFlexibleMinutes() {
        UserProfile restrictive = profile.withMaxTotalPlannedMinutesPerDay(100);

        PlanInputs inputs = new PlanInputs(
                userId,
                restrictive,
                monday,
                List.of(),
                500
        );

        DayPlan plan = planningEngine.generateDayPlan(inputs);

        int flexibleMinutes = plan.getBlocks().stream()
                .filter(b -> b.getKind() == BlockKind.STUDY ||
                        b.getKind() == BlockKind.TASK ||
                        b.getKind() == BlockKind.PROGRAM ||
                        b.getKind() == BlockKind.BREAK)
                .mapToInt(Block::getPlannedMinutes)
                .sum();

        assertThat(flexibleMinutes).isLessThanOrEqualTo(100);
    }

    @Test
    @DisplayName("Should throw exception if generated plan is invalid")
    void shouldThrowIfPlanInvalid() {
        PlanInputs inputs = new PlanInputs(
                userId,
                profile,
                monday,
                List.of(),
                100
        );

        DayPlan plan = planningEngine.generateDayPlan(inputs);
        assertThat(plan).isNotNull();

        ScheduleValidationService validator = new ScheduleValidationService();
        assertThatCode(() -> validator.validateDayPlan(profile, plan))
                .doesNotThrowAnyException();
    }

    @Test
    @DisplayName("Should handle inactive fixed events")
    void shouldHandleInactiveFixedEvents() {
        FixedEvent inactiveEvent = FixedEvent.builder()
                .userId(userId)
                .kind(FixedEventKind.CLASS)
                .title("Cancelled Class")
                .weekday(Weekday.MONDAY)
                .recurringTimeRange(new TimeRange(LocalTime.of(10, 0), LocalTime.of(12, 0)))
                .active(false)
                .build();

        PlanInputs inputs = new PlanInputs(
                userId,
                profile,
                monday,
                List.of(inactiveEvent),
                100
        );

        DayPlan plan = planningEngine.generateDayPlan(inputs);

        assertThat(plan.getBlocks())
                .noneMatch(b -> b.getTitle().equals("Cancelled Class"));
    }

    @Test
    @DisplayName("Should create plan with correct metadata")
    void shouldCreatePlanWithCorrectMetadata() {
        PlanInputs inputs = new PlanInputs(
                userId,
                profile,
                monday,
                List.of(),
                100
        );

        DayPlan plan = planningEngine.generateDayPlan(inputs);

        assertThat(plan.getUserId()).isEqualTo(userId);
        assertThat(plan.getDay()).isEqualTo(monday);
        assertThat(plan.getId()).isNotNull();
    }

    private FixedEvent createRecurringEvent(
            Weekday weekday,
            String title,
            int startHour,
            int startMinute,
            int endHour,
            int endMinute
    ) {
        return FixedEvent.builder()
                .userId(userId)
                .kind(defaultEventKind)
                .title(title)
                .weekday(weekday)
                .recurringTimeRange(new TimeRange(
                        LocalTime.of(startHour, startMinute),
                        LocalTime.of(endHour, endMinute)
                ))
                .build();
    }
}
