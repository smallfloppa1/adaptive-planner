package com.floppahost.adaptiveplanner.planner.application.usecase;

import com.floppahost.adaptiveplanner.planner.application.port.inbound.handletelegramupdate.HandleTelegramUpdate;
import com.floppahost.adaptiveplanner.planner.application.port.inbound.handletelegramupdate.dto.IncomingUpdate;
import com.floppahost.adaptiveplanner.planner.application.port.inbound.handletelegramupdate.dto.OutgoingResponse;
import com.floppahost.adaptiveplanner.planner.application.port.outbound.telegramuserregistry.TelegramUserRepository;
import com.floppahost.adaptiveplanner.planner.application.port.outbound.telegramuserregistry.dto.TelegramUserDto;
import com.floppahost.adaptiveplanner.planner.application.port.outbound.userrepository.UserRepository;
import com.floppahost.adaptiveplanner.planner.domain.model.User;
import com.floppahost.adaptiveplanner.planner.presentation.telegrambot.routing.BotRoute;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class HandleTelegramUpdateUseCase implements HandleTelegramUpdate {

    private final TelegramUserRepository telegramUserRepository;
    private final UserRepository domainUserRepository;

    @Override
    public OutgoingResponse handle(IncomingUpdate input) {
        if (input == null || input.payload() == null || input.payload().isBlank()) return null;
        String payload = input.payload().trim();

        // 1. Intercept onboarding command
        if ("/start".equalsIgnoreCase(payload)) {
            return handleStart(input);
        }

        // 2. Require registration for all other interactions
        Optional<TelegramUserDto> tgUser = telegramUserRepository.findByTelegramUserId(input.userId());
        if (tgUser.isEmpty()) {
            return new OutgoingResponse(input.chatId(), "UNREGISTERED_ERROR", null, null);
        }

        // 3. Load the Domain User Aggregate
        User user = domainUserRepository.findById(tgUser.get().userId()).orElseThrow();

        // 4. Route Callback Queries (Button Clicks)
        if (input.isCallback()) {
            BotRoute route = BotRoute.fromExactPayload(payload);
            if (route != null) {
                return handleMenuNavigation(route, user, input);
            }

            // TODO: Handle dynamic prefixes here later (e.g., if payload.startsWith("WAKE_"))
        }

        return null; // Ignore unknown text for now
    }

    private OutgoingResponse handleStart(IncomingUpdate input) {
        long telegramUserId = input.userId();
        User user;

        // Ensure idempotency: Don't recreate if they just typed /start again
        if (telegramUserRepository.isPresentByTelegramUserId(telegramUserId)) {
            TelegramUserDto tgUser = telegramUserRepository.findByTelegramUserId(telegramUserId).get();
            user = domainUserRepository.findById(tgUser.userId()).get();
        } else {
            user = User.registerWithoutEmail();
            domainUserRepository.save(user);
            telegramUserRepository.save(new TelegramUserDto(user.id(), telegramUserId, input.chatId()));
        }

        return new OutgoingResponse(input.chatId(), "VIEW_WELCOME", user, null);
    }

    private OutgoingResponse handleMenuNavigation(BotRoute route, User user, IncomingUpdate input) {
        Integer messageId = input.messageId();

        return switch (route) {
            case ADJUST_PROFILE -> new OutgoingResponse(input.chatId(), "VIEW_SETTINGS_MAIN", user, messageId);
            case MENU_WAKE_SLEEP -> new OutgoingResponse(input.chatId(), "VIEW_SETTINGS_WAKE_SLEEP", user, messageId);
            case ACCEPT_DEFAULTS -> new OutgoingResponse(input.chatId(), "VIEW_ADD_FIXED_EVENTS", user, messageId);
            case BACK_TO_MAIN -> new OutgoingResponse(input.chatId(), "VIEW_WELCOME", user, messageId);
            default -> null; // Ignore unmapped routes
        };
    }
}
