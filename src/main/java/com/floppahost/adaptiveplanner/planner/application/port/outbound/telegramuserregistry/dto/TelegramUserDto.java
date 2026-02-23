package com.floppahost.adaptiveplanner.planner.application.port.outbound.telegramuserregistry.dto;

public record TelegramUserDto(
        long telegramUserId,
        long chatId
) {
}
