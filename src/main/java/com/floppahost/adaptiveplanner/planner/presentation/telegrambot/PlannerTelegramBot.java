package com.floppahost.adaptiveplanner.planner.presentation.telegrambot;

import com.floppahost.adaptiveplanner.planner.application.port.inbound.handletelegramupdate.HandleTelegramUpdate;
import com.floppahost.adaptiveplanner.planner.application.port.inbound.handletelegramupdate.dto.IncomingUpdate;
import com.floppahost.adaptiveplanner.planner.application.port.inbound.handletelegramupdate.dto.OutgoingResponse;
import com.floppahost.adaptiveplanner.planner.presentation.telegrambot.config.TelegramBotProperties;
import com.floppahost.adaptiveplanner.planner.presentation.telegrambot.mapper.UpdateMapper;
import com.floppahost.adaptiveplanner.planner.presentation.telegrambot.view.ProfileViewFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.longpolling.BotSession;
import org.telegram.telegrambots.longpolling.interfaces.LongPollingUpdateConsumer;
import org.telegram.telegrambots.longpolling.starter.AfterBotRegistration;
import org.telegram.telegrambots.longpolling.starter.SpringLongPollingBot;
import org.telegram.telegrambots.longpolling.util.LongPollingSingleThreadUpdateConsumer;
import org.telegram.telegrambots.meta.api.methods.botapimethods.BotApiMethod;
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
    private final ProfileViewFactory profileViewFactory;

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
        IncomingUpdate input = UpdateMapper.toIncomingUpdate(update);
        if (input == null) return;

        OutgoingResponse out = handler.handle(input);
        if (out == null) return;

        // Use the factory to generate the UI based on the response
        BotApiMethod<?> apiMethod = profileViewFactory.buildView(out);

        if (apiMethod != null) {
            try {
                // This executes either the SendMessage or EditMessageText!
                telegramClient.execute(apiMethod);
            } catch (TelegramApiException e) {
                log.error("Failed to execute Telegram method", e);
            }
        }
    }

    @AfterBotRegistration
    public void afterRegistration(BotSession session) {
        log.info("Telegram bot running: {}", session.isRunning());
    }

}
