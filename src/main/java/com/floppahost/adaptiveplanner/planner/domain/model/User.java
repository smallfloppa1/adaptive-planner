package com.floppahost.adaptiveplanner.planner.domain.model;

import com.floppahost.adaptiveplanner.planner.domain.value.Email;
import com.floppahost.adaptiveplanner.planner.domain.value.UserProfile;

import java.time.LocalTime;
import java.util.Objects;
import java.util.UUID;


public final class User {

    private final UUID id;
    private Email email;
    private boolean isActive;
    private UserProfile profile;

    private User(UUID id, Email email, boolean isActive, UserProfile profile) {
        this.id = Objects.requireNonNull(id, "id");
        this.email = email; // nullable
        this.isActive = isActive;
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

    public static User rehydrate(
            UUID id,
            Email email,
            boolean isActive,
            UserProfile profile
    ) {
        return new User(id, email, isActive, profile);
    }

    public UUID id() {
        return id;
    }

    public Email email() {
        return email;
    }

    public boolean isActive() {
        return isActive;
    }

    public UserProfile profile() {
        return profile;
    }

    public void setEmail(Email newEmail) {
        ensureActive();
        this.email = newEmail; // explicit domain permission
    }

    public void clearEmail() {
        ensureActive();
        this.email = null;
    }

    public void deactivate() {
        this.isActive = false;
    }

    public void reactivate() {
        this.isActive = true;
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

    public void updatePlanningConstraints(
            int maxHeavyBlocksPerDay,
            int maxTotalPlannedMinutesPerDay,
            int weeklyStudyTargetMinutes,
            boolean strictEnforcement
    ) {
        ensureActive();
        this.profile = this.profile.withPlanningConstraints(
                maxHeavyBlocksPerDay,
                maxTotalPlannedMinutesPerDay,
                weeklyStudyTargetMinutes,
                strictEnforcement
        );
    }

    private void ensureActive() {
        if (!isActive) {
            throw new IllegalStateException("User is inactive.");
        }
    }
}
