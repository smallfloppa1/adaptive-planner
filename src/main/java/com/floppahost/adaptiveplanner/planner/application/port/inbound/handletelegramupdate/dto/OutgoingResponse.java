package com.floppahost.adaptiveplanner.planner.application.port.inbound.handletelegramupdate.dto;

import com.floppahost.adaptiveplanner.planner.domain.user.User;

public record OutgoingResponse(
        Long chatId,
        String viewName,
        User user,
        Integer editMessageId
) {}