package com.floppahost.adaptiveplanner.planner.application.usecase;

import com.floppahost.adaptiveplanner.planner.application.port.inbound.handletelegramupdate.HandleTelegramUpdate;
import com.floppahost.adaptiveplanner.planner.application.port.inbound.handletelegramupdate.dto.IncomingUpdate;
import com.floppahost.adaptiveplanner.planner.application.port.inbound.handletelegramupdate.dto.OutgoingResponse;
import com.floppahost.adaptiveplanner.planner.application.port.outbound.telegram.LoadTelegramUserPort;
import com.floppahost.adaptiveplanner.planner.application.usecase.telegram.TelegramMenuService;
import com.floppahost.adaptiveplanner.planner.application.usecase.telegram.TelegramRegistrationService;
import com.floppahost.adaptiveplanner.planner.application.usecase.telegram.TelegramSettingsService;
import com.floppahost.adaptiveplanner.planner.application.usecase.telegram.TelegramStateService;
import com.floppahost.adaptiveplanner.planner.domain.telegram.ChatState;
import com.floppahost.adaptiveplanner.planner.application.port.outbound.user.LoadUserPort;
import com.floppahost.adaptiveplanner.planner.domain.telegram.TelegramUser;
import com.floppahost.adaptiveplanner.planner.domain.user.User;
import com.floppahost.adaptiveplanner.planner.presentation.telegrambot.routing.BotRoute;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class HandleTelegramUpdateUseCase implements HandleTelegramUpdate {

    private final LoadUserPort loadUserPort;

    private final LoadTelegramUserPort loadTelegramUserPort;

    private final TelegramRegistrationService registrationService;
    private final TelegramSettingsService settingsService;
    private final TelegramStateService stateService;
    private final TelegramMenuService menuService;

    @Override
    public OutgoingResponse handle(IncomingUpdate input) {
        if (input.payload() == null || input.payload().isBlank()) return null;
        String payload = input.payload().trim();

        if ("/start".equalsIgnoreCase(payload)) {
            return registrationService.handleStart(input);
        }

        Optional<TelegramUser> telegramUserOptional = loadTelegramUserPort.loadByTelegramId(input.userId());
        if (telegramUserOptional.isEmpty()) {
            return new OutgoingResponse(input.chatId(), "UNREGISTERED_ERROR", null, null);
        }

        TelegramUser telegramUser = telegramUserOptional.get();
        User user = loadUserPort.loadById(telegramUser.getUserId()).orElseThrow();

        if (input.isCallback()) {
            OutgoingResponse settingsResponse = settingsService.handleCallback(payload, telegramUser, user, input);
            if (settingsResponse != null) return settingsResponse;

            BotRoute route = BotRoute.fromExactPayload(payload);
            if (route != null) {
                return menuService.handleMenuNavigation(route, user, input);
            }
        } else {
            if (telegramUser.getState() != ChatState.IDLE) {
                return stateService.handleStatefulTextInput(input, payload, telegramUser, user);
            }
        }

        return null;
    }
}
