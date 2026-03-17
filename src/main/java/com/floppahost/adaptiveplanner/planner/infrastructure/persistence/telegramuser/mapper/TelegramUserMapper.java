package com.floppahost.adaptiveplanner.planner.infrastructure.persistence.telegramuser.mapper;

import com.floppahost.adaptiveplanner.planner.domain.telegram.TelegramUser;
import com.floppahost.adaptiveplanner.planner.infrastructure.persistence.telegramuser.entity.TelegramUserEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface TelegramUserMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    TelegramUserEntity toEntity(TelegramUser domain);

    default TelegramUser toDomain(TelegramUserEntity entity) {
        if (entity == null) return null;

        return TelegramUser.rehydrate(
                entity.getTelegramId(),
                entity.getUserId(),
                entity.getChatId(),
                entity.getState(),
                entity.getStatePayload()
        );
    }
}
