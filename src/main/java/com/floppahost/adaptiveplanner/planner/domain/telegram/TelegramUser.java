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
        if (telegramId < 0) throw new IllegalArgumentException("Telegram ID cannot be negative");
        if (chatId < 0) throw new IllegalArgumentException("Telegram chat ID cannot be negative");

        this.userId = Objects.requireNonNull(userId, "User ID cannot be null");
        this.state = Objects.requireNonNull(state, "Chat state cannot be null");
        this.telegramId = telegramId;
        this.chatId = chatId;
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
