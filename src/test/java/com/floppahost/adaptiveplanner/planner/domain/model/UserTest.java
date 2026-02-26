package com.floppahost.adaptiveplanner.planner.domain.model;

import org.junit.jupiter.api.DisplayName;

import com.floppahost.adaptiveplanner.planner.domain.value.Email;
import com.floppahost.adaptiveplanner.planner.domain.value.UserProfile;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("User Entity Tests")
class UserTest {

    private User user;

    @BeforeEach
    void setUp() {
        user = User.registerNew(new Email("test@example.com"));
    }

    @Test
    @DisplayName("Should register a new user with an email")
    void shouldRegisterNewUserWithEmail() {
        assertNotNull(user.id());
        assertEquals("test@example.com", user.email().value());
        assertTrue(user.isActive());
        assertNotNull(user.profile());
    }

    @Test
    @DisplayName("Should register a new user without an email")
    void shouldRegisterNewUserWithoutEmail() {
        User userWithoutEmail = User.registerWithoutEmail();
        assertNotNull(userWithoutEmail.id());
        assertNull(userWithoutEmail.email());
        assertTrue(userWithoutEmail.isActive());
        assertNotNull(userWithoutEmail.profile());
    }

    @Test
    @DisplayName("Should rehydrate a user from existing data")
    void shouldRehydrateUserFromExistingData() {
        UUID id = UUID.randomUUID();
        Email email = new Email("rehydrated@example.com");
        UserProfile profile = UserProfile.defaults();
        User rehydratedUser = User.rehydrate(id, email, false, profile);

        assertEquals(id, rehydratedUser.id());
        assertEquals(email, rehydratedUser.email());
        assertFalse(rehydratedUser.isActive());
        assertEquals(profile, rehydratedUser.profile());
    }

    @Test
    @DisplayName("Should set user's email")
    void shouldSetUsersEmail() {
        Email newEmail = new Email("new@example.com");
        user.setEmail(newEmail);
        assertEquals(newEmail, user.email());
    }

    @Test
    @DisplayName("Should clear user's email")
    void shouldClearUsersEmail() {
        user.clearEmail();
        assertNull(user.email());
    }

    @Test
    @DisplayName("Should deactivate and reactivate user")
    void shouldDeactivateAndReactivateUser() {
        user.deactivate();
        assertFalse(user.isActive());

        user.reactivate();
        assertTrue(user.isActive());
    }

    @Test
    @DisplayName("Should throw IllegalStateException when modifying an inactive user")
    void shouldThrowIllegalStateExceptionWhenModifyingInactiveUser() {
        user.deactivate();
        assertThrows(IllegalStateException.class, () -> user.setEmail(new Email("fail@example.com")));
        assertThrows(IllegalStateException.class, () -> user.clearEmail());
        assertThrows(IllegalStateException.class, () -> user.updateSleepWindow(LocalTime.now(), LocalTime.now(), 8));
        assertThrows(IllegalStateException.class, () -> user.updateFocusCycle(1, 1));
        assertThrows(IllegalStateException.class, () -> user.updatePlanningConstraints(1, 1, 1, true));
    }

    @Test
    @DisplayName("Should update user's sleep window in profile")
    void shouldUpdateUsersSleepWindowInProfile() {
        LocalTime newWakeTime = LocalTime.of(8, 0);
        LocalTime newSleepTime = LocalTime.of(0, 0);
        double newMinSleepHours = 8.0;

        user.updateSleepWindow(newWakeTime, newSleepTime, newMinSleepHours);

        UserProfile updatedProfile = user.profile();
        assertEquals(newWakeTime, updatedProfile.wakeTime());
        assertEquals(newSleepTime, updatedProfile.sleepTime());
        assertEquals(newMinSleepHours, updatedProfile.minSleepHours());
    }

    @Test
    @DisplayName("Should update user's focus cycle in profile")
    void shouldUpdateUsersFocusCycleInProfile() {
        int newFocusMinutes = 60;
        int newBreakMinutes = 20;

        user.updateFocusCycle(newFocusMinutes, newBreakMinutes);

        UserProfile updatedProfile = user.profile();
        assertEquals(newFocusMinutes, updatedProfile.focusMinutes());
        assertEquals(newBreakMinutes, updatedProfile.breakMinutes());
    }

    @Test
    @DisplayName("Should update user's planning constraints in profile")
    void shouldUpdateUsersPlanningConstraintsInProfile() {
        int newMaxHeavyBlocksPerDay = 4;
        int newMaxTotalPlannedMinutesPerDay = 9 * 60;
        int newWeeklyStudyTargetMinutes = 12 * 60;
        boolean newStrictEnforcement = false;

        user.updatePlanningConstraints(
                newMaxHeavyBlocksPerDay,
                newMaxTotalPlannedMinutesPerDay,
                newWeeklyStudyTargetMinutes,
                newStrictEnforcement
        );

        UserProfile updatedProfile = user.profile();
        assertEquals(newMaxHeavyBlocksPerDay, updatedProfile.maxHeavyBlocksPerDay());
        assertEquals(newMaxTotalPlannedMinutesPerDay, updatedProfile.maxTotalPlannedMinutesPerDay());
        assertEquals(newWeeklyStudyTargetMinutes, updatedProfile.weeklyStudyTargetMinutes());
        assertEquals(newStrictEnforcement, updatedProfile.strictEnforcement());
    }
}
