package com.floppahost.adaptiveplanner.planner.domain.value;

import lombok.With;

import java.time.Duration;
import java.time.LocalTime;


public record UserProfile(
        LocalTime wakeTime,
        LocalTime sleepTime,
        double minSleepHours,

        // TODO: implement set up and logic of those 2 params
        int morningRoutineMinutes,
        int eveningRoutineMinutes,

        int focusMinutes,
        int breakMinutes,

        @With
        int maxHeavyBlocksPerDay,

        @With
        int maxTotalPlannedMinutesPerDay,

        @With
        int weeklyStudyTargetMinutes,

        boolean strictEnforcement
) {

    public UserProfile {
        if (wakeTime == null) throw new IllegalArgumentException("wakeTime must not be null when creating UserProfile");
        if (sleepTime == null) throw new IllegalArgumentException("sleepTime must not be null when creating UserProfile");

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
                LocalTime.of(7, 0),
                LocalTime.of(23, 0),
                7.0,
                60,
                60,
                40,
                10,
                3,
                8 * 60,
                10 * 60,
                true
        );
    }

    public UserProfile withSleepWindow(
            LocalTime wakeTime,
            LocalTime sleepTime,
            double minSleepHours
    ) {
        return new UserProfile(
                wakeTime,
                sleepTime,
                minSleepHours,
                morningRoutineMinutes,
                eveningRoutineMinutes,
                focusMinutes,
                breakMinutes,
                maxHeavyBlocksPerDay,
                maxTotalPlannedMinutesPerDay,
                weeklyStudyTargetMinutes,
                strictEnforcement
        );
    }

    public UserProfile withFocusCycle(int focusMinutes, int breakMinutes) {
        return new UserProfile(
                wakeTime,
                sleepTime,
                minSleepHours,
                morningRoutineMinutes,
                eveningRoutineMinutes,
                focusMinutes,
                breakMinutes,
                maxHeavyBlocksPerDay,
                maxTotalPlannedMinutesPerDay,
                weeklyStudyTargetMinutes,
                strictEnforcement
        );
    }

    public UserProfile withToggledStrictEnforcement() {
        boolean newStrictEnforcement = !this.strictEnforcement;

        return new UserProfile(
                wakeTime,
                sleepTime,
                minSleepHours,
                morningRoutineMinutes,
                eveningRoutineMinutes,
                focusMinutes,
                breakMinutes,
                maxHeavyBlocksPerDay,
                maxTotalPlannedMinutesPerDay,
                weeklyStudyTargetMinutes,
                newStrictEnforcement
        );
    }

    private static void validateSleepWindow(
            LocalTime wakeTime,
            LocalTime sleepTime,
            double minSleepHours
    ) {
        if (minSleepHours <= 0) {
            throw new IllegalArgumentException("Minimum sleep hours must be greater than 0");
        }

        long sleepMinutes = calculateSleepDuration(sleepTime, wakeTime).toMinutes();

        if (sleepMinutes <= 0) {
            throw new IllegalArgumentException("Sleep window must be greater than 0 minutes");
        }

        if (sleepMinutes < minSleepHours * 60) {
            throw new IllegalArgumentException("Sleep window is shorter than the minimum required sleep duration");
        }
    }

    private static void validateRoutines(
            LocalTime sleepTime,
            LocalTime wakeTime,
            int morningRoutineMinutes,
            int eveningRoutineMinutes
    ) {
        if (morningRoutineMinutes < 0 || eveningRoutineMinutes < 0) {
            throw new IllegalArgumentException("Routines can not be negative");
        }

        Duration sleepDuration = calculateSleepDuration(sleepTime, wakeTime);
        Duration awakeDuration = Duration.ofDays(1).minus(sleepDuration);

        long awakeMinutes = awakeDuration.toMinutes();
        int allRoutinesMinutes = morningRoutineMinutes + eveningRoutineMinutes;

        if (allRoutinesMinutes >= awakeMinutes) {
            throw new IllegalArgumentException("Morning and evening routines combined cannot exceed your total awake time");
        }
    }

    private static Duration calculateSleepDuration(LocalTime sleepTime, LocalTime wakeTime) {
        Duration duration = Duration.between(sleepTime, wakeTime);

        // If the duration is negative, the sleep period crossed midnight
        if (duration.isNegative()) {
            duration = duration.plusDays(1);
        }

        return duration;
    }

    private static void validateFocusCycle(int focusMinutes, int breakMinutes) {
        if (focusMinutes <= 0) {
            throw new IllegalArgumentException("Focus minutes must be greater than 0");
        }
        if (breakMinutes < 0) {
            throw new IllegalArgumentException("Break minutes must be non-negative");
        }
    }

    private static void validateConstraints(
            int maxHeavyBlocksPerDay,
            int maxTotalPlannedMinutesPerDay,
            int weeklyStudyTargetMinutes
    ) {
        if (maxHeavyBlocksPerDay <= 0) {
            throw new IllegalArgumentException("Maximal number of Heavy Blocks per Day must be greater than 0");
        }
        if (maxTotalPlannedMinutesPerDay <= 0) {
            throw new IllegalArgumentException("Maximum Total Planned Minutes per Day must be greater than 0");
        }
        if (weeklyStudyTargetMinutes < 0) {
            throw new IllegalArgumentException("Weekly Study Target Minutes must be non-negative");
        }
    }
}