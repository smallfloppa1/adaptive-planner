package com.floppahost.adaptiveplanner.planner.presentation.telegrambot.mapper;

import com.floppahost.adaptiveplanner.planner.application.port.inbound.handletelegramupdate.dto.IncomingUpdate;
import org.telegram.telegrambots.meta.api.objects.Update;

public final class UpdateMapper {

    public static IncomingUpdate toIncomingUpdate(Update update) {

        // Handle standard typed messages (e.g., "/start")
        if (update.hasMessage() && update.getMessage().hasText()) {
            var message = update.getMessage();
            Long userId = message.getFrom() != null ? message.getFrom().getId() : null;

            return new IncomingUpdate(
                    message.getChatId(),
                    userId,
                    message.getText(),
                    message.getMessageId(),
                    false
            );
        }

        // Handle button clicks (Callback Queries)
        if (update.hasCallbackQuery()) {
            var query = update.getCallbackQuery();

            return new IncomingUpdate(
                    query.getMessage().getChatId(),
                    query.getFrom().getId(),
                    query.getData(),
                    query.getMessage().getMessageId(),
                    true
            );
        }

        return null;
    }
}
