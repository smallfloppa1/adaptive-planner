package com.floppahost.adaptiveplanner.planner.infrastructure.outbound.persistence.telegram.adapter;

import com.floppahost.adaptiveplanner.planner.application.port.outbound.telegramuserrepository.dto.TelegramUserDto;
import com.floppahost.adaptiveplanner.planner.domain.model.User;
import com.floppahost.adaptiveplanner.planner.infrastructure.outbound.persistence.BaseIntegrationTest;
import com.floppahost.adaptiveplanner.planner.infrastructure.outbound.persistence.user.mapper.UserMapperImpl;
import com.floppahost.adaptiveplanner.planner.infrastructure.outbound.persistence.userprofile.mapper.UserProfileMapperImpl;
import com.floppahost.adaptiveplanner.planner.infrastructure.persistence.telegramuser.adapter.TelegramUserPersistenceAdapter;
import com.floppahost.adaptiveplanner.planner.infrastructure.persistence.telegramuser.repository.TelegramUserJpaRepository;
import com.floppahost.adaptiveplanner.planner.infrastructure.persistence.user.adapter.UserPersistenceAdapter;
import com.floppahost.adaptiveplanner.planner.infrastructure.persistence.user.repository.UserJpaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@Import({
        TelegramUserPersistenceAdapter.class,
        UserPersistenceAdapter.class,

        UserMapperImpl.class,
        UserProfileMapperImpl.class
})
@DisplayName("TelegramUserPersistenceAdapter Integration Tests")
class TelegramUserPersistenceAdapterTest extends BaseIntegrationTest {

    @Autowired
    private TelegramUserPersistenceAdapter telegramUserPersistenceAdapter;

    @Autowired
    private UserPersistenceAdapter userPersistenceAdapter;

    @Autowired
    private TelegramUserJpaRepository telegramUserRepository;

    @Autowired
    private UserJpaRepository userRepository;

    @BeforeEach
    void setUp() {
        telegramUserRepository.deleteAll();
        userRepository.deleteAll();
    }

    @Test
    @DisplayName("Should save a Telegram user")
    void shouldSaveTelegramUser() {
        // Given
        User user = User.registerWithoutEmail();
        userPersistenceAdapter.save(user);

        UUID userId = user.getId();
        TelegramUserDto userDto = new TelegramUserDto(userId, 12345L, 67890L);

        // When
        TelegramUserDto savedUser = telegramUserPersistenceAdapter.save(userDto);

        // Then
        assertThat(savedUser).isNotNull();
        assertThat(savedUser.userId()).isEqualTo(userId);
        assertThat(savedUser.telegramUserId()).isEqualTo(12345L);
        assertThat(savedUser.chatId()).isEqualTo(67890L);
    }

    @Test
    @DisplayName("Should find a Telegram user by Telegram user ID")
    void shouldFindByTelegramUserId() {
        // Given
        User user = User.registerWithoutEmail();
        userPersistenceAdapter.save(user);

        UUID userId = user.getId();

        TelegramUserDto userDto = new TelegramUserDto(userId, 12345L, 67890L);
        telegramUserPersistenceAdapter.save(userDto);

        // When
        Optional<TelegramUserDto> foundUser = telegramUserPersistenceAdapter.findByTelegramUserId(12345L);

        // Then
        assertThat(foundUser).isPresent();
        assertThat(foundUser.get().userId()).isEqualTo(userId);
    }

    @Test
    @DisplayName("Should return empty optional when user not found")
    void shouldReturnEmptyOptionalWhenUserNotFound() {
        // When
        Optional<TelegramUserDto> foundUser = telegramUserPersistenceAdapter.findByTelegramUserId(99999L);

        // Then
        assertThat(foundUser).isNotPresent();
    }

    @Test
    @DisplayName("Should return true when a Telegram user is present")
    void shouldReturnTrueWhenUserIsPresent() {
        // Given
        User user = User.registerWithoutEmail();
        userPersistenceAdapter.save(user);

        UUID userId = user.getId();

        TelegramUserDto userDto = new TelegramUserDto(userId, 12345L, 67890L);
        telegramUserPersistenceAdapter.save(userDto);

        // When
        boolean isPresent = telegramUserPersistenceAdapter.isPresentByTelegramUserId(12345L);

        // Then
        assertThat(isPresent).isTrue();
    }

    @Test
    @DisplayName("Should return false when a Telegram user is not present")
    void shouldReturnFalseWhenUserIsNotPresent() {
        // When
        boolean isPresent = telegramUserPersistenceAdapter.isPresentByTelegramUserId(99999L);

        // Then
        assertThat(isPresent).isFalse();
    }
}
