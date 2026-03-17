package com.floppahost.adaptiveplanner.planner.domain.user;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalTime;
import java.util.Objects;
import java.util.UUID;


@Getter
public final class User {

    private final UUID id;
    private Email email;
    private UserProfile profile;

    private User(UUID id, Email email, UserProfile profile) {
        this.id = Objects.requireNonNull(id, "id");
        this.email = email;
        this.profile = Objects.requireNonNull(profile, "profile");
    }

    public static User rehydrate(UUID id, Email email, UserProfile profile) {
        return new User(id, email, profile);
    }

    public static User registerNew(Email email) {
        return new User(
                UUID.randomUUID(),
                email,
                UserProfile.defaults()
        );
    }

    public static User registerWithoutEmail() {
        return registerNew(null);
    }

    public void changeEmail(Email newEmail) {
        this.email = Objects.requireNonNull(newEmail, "New email cannot be null");
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
