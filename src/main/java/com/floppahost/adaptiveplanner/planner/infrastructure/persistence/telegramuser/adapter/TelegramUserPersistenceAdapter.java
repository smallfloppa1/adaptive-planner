package com.floppahost.adaptiveplanner.planner.infrastructure.persistence.telegramuser.adapter;

import com.floppahost.adaptiveplanner.planner.application.port.outbound.telegram.LoadTelegramUserPort;
import com.floppahost.adaptiveplanner.planner.application.port.outbound.telegram.SaveTelegramUserPort;
import com.floppahost.adaptiveplanner.planner.domain.telegram.TelegramUser;
import com.floppahost.adaptiveplanner.planner.infrastructure.persistence.telegramuser.entity.TelegramUserEntity;
import com.floppahost.adaptiveplanner.planner.infrastructure.persistence.telegramuser.mapper.TelegramUserMapper;
import com.floppahost.adaptiveplanner.planner.infrastructure.persistence.telegramuser.repository.TelegramUserJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class TelegramUserPersistenceAdapter implements SaveTelegramUserPort, LoadTelegramUserPort {

    private final TelegramUserJpaRepository repository;
    private final TelegramUserMapper telegramUserMapper;

    @Override
    public Optional<TelegramUser> loadByTelegramId(long telegramUserId) {
        return repository.findByTelegramId(telegramUserId)
                .map(telegramUserMapper::toDomain);
    }

    @Override
    public void save(TelegramUser user) {
        TelegramUserEntity entity = telegramUserMapper.toEntity(user);

        repository.save(entity);
    }
}
