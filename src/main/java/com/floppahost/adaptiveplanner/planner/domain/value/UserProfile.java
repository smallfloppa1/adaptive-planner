package com.floppahost.adaptiveplanner.planner.domain.value;

import java.time.Duration;
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
        boolean strictEnforcement
) {

    public UserProfile {
        if (wakeTime == null) throw new IllegalArgumentException("wakeTime must not be null when creating UserProfile");
        if (sleepTime == null) throw new IllegalArgumentException("sleepTime must not be null when creating UserProfile");

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
                focusMinutes,
                breakMinutes,
                maxHeavyBlocksPerDay,
                maxTotalPlannedMinutesPerDay,
                weeklyStudyTargetMinutes,
                strictEnforcement
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
                strictEnforcement
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

        long sleepMinutes = calculateSleepMinutes(sleepTime, wakeTime);

        // Catch scenarios where sleep and wake times are exactly the same
        if (sleepMinutes <= 0) {
            throw new IllegalArgumentException("Sleep window must be greater than 0 minutes");
        }

        // Implicitly promotes sleepMinutes to double for accurate comparison
        if (sleepMinutes < minSleepHours * 60) {
            throw new IllegalArgumentException("Sleep window is shorter than the minimum required sleep duration");
        }
    }

    private static long calculateSleepMinutes(LocalTime sleepTime, LocalTime wakeTime) {
        Duration duration = Duration.between(sleepTime, wakeTime);

        // If the duration is negative, the sleep period crossed midnight
        if (duration.isNegative()) {
            duration = duration.plusDays(1);
        }

        return duration.toMinutes();
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