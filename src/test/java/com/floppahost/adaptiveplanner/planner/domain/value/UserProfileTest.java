package com.floppahost.adaptiveplanner.planner.domain.value;

import org.junit.jupiter.api.DisplayName;

import org.junit.jupiter.api.Test;

import java.time.LocalTime;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("UserProfile Tests")
class UserProfileTest {

    @Test
    @DisplayName("Should return default UserProfile settings")
    void shouldReturnDefaultUserProfileSettings() {
        UserProfile defaultProfile = UserProfile.defaults();
        assertNotNull(defaultProfile);
        assertEquals(LocalTime.of(7, 0), defaultProfile.wakeTime());
        assertEquals(LocalTime.of(23, 0), defaultProfile.sleepTime());
        assertEquals(7.0, defaultProfile.minSleepHours());
        assertEquals(40, defaultProfile.focusMinutes());
        assertEquals(10, defaultProfile.breakMinutes());
        assertEquals(3, defaultProfile.maxHeavyBlocksPerDay());
        assertEquals(8 * 60, defaultProfile.maxTotalPlannedMinutesPerDay());
        assertEquals(10 * 60, defaultProfile.weeklyStudyTargetMinutes());
        assertTrue(defaultProfile.strictEnforcement());
    }

    @Test
    @DisplayName("Should create UserProfile with specified values")
    void shouldCreateUserProfileWithSpecifiedValues() {
        LocalTime wakeTime = LocalTime.of(6, 0);
        LocalTime sleepTime = LocalTime.of(22, 0);
        UserProfile profile = new UserProfile(
                wakeTime,
                sleepTime,
                8.0,
                50,
                15,
                2,
                7 * 60,
                20 * 60,
                false
        );
        assertEquals(wakeTime, profile.wakeTime());
        assertEquals(sleepTime, profile.sleepTime());
        assertEquals(8.0, profile.minSleepHours());
        assertEquals(50, profile.focusMinutes());
        assertEquals(15, profile.breakMinutes());
        assertEquals(2, profile.maxHeavyBlocksPerDay());
        assertEquals(7 * 60, profile.maxTotalPlannedMinutesPerDay());
        assertEquals(20 * 60, profile.weeklyStudyTargetMinutes());
        assertFalse(profile.strictEnforcement());
    }

    @Test
    @DisplayName("Should return new UserProfile with updated sleep window")
    void shouldReturnNewUserProfileWithUpdatedSleepWindow() {
        UserProfile initialProfile = UserProfile.defaults();
        LocalTime newWakeTime = LocalTime.of(8, 0);
        LocalTime newSleepTime = LocalTime.of(0, 0);
        double newMinSleepHours = 8.0;

        UserProfile updatedProfile = initialProfile.withSleepWindow(newWakeTime, newSleepTime, newMinSleepHours);

        assertEquals(newWakeTime, updatedProfile.wakeTime());
        assertEquals(newSleepTime, updatedProfile.sleepTime());
        assertEquals(newMinSleepHours, updatedProfile.minSleepHours());
        assertEquals(initialProfile.focusMinutes(), updatedProfile.focusMinutes());
    }

    @Test
    @DisplayName("Should return new UserProfile with updated focus cycle")
    void shouldReturnNewUserProfileWithUpdatedFocusCycle() {
        UserProfile initialProfile = UserProfile.defaults();
        int newFocusMinutes = 60;
        int newBreakMinutes = 20;

        UserProfile updatedProfile = initialProfile.withFocusCycle(newFocusMinutes, newBreakMinutes);

        assertEquals(newFocusMinutes, updatedProfile.focusMinutes());
        assertEquals(newBreakMinutes, updatedProfile.breakMinutes());
        assertEquals(initialProfile.wakeTime(), updatedProfile.wakeTime());
    }

    @Test
    @DisplayName("Should return new UserProfile with updated planning constraints")
    void shouldReturnNewUserProfileWithUpdatedPlanningConstraints() {
        UserProfile initialProfile = UserProfile.defaults();
        int newMaxHeavyBlocksPerDay = 4;
        int newMaxTotalPlannedMinutesPerDay = 9 * 60;
        int newWeeklyStudyTargetMinutes = 12 * 60;
        boolean newStrictEnforcement = false;

        UserProfile updatedProfile = initialProfile.withPlanningConstraints(
                newMaxHeavyBlocksPerDay,
                newMaxTotalPlannedMinutesPerDay,
                newWeeklyStudyTargetMinutes,
                newStrictEnforcement
        );

        assertEquals(newMaxHeavyBlocksPerDay, updatedProfile.maxHeavyBlocksPerDay());
        assertEquals(newMaxTotalPlannedMinutesPerDay, updatedProfile.maxTotalPlannedMinutesPerDay());
        assertEquals(newWeeklyStudyTargetMinutes, updatedProfile.weeklyStudyTargetMinutes());
        assertEquals(newStrictEnforcement, updatedProfile.strictEnforcement());
        assertEquals(initialProfile.wakeTime(), updatedProfile.wakeTime());
    }

    @Test
    @DisplayName("Should throw IllegalArgumentException for invalid sleep window configurations")
    void shouldThrowIllegalArgumentExceptionForInvalidSleepWindowConfigurations() {
        assertThrows(IllegalArgumentException.class, () ->
                new UserProfile(
                        LocalTime.of(7, 0),
                        LocalTime.of(23, 0),
                        0,
                        40,
                        10,
                        3,
                        8 * 60,
                        10 * 60,
                        true
                ), "Min sleep hours must be greater than 0"
        );

        assertThrows(IllegalArgumentException.class, () ->
                new UserProfile(
                        LocalTime.of(7, 0),
                        LocalTime.of(7, 0),
                        8.0,
                        40,
                        10,
                        3,
                        8 * 60,
                        10 * 60,
                        true
                ), "Sleep window must be greater than 0 minutes"
        );

        assertThrows(IllegalArgumentException.class, () ->
                new UserProfile(
                        LocalTime.of(8, 0),
                        LocalTime.of(7, 0),
                        2.0,
                        40,
                        10,
                        3,
                        8 * 60,
                        10 * 60,
                        true
                ), "Sleep window is shorter than the minimum required sleep duration"
        );
    }

    @Test
    @DisplayName("Should throw IllegalArgumentException for invalid focus cycle configurations")
    void shouldThrowIllegalArgumentExceptionForInvalidFocusCycleConfigurations() {
        assertThrows(IllegalArgumentException.class, () ->
                new UserProfile(
                        LocalTime.of(7, 0),
                        LocalTime.of(23, 0),
                        8.0,
                        0,
                        10,
                        3,
                        8 * 60,
                        10 * 60,
                        true
                ), "Focus minutes must be greater than 0"
        );

        assertThrows(IllegalArgumentException.class, () ->
                new UserProfile(
                        LocalTime.of(7, 0),
                        LocalTime.of(23, 0),
                        8.0,
                        40,
                        -1,
                        3,
                        8 * 60,
                        10 * 60,
                        true
                ), "Break minutes must be non-negative"
        );
    }

    @Test
    @DisplayName("Should throw IllegalArgumentException for invalid planning constraints configurations")
    void shouldThrowIllegalArgumentExceptionForInvalidPlanningConstraintsConfigurations() {
        assertThrows(IllegalArgumentException.class, () ->
                new UserProfile(
                        LocalTime.of(7, 0),
                        LocalTime.of(23, 0),
                        8.0,
                        40,
                        10,
                        0,
                        8 * 60,
                        10 * 60,
                        true
                ), "Maximal number of Heavy Blocks per Day must be greater than 0"
        );

        assertThrows(IllegalArgumentException.class, () ->
                new UserProfile(
                        LocalTime.of(7, 0),
                        LocalTime.of(23, 0),
                        8.0,
                        40,
                        10,
                        3,
                        0,
                        10 * 60,
                        true
                ), "Maximum Total Planned Minutes per Day must be greater than 0"
        );

        assertThrows(IllegalArgumentException.class, () ->
                new UserProfile(
                        LocalTime.of(7, 0),
                        LocalTime.of(23, 0),
                        8.0,
                        40,
                        10,
                        3,
                        8 * 60,
                        -1,
                        true
                ), "Weekly Study Target Minutes must be non-negative"
        );
    }
}
