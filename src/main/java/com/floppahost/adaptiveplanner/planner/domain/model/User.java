package com.floppahost.adaptiveplanner.planner.domain.model;

import com.floppahost.adaptiveplanner.planner.domain.value.Email;
import com.floppahost.adaptiveplanner.planner.domain.value.UserProfile;
import lombok.Getter;

import java.time.LocalTime;
import java.util.Objects;
import java.util.UUID;


@Getter
public final class User {

    private final UUID id;
    private Email email;
    private boolean active;
    private UserProfile profile;

    public User(UUID id, Email email, boolean active, UserProfile profile) {
        this.id = Objects.requireNonNull(id, "id");
        this.email = email;
        this.active = active;
        this.profile = Objects.requireNonNull(profile, "profile");
    }

    public static User registerNew(Email email) {
        return new User(
                UUID.randomUUID(),
                email,
                true,
                UserProfile.defaults()
        );
    }

    public static User registerWithoutEmail() {
        return registerNew(null);
    }

    public void deactivate() {
        this.active = false;
    }

    public void reactivate() {
        this.active = true;
    }

    public void setEmail(Email newEmail) {
        ensureActive();
        this.email = newEmail; // explicit domain permission
    }

    public void clearEmail() {
        ensureActive();
        this.email = null;
    }

    public void updateSleepWindow(
            LocalTime wakeTime,
            LocalTime sleepTime,
            double minSleepHours
    ) {
        ensureActive();
        this.profile = this.profile.withSleepWindow(wakeTime, sleepTime, minSleepHours);
    }

    public void updateFocusCycle(int focusMinutes, int breakMinutes) {
        ensureActive();
        this.profile = this.profile.withFocusCycle(focusMinutes, breakMinutes);
    }

    public void updateMaxHeavyBlocksPerDay(int maxHeavyBlocksPerDay) {
        ensureActive();
        this.profile = this.profile.withMaxHeavyBlocksPerDay(maxHeavyBlocksPerDay);
    }

    public void updateMaxDailyLoadMinutes(int maxDailyLoadMinutes) {
        ensureActive();
        this.profile = this.profile.withMaxTotalPlannedMinutesPerDay(maxDailyLoadMinutes);
    }

    public void updateWeeklyTargetMinutes(int weeklyTargetMinutes) {
        ensureActive();
        this.profile = this.profile.withWeeklyStudyTargetMinutes(weeklyTargetMinutes);
    }

    public void toggleStrictEnforcement() {
        ensureActive();
        this.profile = this.profile.withToggledStrictEnforcement();
    }

    // Helper Methods

    private void ensureActive() {
        if (!active) {
            throw new IllegalStateException("User is inactive.");
        }
    }
}
