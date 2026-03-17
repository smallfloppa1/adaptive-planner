package com.floppahost.adaptiveplanner.planner.application.port.outbound.telegramuserrepository.dto;

import java.util.UUID;

public record TelegramUserDto(
        UUID userId,
        long telegramUserId,
        long chatId,
        ChatState state,
        String statePayload
) {
}
