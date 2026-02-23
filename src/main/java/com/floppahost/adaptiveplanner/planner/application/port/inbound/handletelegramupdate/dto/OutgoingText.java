package com.floppahost.adaptiveplanner.planner.application.port.inbound.handletelegramupdate.dto;

public record OutgoingText(
        long chatId,
        String text
) {}