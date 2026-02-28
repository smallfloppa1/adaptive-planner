package com.floppahost.adaptiveplanner.planner.infrastructure.outbound.persistence.telegram.adapter;

import com.floppahost.adaptiveplanner.planner.application.port.outbound.telegramuserrepository.dto.TelegramUserDto;
import com.floppahost.adaptiveplanner.planner.infrastructure.outbound.persistence.telegram.repository.TelegramUserJpaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import com.floppahost.adaptiveplanner.planner.infrastructure.outbound.persistence.BaseIntegrationTest;
import org.springframework.context.annotation.Import;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@Import(TelegramUserPersistenceAdapter.class)
@DisplayName("TelegramUserPersistenceAdapter Integration Tests")
class TelegramUserPersistenceAdapterTest extends BaseIntegrationTest {

    @Autowired
    private TelegramUserPersistenceAdapter adapter;

    @Autowired
    private TelegramUserJpaRepository repository;

    @BeforeEach
    void setUp() {
        repository.deleteAll();
    }

    @Test
    @DisplayName("Should save a Telegram user")
    void shouldSaveTelegramUser() {
        // Given
        UUID userId = UUID.randomUUID();
        TelegramUserDto userDto = new TelegramUserDto(userId, 12345L, 67890L);

        // When
        TelegramUserDto savedUser = adapter.save(userDto);

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
        UUID userId = UUID.randomUUID();
        TelegramUserDto userDto = new TelegramUserDto(userId, 12345L, 67890L);
        adapter.save(userDto);

        // When
        Optional<TelegramUserDto> foundUser = adapter.findByTelegramUserId(12345L);

        // Then
        assertThat(foundUser).isPresent();
        assertThat(foundUser.get().userId()).isEqualTo(userId);
    }

    @Test
    @DisplayName("Should return empty optional when user not found")
    void shouldReturnEmptyOptionalWhenUserNotFound() {
        // When
        Optional<TelegramUserDto> foundUser = adapter.findByTelegramUserId(99999L);

        // Then
        assertThat(foundUser).isNotPresent();
    }

    @Test
    @DisplayName("Should return true when a Telegram user is present")
    void shouldReturnTrueWhenUserIsPresent() {
        // Given
        TelegramUserDto userDto = new TelegramUserDto(UUID.randomUUID(), 12345L, 67890L);
        adapter.save(userDto);

        // When
        boolean isPresent = adapter.isPresentByTelegramUserId(12345L);

        // Then
        assertThat(isPresent).isTrue();
    }

    @Test
    @DisplayName("Should return false when a Telegram user is not present")
    void shouldReturnFalseWhenUserIsNotPresent() {
        // When
        boolean isPresent = adapter.isPresentByTelegramUserId(99999L);

        // Then
        assertThat(isPresent).isFalse();
    }
}
