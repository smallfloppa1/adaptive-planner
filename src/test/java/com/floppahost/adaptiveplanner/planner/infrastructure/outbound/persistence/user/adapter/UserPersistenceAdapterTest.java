package com.floppahost.adaptiveplanner.planner.infrastructure.outbound.persistence.user.adapter;

import com.floppahost.adaptiveplanner.planner.domain.user.User;
import com.floppahost.adaptiveplanner.planner.domain.user.Email;
import com.floppahost.adaptiveplanner.planner.infrastructure.outbound.persistence.BaseIntegrationTest;
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
        UserPersistenceAdapter.class,

        UserMapperImpl.class,
        UserProfileMapperImpl.class
})
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
        assertThat(savedUser.getId()).isEqualTo(user.getId());
        assertThat(savedUser.getEmail().value()).isEqualTo("test@example.com");
        assertThat(savedUser.getProfile()).isNotNull();
    }

    @Test
    @DisplayName("Should find a user by ID")
    void shouldFindUserById() {
        // Given
        User user = User.registerNew(new Email("test@example.com"));
        adapter.save(user);

        // When
        Optional<User> foundUser = adapter.loadById(user.getId());

        // Then
        assertThat(foundUser).isPresent();
        assertThat(foundUser.get().getId()).isEqualTo(user.getId());
    }

    @Test
    @DisplayName("Should return empty optional when user not found by ID")
    void shouldReturnEmptyOptionalWhenUserNotFoundById() {
        // When
        Optional<User> foundUser = adapter.loadById(UUID.randomUUID());

        // Then
        assertThat(foundUser).isNotPresent();
    }

    @Test
    @DisplayName("Should delete a user by ID")
    void shouldDeleteUserById() {
        // Given
        User user = User.registerNew(new Email("test@example.com"));
        adapter.save(user);
        assertThat(repository.findById(user.getId())).isPresent();

        // When
        adapter.deleteById(user.getId());

        // Then
        assertThat(repository.findById(user.getId())).isNotPresent();
    }
}
