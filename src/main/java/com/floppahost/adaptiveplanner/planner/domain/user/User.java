package com.floppahost.adaptiveplanner.planner.domain.user;

import lombok.Getter;

import java.time.LocalTime;
import java.util.Objects;
import java.util.UUID;


@Getter
public final class User {

    private final UUID id;
    private Email email;
    private UserProfile profile;

    private User(UUID id, Email email, UserProfile profile) {
        this.id = Objects.requireNonNull(id, "User ID can not be null");
        this.email = email;
        this.profile = Objects.requireNonNull(profile, "User profile can not be null");
    }

    public static User rehydrate(UUID id, Email email, UserProfile profile) {
        return new User(id, email, profile);
    }

    public static User registerNew(Email email) {
        UUID newUserId = UUID.randomUUID();
        UserProfile defaultUserProfile = UserProfile.defaults();

        return new User(
                newUserId,
                email,
                defaultUserProfile
        );
    }

    public static User registerWithoutEmail() {
        return registerNew(null);
    }

    public void updateEmail(Email newEmail) {
        this.email = Objects.requireNonNull(newEmail, "Email can not be null");
    }

    public void clearEmail() {
        this.email = null;
    }

    public void updateSleepWindow(
            LocalTime wakeTime,
            LocalTime sleepTime,
            double minSleepHours
    ) {
        this.profile = this.profile.withSleepWindow(wakeTime, sleepTime, minSleepHours);
    }

    public void updateFocusCycle(int focusMinutes, int breakMinutes) {
        this.profile = this.profile.withFocusCycle(focusMinutes, breakMinutes);
    }

    public void updateMaxHeavyBlocksPerDay(int maxHeavyBlocksPerDay) {
        this.profile = this.profile.withMaxHeavyBlocksPerDay(maxHeavyBlocksPerDay);
    }

    public void updateMaxDailyLoadMinutes(int maxDailyLoadMinutes) {
        this.profile = this.profile.withMaxTotalPlannedMinutesPerDay(maxDailyLoadMinutes);
    }

    public void updateWeeklyTargetMinutes(int weeklyTargetMinutes) {
        this.profile = this.profile.withWeeklyStudyTargetMinutes(weeklyTargetMinutes);
    }

    public void toggleStrictEnforcement() {
        this.profile = this.profile.withToggledStrictEnforcement();
    }
}
