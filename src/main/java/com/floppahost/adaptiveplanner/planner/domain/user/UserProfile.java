package com.floppahost.adaptiveplanner.planner.domain.user;

import java.time.Duration;
import java.time.LocalTime;
import java.util.Objects;

public record UserProfile(
        LocalTime wakeTime,
        LocalTime sleepTime,
        double minSleepHours,

        // TODO: implement set up and logic of those 2 params
        int morningRoutineMinutes,
        int eveningRoutineMinutes,

        int focusMinutes,
        int breakMinutes,

        int maxHeavyBlocksPerDay,
        int maxTotalPlannedMinutesPerDay,
        int weeklyStudyTargetMinutes,

        boolean strictEnforcement
) {
    private static final LocalTime DEFAULT_WAKE_TIME = LocalTime.of(7, 0);
    private static final LocalTime DEFAULT_SLEEP_TIME = LocalTime.of(23, 0);
    private static final double DEFAULT_MIN_SLEEP_HOURS = 7.0;
    private static final int DEFAULT_MORNING_ROUTINE_MINUTES = 60;
    private static final int DEFAULT_EVENING_ROUTINE_MINUTES = 60;
    private static final int DEFAULT_FOCUS_MINUTES = 40;
    private static final int DEFAULT_BREAK_MINUTES = 10;
    private static final int DEFAULT_MAX_HEAVY_BLOCKS = 3;
    private static final int DEFAULT_MAX_TOTAL_PLANNED_MINUTES_PER_DAY = 8 * 60;
    private static final int DEFAULT_WEEKLY_STUDY_TARGET_MINUTES = 10 * 60;
    private static final boolean DEFAULT_STRICT_ENFORCEMENT = true;

    public UserProfile {
        Objects.requireNonNull(wakeTime, "Wake time cannot be null");
        Objects.requireNonNull(sleepTime, "Sleep time cannot be null");

        validateSleepWindow(wakeTime, sleepTime, minSleepHours);
        validateRoutines(wakeTime, sleepTime, morningRoutineMinutes, eveningRoutineMinutes);
        validateFocusCycle(focusMinutes, breakMinutes);
        validateConstraints(
                maxHeavyBlocksPerDay,
                maxTotalPlannedMinutesPerDay,
                weeklyStudyTargetMinutes
        );
    }

    public static UserProfile defaults() {
        return new UserProfile(
                DEFAULT_WAKE_TIME,
                DEFAULT_SLEEP_TIME,
                DEFAULT_MIN_SLEEP_HOURS,
                DEFAULT_MORNING_ROUTINE_MINUTES,
                DEFAULT_EVENING_ROUTINE_MINUTES,
                DEFAULT_FOCUS_MINUTES,
                DEFAULT_BREAK_MINUTES,
                DEFAULT_MAX_HEAVY_BLOCKS,
                DEFAULT_MAX_TOTAL_PLANNED_MINUTES_PER_DAY,
                DEFAULT_WEEKLY_STUDY_TARGET_MINUTES,
                DEFAULT_STRICT_ENFORCEMENT
        );
    }

    private static void validateSleepWindow(
            LocalTime wakeTime,
            LocalTime sleepTime,
            double minSleepHours
    ) {
        if (minSleepHours <= 0) {
            throw new IllegalArgumentException("Minimum sleep hours must be greater than zero");
        }

        long sleepMinutes = calculateSleepDuration(wakeTime, sleepTime).toMinutes();

        if (sleepMinutes <= 0) {
            throw new IllegalArgumentException("Sleep window must be greater than zero minutes");
        }

        if (sleepMinutes < minSleepHours * 60) {
            throw new IllegalArgumentException("Sleep window is shorter than the minimum required sleep duration");
        }
    }

    private static void validateRoutines(
            LocalTime wakeTime,
            LocalTime sleepTime,
            int morningRoutineMinutes,
            int eveningRoutineMinutes
    ) {
        if (morningRoutineMinutes < 0 || eveningRoutineMinutes < 0) {
            throw new IllegalArgumentException("Morning and evening routines cannot be negative");
        }

        Duration sleepDuration = calculateSleepDuration(wakeTime, sleepTime);
        Duration awakeDuration = Duration.ofDays(1).minus(sleepDuration);

        long awakeMinutes = awakeDuration.toMinutes();
        int allRoutinesMinutes = morningRoutineMinutes + eveningRoutineMinutes;

        if (allRoutinesMinutes >= awakeMinutes) {
            throw new IllegalArgumentException("Morning and evening routines combined cannot exceed total awake time");
        }
    }

    private static Duration calculateSleepDuration(LocalTime wakeTime, LocalTime sleepTime) {
        Duration duration = Duration.between(sleepTime, wakeTime);

        if (duration.isNegative()) {
            duration = duration.plusDays(1);
        }

        return duration;
    }

    private static void validateFocusCycle(int focusMinutes, int breakMinutes) {
        if (focusMinutes <= 0) {
            throw new IllegalArgumentException("Focus minutes must be greater than zero");
        }
        if (breakMinutes < 0) {
            throw new IllegalArgumentException("Break minutes cannot be negative");
        }
    }

    private static void validateConstraints(
            int maxHeavyBlocksPerDay,
            int maxTotalPlannedMinutesPerDay,
            int weeklyStudyTargetMinutes
    ) {
        if (maxHeavyBlocksPerDay < 0) {
            throw new IllegalArgumentException("Maximum heavy blocks per date cannot be negative");
        }
        if (maxTotalPlannedMinutesPerDay < 0) {
            throw new IllegalArgumentException("Maximum total planned minutes per date cannot be negative");
        }
        if (weeklyStudyTargetMinutes < 0) {
            throw new IllegalArgumentException("Weekly study target minutes cannot be negative");
        }
    }

    public UserProfile withSleepWindow(
            LocalTime wakeTime,
            LocalTime sleepTime,
            double minSleepHours
    ) {
        return new UserProfile(
                wakeTime, sleepTime, minSleepHours, morningRoutineMinutes,
                eveningRoutineMinutes, focusMinutes, breakMinutes,
                maxHeavyBlocksPerDay, maxTotalPlannedMinutesPerDay,
                weeklyStudyTargetMinutes, strictEnforcement
        );
    }

    public UserProfile withFocusCycle(int focusMinutes, int breakMinutes) {
        return new UserProfile(
                wakeTime, sleepTime, minSleepHours, morningRoutineMinutes,
                eveningRoutineMinutes, focusMinutes, breakMinutes,
                maxHeavyBlocksPerDay, maxTotalPlannedMinutesPerDay,
                weeklyStudyTargetMinutes, strictEnforcement
        );
    }

    public UserProfile withMaxHeavyBlocksPerDay(int maxHeavyBlocksPerDay) {
        return new UserProfile(
                wakeTime, sleepTime, minSleepHours, morningRoutineMinutes,
                eveningRoutineMinutes, focusMinutes, breakMinutes,
                maxHeavyBlocksPerDay, maxTotalPlannedMinutesPerDay,
                weeklyStudyTargetMinutes, strictEnforcement
        );
    }

    public UserProfile withMaxTotalPlannedMinutesPerDay(int maxTotalPlannedMinutesPerDay) {
        return new UserProfile(
                wakeTime, sleepTime, minSleepHours, morningRoutineMinutes,
                eveningRoutineMinutes, focusMinutes, breakMinutes,
                maxHeavyBlocksPerDay, maxTotalPlannedMinutesPerDay,
                weeklyStudyTargetMinutes, strictEnforcement
        );
    }

    public UserProfile withWeeklyStudyTargetMinutes(int weeklyStudyTargetMinutes) {
        return new UserProfile(
                wakeTime, sleepTime, minSleepHours, morningRoutineMinutes,
                eveningRoutineMinutes, focusMinutes, breakMinutes,
                maxHeavyBlocksPerDay, maxTotalPlannedMinutesPerDay,
                weeklyStudyTargetMinutes, strictEnforcement
        );
    }

    public UserProfile withToggledStrictEnforcement() {
        return new UserProfile(
                wakeTime, sleepTime, minSleepHours, morningRoutineMinutes,
                eveningRoutineMinutes, focusMinutes, breakMinutes,
                maxHeavyBlocksPerDay, maxTotalPlannedMinutesPerDay,
                weeklyStudyTargetMinutes, !this.strictEnforcement
        );
    }
}