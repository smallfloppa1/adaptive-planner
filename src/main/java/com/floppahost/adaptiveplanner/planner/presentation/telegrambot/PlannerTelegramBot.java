package com.floppahost.adaptiveplanner.planner.presentation.telegrambot;

import com.floppahost.adaptiveplanner.planner.application.port.inbound.handletelegramupdate.HandleTelegramUpdate;
import com.floppahost.adaptiveplanner.planner.application.port.inbound.handletelegramupdate.dto.IncomingText;
import com.floppahost.adaptiveplanner.planner.application.port.inbound.handletelegramupdate.dto.OutgoingText;
import com.floppahost.adaptiveplanner.planner.presentation.telegrambot.config.TelegramBotProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.longpolling.BotSession;
import org.telegram.telegrambots.longpolling.interfaces.LongPollingUpdateConsumer;
import org.telegram.telegrambots.longpolling.starter.AfterBotRegistration;
import org.telegram.telegrambots.longpolling.starter.SpringLongPollingBot;
import org.telegram.telegrambots.longpolling.util.LongPollingSingleThreadUpdateConsumer;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.generics.TelegramClient;

@Slf4j
@Component
@RequiredArgsConstructor
public class PlannerTelegramBot implements SpringLongPollingBot, LongPollingSingleThreadUpdateConsumer {

    private final TelegramBotProperties properties;
    private final HandleTelegramUpdate handler;
    private final TelegramClient telegramClient;

    @Override
    public String getBotToken() {
        return properties.apiKey();
    }

    @Override
    public LongPollingUpdateConsumer getUpdatesConsumer() {
        return this;
    }

    @Override
    public void consume(Update update) {
        IncomingText input = UpdateMapper.toIncomingText(update);
        if (input == null) return;

        OutgoingText out = handler.handle(input);
        if (out == null) return;

        SendMessage msg = SendMessage.builder()
                .chatId(Long.toString(out.chatId()))
                .text(out.text())
                .build();

        try {
            telegramClient.execute(msg);
        } catch (TelegramApiException e) {
            // log, don't crash polling thread
            log.error("Failed to consume a Telegram message", e);
        }
    }

    @AfterBotRegistration
    public void afterRegistration(BotSession session) {
        log.info("Telegram bot running: {}", session.isRunning());
    }

}
