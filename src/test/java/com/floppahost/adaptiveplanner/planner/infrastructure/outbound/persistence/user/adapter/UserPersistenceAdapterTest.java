package com.floppahost.adaptiveplanner.planner.infrastructure.outbound.persistence.user.adapter;

import com.floppahost.adaptiveplanner.planner.domain.model.User;
import com.floppahost.adaptiveplanner.planner.domain.value.Email;
import com.floppahost.adaptiveplanner.planner.infrastructure.outbound.persistence.user.repository.UserJpaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import com.floppahost.adaptiveplanner.planner.infrastructure.outbound.persistence.BaseIntegrationTest;
import org.springframework.context.annotation.Import;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@Import(UserPersistenceAdapter.class)
@DisplayName("UserPersistenceAdapter Integration Tests")
class UserPersistenceAdapterTest extends BaseIntegrationTest {

    @Autowired
    private UserPersistenceAdapter adapter;

    @Autowired
    private UserJpaRepository repository;

    @BeforeEach
    void setUp() {
        repository.deleteAll();
    }

    @Test
    @DisplayName("Should save a user")
    void shouldSaveUser() {
        // Given
        User user = User.registerNew(new Email("test@example.com"));

        // When
        User savedUser = adapter.save(user);

        // Then
        assertThat(savedUser).isNotNull();
        assertThat(savedUser.id()).isEqualTo(user.id());
        assertThat(savedUser.email().value()).isEqualTo("test@example.com");
        assertThat(savedUser.isActive()).isTrue();
        assertThat(savedUser.profile()).isNotNull();
    }

    @Test
    @DisplayName("Should find a user by ID")
    void shouldFindUserById() {
        // Given
        User user = User.registerNew(new Email("test@example.com"));
        adapter.save(user);

        // When
        Optional<User> foundUser = adapter.findById(user.id());

        // Then
        assertThat(foundUser).isPresent();
        assertThat(foundUser.get().id()).isEqualTo(user.id());
    }

    @Test
    @DisplayName("Should return empty optional when user not found by ID")
    void shouldReturnEmptyOptionalWhenUserNotFoundById() {
        // When
        Optional<User> foundUser = adapter.findById(UUID.randomUUID());

        // Then
        assertThat(foundUser).isNotPresent();
    }

    @Test
    @DisplayName("Should delete a user by ID")
    void shouldDeleteUserById() {
        // Given
        User user = User.registerNew(new Email("test@example.com"));
        adapter.save(user);
        assertThat(repository.findById(user.id())).isPresent();


        // When
        adapter.deleteById(user.id());

        // Then
        assertThat(repository.findById(user.id())).isNotPresent();
    }
}
