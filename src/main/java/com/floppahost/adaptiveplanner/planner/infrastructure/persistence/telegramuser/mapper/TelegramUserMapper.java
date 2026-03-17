package com.floppahost.adaptiveplanner.planner.infrastructure.persistence.telegramuser.mapper;

import com.floppahost.adaptiveplanner.planner.application.port.outbound.telegramuserrepository.dto.TelegramUserDto;
import com.floppahost.adaptiveplanner.planner.infrastructure.persistence.telegramuser.entity.TelegramUserEntity;

public final class TelegramUserMapper {

    public static TelegramUserEntity toEntity(TelegramUserDto dto) {
        if (dto == null) return null;

        return new TelegramUserEntity(
                dto.telegramUserId(),
                dto.chatId(),
                dto.userId()
        );
    }

    public static TelegramUserDto toDto(TelegramUserEntity entity) {
        if (entity == null) return null;

        return new TelegramUserDto(
                entity.getUserId(),
                entity.getTelegramUserId(),
                entity.getChatId()
        );
    }
}
