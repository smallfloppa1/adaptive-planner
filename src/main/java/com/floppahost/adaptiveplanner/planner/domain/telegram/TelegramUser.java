package com.floppahost.adaptiveplanner.planner.domain.telegram;

import lombok.Getter;

import java.util.Objects;
import java.util.UUID;

@Getter
public class TelegramUser {
    private final long telegramId;
    private final UUID userId;
    private final long chatId;
    private ChatState state;
    private String statePayload;

    private TelegramUser(long telegramId, UUID userId, long chatId, ChatState state, String statePayload) {
        this.telegramId = telegramId;
        this.userId = userId;
        this.chatId = chatId;
        this.state = state;
        this.statePayload = statePayload;
    }

    public static TelegramUser rehydrate(
            long telegramId,
            UUID userId,
            long chatId,
            ChatState state,
            String statePayload
    ) {
        return new TelegramUser(
                telegramId,
                userId,
                chatId,
                state,
                statePayload
        );
    }

    public static TelegramUser create(long telegramId, UUID userId, long chatId) {
        return new TelegramUser(telegramId, userId, chatId, ChatState.IDLE, null);
    }

    public void transitionTo(ChatState newState, String payload) {
        this.state = Objects.requireNonNull(newState);
        this.statePayload = payload;
    }

    public void reset() {
        this.state = ChatState.IDLE;
        this.statePayload = null;
    }
}
