package com.floppahost.adaptiveplanner.planner.domain.value;

import java.time.LocalTime;


public record UserProfile(
        LocalTime wakeTime,
        LocalTime sleepTime,
        double minSleepHours,
        int focusMinutes,
        int breakMinutes,
        int maxHeavyBlocksPerDay,
        int maxTotalPlannedMinutesPerDay,
        int weeklyStudyTargetMinutes,
        boolean strictEnforcement,
        ProfileSetupState setupState
) {

    public UserProfile {
        if (wakeTime == null) throw new IllegalArgumentException("wakeTime is required");
        if (sleepTime == null) throw new IllegalArgumentException("sleepTime is required");
        if (setupState == null) throw new IllegalArgumentException("setupState is required");

        validateSleepWindow(wakeTime, sleepTime, minSleepHours);
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
                40,
                10,
                3,
                8 * 60,
                10 * 60,
                true,
                ProfileSetupState.IN_PROGRESS
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
                focusMinutes,
                breakMinutes,
                maxHeavyBlocksPerDay,
                maxTotalPlannedMinutesPerDay,
                weeklyStudyTargetMinutes,
                strictEnforcement,
                setupState
        );
    }

    public UserProfile withFocusCycle(int focusMinutes, int breakMinutes) {
        return new UserProfile(
                wakeTime,
                sleepTime,
                minSleepHours,
                focusMinutes,
                breakMinutes,
                maxHeavyBlocksPerDay,
                maxTotalPlannedMinutesPerDay,
                weeklyStudyTargetMinutes,
                strictEnforcement,
                setupState
        );
    }

    public UserProfile withPlanningConstraints(
            int maxHeavyBlocksPerDay,
            int maxTotalPlannedMinutesPerDay,
            int weeklyStudyTargetMinutes,
            boolean strictEnforcement
    ) {
        return new UserProfile(
                wakeTime,
                sleepTime,
                minSleepHours,
                focusMinutes,
                breakMinutes,
                maxHeavyBlocksPerDay,
                maxTotalPlannedMinutesPerDay,
                weeklyStudyTargetMinutes,
                strictEnforcement,
                setupState
        );
    }

    public UserProfile markSetupCompleted() {
        return new UserProfile(
                wakeTime,
                sleepTime,
                minSleepHours,
                focusMinutes,
                breakMinutes,
                maxHeavyBlocksPerDay,
                maxTotalPlannedMinutesPerDay,
                weeklyStudyTargetMinutes,
                strictEnforcement,
                ProfileSetupState.COMPLETED
        );
    }

    private static void validateSleepWindow(
            LocalTime wakeTime,
            LocalTime sleepTime,
            double minSleepHours
    ) {
        if (!sleepTime.isAfter(wakeTime)) {
            throw new IllegalArgumentException(
                    "sleepTime must be after wakeTime for a valid planning window"
            );
        }
        if (minSleepHours <= 0) {
            throw new IllegalArgumentException("minSleepHours must be positive");
        }
    }

    private static void validateFocusCycle(int focusMinutes, int breakMinutes) {
        if (focusMinutes <= 0) {
            throw new IllegalArgumentException("focusMinutes must be positive");
        }
        if (breakMinutes < 0) {
            throw new IllegalArgumentException("breakMinutes must be non-negative");
        }
    }

    private static void validateConstraints(
            int maxHeavyBlocksPerDay,
            int maxTotalPlannedMinutesPerDay,
            int weeklyStudyTargetMinutes
    ) {
        if (maxHeavyBlocksPerDay <= 0) {
            throw new IllegalArgumentException("maxHeavyBlocksPerDay must be positive");
        }
        if (maxTotalPlannedMinutesPerDay <= 0) {
            throw new IllegalArgumentException("maxTotalPlannedMinutesPerDay must be positive");
        }
        if (weeklyStudyTargetMinutes < 0) {
            throw new IllegalArgumentException("weeklyStudyTargetMinutes must be non-negative");
        }
    }
}