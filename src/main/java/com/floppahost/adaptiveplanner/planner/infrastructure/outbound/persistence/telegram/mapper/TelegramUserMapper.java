package com.floppahost.adaptiveplanner.planner.infrastructure.outbound.persistence.telegram.mapper;

import com.floppahost.adaptiveplanner.planner.application.port.outbound.telegramuserregistry.dto.TelegramUserDto;
import com.floppahost.adaptiveplanner.planner.infrastructure.outbound.persistence.telegram.entity.TelegramUserEntity;

public final class TelegramUserMapper {

    public static TelegramUserEntity toEntity(TelegramUserDto dto) {
        if (dto == null) return null;

        return new TelegramUserEntity(
                dto.telegramUserId(),
                dto.chatId(),
                dto.domainUserId()
        );
    }

    public static TelegramUserDto toDto(TelegramUserEntity entity) {
        if (entity == null) return null;

        return new TelegramUserDto(
                entity.getTelegramUserId(),
                entity.getChatId(),
                entity.getDomainId()
        );
    }
}
