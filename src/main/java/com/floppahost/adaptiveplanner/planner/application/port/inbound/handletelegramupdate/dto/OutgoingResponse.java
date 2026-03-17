package com.floppahost.adaptiveplanner.planner.application.port.inbound.handletelegramupdate.dto;

import com.floppahost.adaptiveplanner.planner.domain.user.User;

public record OutgoingResponse(
        Long chatId,
        String viewName,     // Tells the bot WHICH UI to draw (e.g., "WELCOME_MENU")
        User user,           // Passes the domain user so the UI can display their settings
        Integer editMessageId // If not null, the bot edits this message instead of sending a new one
) {}