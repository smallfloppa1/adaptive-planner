package com.floppahost.adaptiveplanner.planner.infrastructure.outbound.persistence.telegram.adapter;

import com.floppahost.adaptiveplanner.planner.application.port.outbound.telegramuserregistry.TelegramUserRegistry;
import com.floppahost.adaptiveplanner.planner.application.port.outbound.telegramuserregistry.dto.TelegramUserDto;
import com.floppahost.adaptiveplanner.planner.infrastructure.outbound.persistence.telegram.entity.TelegramUserEntity;
import com.floppahost.adaptiveplanner.planner.infrastructure.outbound.persistence.telegram.mapper.TelegramUserMapper;
import com.floppahost.adaptiveplanner.planner.infrastructure.outbound.persistence.telegram.repository.TelegramUserJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class TelegramUserPersistenceAdapter implements TelegramUserRegistry {

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
}
