package com.floppahost.adaptiveplanner.planner.infrastructure.outbound.persistence.userprofile.mapper;

import com.floppahost.adaptiveplanner.planner.domain.value.UserProfile;
import com.floppahost.adaptiveplanner.planner.infrastructure.outbound.persistence.userprofile.entity.UserProfileEntity;

public final class UserProfileMapper {

    public static UserProfileEntity toEntity(UserProfile profile) {
        if (profile == null) {
            throw new IllegalArgumentException("UserProfile must not be null when mapping to UserProfileEntity");
        }

        return new UserProfileEntity(
                profile.wakeTime(),
                profile.sleepTime(),
                profile.minSleepHours(),
                profile.focusMinutes(),
                profile.breakMinutes(),
                profile.maxHeavyBlocksPerDay(),
                profile.maxTotalPlannedMinutesPerDay(),
                profile.weeklyStudyTargetMinutes(),
                profile.strictEnforcement()
        );
    }

    public static UserProfile toDomain(UserProfileEntity entity) {
        if (entity == null) {
            throw new IllegalArgumentException("UserProfileEntity must not be null when mapping to UserProfile");
        }

        return new UserProfile(
                entity.getWakeTime(),
                entity.getSleepTime(),
                entity.getMinSleepHours(),
                entity.getFocusMinutes(),
                entity.getBreakMinutes(),
                entity.getMaxHeavyBlocksPerDay(),
                entity.getMaxTotalPlannedMinutesPerDay(),
                entity.getWeeklyStudyTargetMinutes(),
                entity.isStrictEnforcement()
        );
    }
}
