package com.floppahost.adaptiveplanner.planner.application.port.outbound.telegramuserregistry.dto;

import java.util.UUID;

public record TelegramUserDto(
        UUID userId,
        long telegramUserId,
        long chatId
) {
}
