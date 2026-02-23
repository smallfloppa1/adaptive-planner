package com.floppahost.adaptiveplanner.planner.application.port.outbound.telegramuserregistry.dto;

import java.util.UUID;

public record TelegramUserDto(
        long telegramUserId,
        long chatId,
        UUID domainUserId
) {
}
