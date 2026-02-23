package com.floppahost.adaptiveplanner.planner.application.port.inbound.handletelegramupdate.dto;

public record IncomingText(
        long chatId,
        Long userId,
        String text
) {
}
