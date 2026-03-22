package com.floppahost.adaptiveplanner.planner.infrastructure.outbound.persistence.telegram.adapter;

import com.floppahost.adaptiveplanner.planner.domain.telegram.TelegramUser;
import com.floppahost.adaptiveplanner.planner.domain.user.User;
import com.floppahost.adaptiveplanner.planner.infrastructure.outbound.persistence.BaseIntegrationTest;
import com.floppahost.adaptiveplanner.planner.infrastructure.persistence.telegramuser.adapter.TelegramUserPersistenceAdapter;
import com.floppahost.adaptiveplanner.planner.infrastructure.persistence.telegramuser.repository.TelegramUserJpaRepository;
import com.floppahost.adaptiveplanner.planner.infrastructure.persistence.user.adapter.UserPersistenceAdapter;
import com.floppahost.adaptiveplanner.planner.infrastructure.persistence.user.mapper.UserMapperImpl;
import com.floppahost.adaptiveplanner.planner.infrastructure.persistence.user.mapper.UserProfileMapperImpl;
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
        TelegramUser telegramUser = TelegramUser.create(12345L, userId, 67890L);

        // When
        telegramUserPersistenceAdapter.save(telegramUser);
        TelegramUser savedUser = telegramUserPersistenceAdapter.loadByTelegramId(12345L).orElseThrow();

        // Then
        assertThat(savedUser).isNotNull();
        assertThat(savedUser.getUserId()).isEqualTo(userId);
        assertThat(savedUser.getTelegramId()).isEqualTo(12345L);
        assertThat(savedUser.getChatId()).isEqualTo(67890L);
    }

    @Test
    @DisplayName("Should find a Telegram user by Telegram user ID")
    void shouldFindByTelegramUserId() {
        // Given
        User user = User.registerWithoutEmail();
        userPersistenceAdapter.save(user);

        UUID userId = user.getId();

        TelegramUser telegramUser = TelegramUser.create(12345L, userId, 67890L);
        telegramUserPersistenceAdapter.save(telegramUser);

        // When
        Optional<TelegramUser> foundUser = telegramUserPersistenceAdapter.loadByTelegramId(12345L);

        // Then
        assertThat(foundUser).isPresent();
        assertThat(foundUser.get().getUserId()).isEqualTo(userId);
    }

    @Test
    @DisplayName("Should return empty optional when user not found")
    void shouldReturnEmptyOptionalWhenUserNotFound() {
        // When
        Optional<TelegramUser> foundUser = telegramUserPersistenceAdapter.loadByTelegramId(99999L);

        // Then
        assertThat(foundUser).isNotPresent();
    }
}
