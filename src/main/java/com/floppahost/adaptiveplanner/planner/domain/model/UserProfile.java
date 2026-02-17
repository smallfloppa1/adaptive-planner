package com.floppahost.adaptiveplanner.planner.domain.model;

import lombok.Builder;
import lombok.Value;
import lombok.With;

import java.time.LocalTime;
import java.util.UUID;

@Value
@Builder
@With
public class UserProfile {
    UUID id; // 1:1, must match User.id

    @Builder.Default
    LocalTime wakeTime = LocalTime.of(7, 0);

    @Builder.Default
    LocalTime sleepTime = LocalTime.of(23, 0);

    @Builder.Default
    double sleepMinHours = 7.0;

    // Focus style
    @Builder.Default
    int focusMinutes = 40;

    @Builder.Default
    int breakMinutes = 10;

    // Load constraints
    @Builder.Default
    int maxHeavyBlocksPerDay = 3;

    @Builder.Default
    int maxTotalPlannedMinutesPerDay = 8 * 60; // cognitive/plan cap

    // Weekly targets (studies are mandatory, others optional)
    @Builder.Default
    int weeklyStudyTargetMinutes = 10 * 60;

    // Strictness preferences
    @Builder.Default
    boolean strictEnforcement = true;

    public UserProfile(
            UUID id,
            LocalTime wakeTime,
            LocalTime sleepTime,
            double sleepMinHours,
            int focusMinutes,
            int breakMinutes,
            int maxHeavyBlocksPerDay,
            int maxTotalPlannedMinutesPerDay,
            int weeklyStudyTargetMinutes,
            boolean strictEnforcement
    ) {
        
        if (sleepTime.isBefore(wakeTime) || sleepTime.equals(wakeTime)) {
            throw new IllegalArgumentException(
                "UserProfile: Sleep time must be after wake time for a valid planning window."
            );
        }
        if (focusMinutes <= 0) {
            throw new IllegalArgumentException("UserProfile: Focus minutes must be positive.");
        }
        if (breakMinutes < 0) {
            throw new IllegalArgumentException("UserProfile: Break minutes must be non-negative.");
        }
        if (maxHeavyBlocksPerDay <= 0) {
            throw new IllegalArgumentException("UserProfile: Max heavy blocks per day must be positive.");
        }
        if (weeklyStudyTargetMinutes < 0) {
            throw new IllegalArgumentException("UserProfile: Weekly study target minutes must be non-negative.");
        }

        this.id = id;
        this.wakeTime = wakeTime;
        this.sleepTime = sleepTime;
        this.sleepMinHours = sleepMinHours;
        this.focusMinutes = focusMinutes;
        this.breakMinutes = breakMinutes;
        this.maxHeavyBlocksPerDay = maxHeavyBlocksPerDay;
        this.maxTotalPlannedMinutesPerDay = maxTotalPlannedMinutesPerDay;
        this.weeklyStudyTargetMinutes = weeklyStudyTargetMinutes;
        this.strictEnforcement = strictEnforcement;
    }
}
