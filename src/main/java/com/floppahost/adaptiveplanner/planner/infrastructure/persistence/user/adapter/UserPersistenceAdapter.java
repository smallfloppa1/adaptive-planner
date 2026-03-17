package com.floppahost.adaptiveplanner.planner.infrastructure.persistence.user.adapter;

import com.floppahost.adaptiveplanner.planner.application.port.outbound.user.DeleteUserPort;
import com.floppahost.adaptiveplanner.planner.application.port.outbound.user.LoadUserPort;
import com.floppahost.adaptiveplanner.planner.application.port.outbound.user.SaveUserPort;
import com.floppahost.adaptiveplanner.planner.domain.user.User;
import com.floppahost.adaptiveplanner.planner.infrastructure.persistence.user.entity.UserEntity;
import com.floppahost.adaptiveplanner.planner.infrastructure.persistence.user.mapper.UserMapper;
import com.floppahost.adaptiveplanner.planner.infrastructure.persistence.user.repository.UserJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class UserPersistenceAdapter implements LoadUserPort, SaveUserPort, DeleteUserPort {

    private final UserJpaRepository repository;
    private final UserMapper userMapper;

    @Override
    public Optional<User> loadById(UUID id) {
        return repository.findById(id)
                .map(userMapper::toDomain);
    }

    @Override
    @Transactional
    public User save(User user) {
        UserEntity entity;

        Optional<UserEntity> existing = repository.findById(user.getId());

        if (existing.isPresent()) {
            entity = existing.get();
            userMapper.updateEntityFromDomain(user, entity);
        } else {
            entity = userMapper.toEntity(user);
        }

        UserEntity saved = repository.save(entity);
        return userMapper.toDomain(saved);
    }

    @Override
    @Transactional
    public void deleteById(UUID id) {
        repository.deleteById(id);
    }
}
