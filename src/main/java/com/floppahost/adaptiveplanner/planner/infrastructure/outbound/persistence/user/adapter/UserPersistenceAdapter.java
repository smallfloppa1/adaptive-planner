package com.floppahost.adaptiveplanner.planner.infrastructure.outbound.persistence.user.adapter;

import com.floppahost.adaptiveplanner.planner.application.port.outbound.userrepository.UserRepository;
import com.floppahost.adaptiveplanner.planner.domain.model.User;
import com.floppahost.adaptiveplanner.planner.infrastructure.outbound.persistence.user.entity.UserEntity;
import com.floppahost.adaptiveplanner.planner.infrastructure.outbound.persistence.user.mapper.UserMapper;
import com.floppahost.adaptiveplanner.planner.infrastructure.outbound.persistence.user.repository.UserJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class UserPersistenceAdapter implements UserRepository {

    private final UserJpaRepository repository;

    @Override
    public Optional<User> findById(UUID id) {
        return repository.findById(id)
                .map(UserMapper::toDomain);
    }

    @Override
    @Transactional
    public User save(User user) {
        UserEntity entity = UserMapper.toEntity(user);
        UserEntity saved = repository.save(entity);
        return UserMapper.toDomain(saved);
    }

    @Override
    @Transactional
    public void deleteById(UUID id) {
        repository.deleteById(id);
    }
}
