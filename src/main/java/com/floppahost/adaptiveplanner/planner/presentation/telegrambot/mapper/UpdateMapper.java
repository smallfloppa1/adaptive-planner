package com.floppahost.adaptiveplanner.planner.presentation.telegrambot.mapper;

import com.floppahost.adaptiveplanner.planner.application.port.inbound.handletelegramupdate.dto.IncomingText;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.message.Message;

public final class UpdateMapper {

    private UpdateMapper() {

    }

    public static IncomingText toIncomingText(Update update) {
        if (update == null || !update.hasMessage()) return null;

        Message message = update.getMessage();
        if (message == null || !message.hasText()) return null;

        Long userId = message.getFrom() == null ? null : message.getFrom().getId();
        return new IncomingText(message.getChatId(), userId, message.getText());
    }
}
