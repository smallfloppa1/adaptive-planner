package com.floppahost.adaptiveplanner.planner.infrastructure.persistence.telegramuser.adapter;

import com.floppahost.adaptiveplanner.planner.application.port.outbound.telegramuserrepository.TelegramUserRepository;
import com.floppahost.adaptiveplanner.planner.application.port.outbound.telegramuserrepository.dto.TelegramUserDto;
import com.floppahost.adaptiveplanner.planner.infrastructure.persistence.telegramuser.entity.TelegramUserEntity;
import com.floppahost.adaptiveplanner.planner.infrastructure.persistence.telegramuser.mapper.TelegramUserMapper;
import com.floppahost.adaptiveplanner.planner.infrastructure.persistence.telegramuser.repository.TelegramUserJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class TelegramUserPersistenceAdapter implements TelegramUserRepository {

    private final TelegramUserJpaRepository repository;


    @Override
    public Optional<TelegramUserDto> findByTelegramUserId(long telegramUserId) {
        return repository.findByTelegramUserId(telegramUserId)
                .map(TelegramUserMapper::toDto);
    }

    @Override
    public TelegramUserDto save(TelegramUserDto user) {
        TelegramUserEntity entity = TelegramUserMapper.toEntity(user);

        TelegramUserEntity saved = repository.save(entity);

        return TelegramUserMapper.toDto(saved);
    }

    @Override
    public boolean isPresentByTelegramUserId(long telegramUserId) {
        return repository.existsByTelegramUserId((telegramUserId));
    }
}
