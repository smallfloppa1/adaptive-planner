package com.floppahost.adaptiveplanner.planner.application.port.inbound.handletelegramupdate.dto;

public record IncomingUpdate(
        Long chatId,
        Long userId,
        String payload,      // Either the typed text OR the hidden button data
        Integer messageId,   // Needed so we know which message to edit later
        boolean isCallback   // True if this came from a button click
) {
}
