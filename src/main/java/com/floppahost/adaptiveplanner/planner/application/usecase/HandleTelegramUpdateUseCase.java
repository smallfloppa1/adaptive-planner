package com.floppahost.adaptiveplanner.planner.application.usecase;

import com.floppahost.adaptiveplanner.planner.application.port.inbound.handletelegramupdate.HandleTelegramUpdate;
import com.floppahost.adaptiveplanner.planner.application.port.inbound.handletelegramupdate.dto.IncomingUpdate;
import com.floppahost.adaptiveplanner.planner.application.port.inbound.handletelegramupdate.dto.OutgoingResponse;
import com.floppahost.adaptiveplanner.planner.application.port.outbound.telegramuserrepository.TelegramUserRepository;
import com.floppahost.adaptiveplanner.planner.application.port.outbound.telegramuserrepository.dto.TelegramUserDto;
import com.floppahost.adaptiveplanner.planner.application.port.outbound.userrepository.UserRepository;
import com.floppahost.adaptiveplanner.planner.domain.model.User;
import com.floppahost.adaptiveplanner.planner.domain.value.UserProfile;
import com.floppahost.adaptiveplanner.planner.presentation.telegrambot.routing.BotRoute;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalTime;
import java.util.Arrays;
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

        // Intercept onboarding command
        if ("/start".equalsIgnoreCase(payload)) {
            return handleStart(input);
        }

        // Require registration for all other interactions
        Optional<TelegramUserDto> telegramUser = telegramUserRepository.findByTelegramUserId(input.userId());
        if (telegramUser.isEmpty()) {
            return new OutgoingResponse(input.chatId(), "UNREGISTERED_ERROR", null, null);
        }

        User user = domainUserRepository.findById(telegramUser.get().userId()).orElseThrow();

        // Route Callback Queries (Button Clicks)
        if (input.isCallback()) {
            Integer messageId = input.messageId();
            UserProfile currentProfile = user.getProfile();

            if (payload.startsWith(BotRoute.PREFIX_SET_WAKE.getPayload())) {
                String wakeTime = payload.replace(BotRoute.PREFIX_SET_WAKE.getPayload(), "");

                return new OutgoingResponse(input.chatId(), "VIEW_SETTINGS_SLEEP|" + wakeTime, user, messageId);
            }
            if (payload.startsWith(BotRoute.PREFIX_SET_SLEEP.getPayload())) {
                String data = payload.replace(BotRoute.PREFIX_SET_SLEEP.getPayload(), "");
                String[] parts = data.split("_"); // parts[0] is Wake, parts[1] is Sleep

                LocalTime newWake = LocalTime.parse(parts[0]);
                LocalTime newSleep = LocalTime.parse(parts[1]);

                user.updateSleepWindow(newWake, newSleep, currentProfile.minSleepHours());
                domainUserRepository.save(user);

                return new OutgoingResponse(input.chatId(), "VIEW_SETTINGS_MAIN", user, messageId);
            }
            if (payload.startsWith(BotRoute.PREFIX_SET_FOCUS.getPayload())) {
                String[] parts = payload.replace(BotRoute.PREFIX_SET_FOCUS.getPayload(), "").split("_");

                int focusMinutes = Integer.parseInt(parts[0]);
                int breakMinutes = Integer.parseInt(parts[1]);

                user.updateFocusCycle(focusMinutes, breakMinutes);
                domainUserRepository.save(user);

                return new OutgoingResponse(input.chatId(), "VIEW_SETTINGS_MAIN", user, messageId);
            }
            if (payload.startsWith(BotRoute.PREFIX_SET_HEAVY_CAP.getPayload())) {
                String cap = payload.replace(BotRoute.PREFIX_SET_HEAVY_CAP.getPayload(), "");

                int maxHeavyBlocks = Integer.parseInt(cap);

                user.updatePlanningConstraints();
                return new OutgoingResponse(input.chatId(), "VIEW_SETTINGS_CAPS", user, messageId); // Back to Caps menu
            }
            if (payload.startsWith(BotRoute.PREFIX_SET_DAILY_LOAD.getPayload())) {
                String max = payload.replace(BotRoute.PREFIX_SET_DAILY_LOAD.getPayload(), "");
                // TODO: Update domain -> maxDailyLoad = Integer.parseInt(max)
                return new OutgoingResponse(input.chatId(), "VIEW_SETTINGS_CAPS", user, messageId); // Back to Caps menu
            }
            if (payload.startsWith(BotRoute.PREFIX_SET_WEEKLY_TARGET.getPayload())) {
                String target = payload.replace(BotRoute.PREFIX_SET_WEEKLY_TARGET.getPayload(), "");
                // TODO: Update domain -> weeklyTarget = Integer.parseInt(target)
                return new OutgoingResponse(input.chatId(), "VIEW_SETTINGS_GOALS", user, messageId); // Back to Goals menu
            }
            if (BotRoute.TOGGLE_STRICT_ENFORCEMENT.getPayload().equals(payload)) {
                // TODO: Update domain -> toggle strict enforcement boolean
                // Reloads the exact same menu, but the factory will draw it with the new toggled state
                return new OutgoingResponse(input.chatId(), "VIEW_SETTINGS_GOALS", user, messageId);
            }


            BotRoute route = BotRoute.fromExactPayload(payload);
            if (route != null) {
                return handleMenuNavigation(route, user, input);
            }

        }

        return null;
    }

    private OutgoingResponse handleStart(IncomingUpdate input) {
        long telegramUserId = input.userId();
        User user;

        if (telegramUserRepository.isPresentByTelegramUserId(telegramUserId)) {
            TelegramUserDto tgUser = telegramUserRepository.findByTelegramUserId(telegramUserId).get();
            user = domainUserRepository.findById(tgUser.userId()).get();
        } else {
            user = User.registerWithoutEmail();
            domainUserRepository.save(user);

            TelegramUserDto telegramUser = new TelegramUserDto(user.getId(), telegramUserId, input.chatId());
            telegramUserRepository.save(telegramUser);
        }

        return new OutgoingResponse(input.chatId(), "VIEW_WELCOME", user, null);
    }

    private OutgoingResponse handleMenuNavigation(BotRoute route, User user, IncomingUpdate input) {
        Integer msgId = input.messageId();
        return switch (route) {
            case ADJUST_PROFILE -> new OutgoingResponse(input.chatId(), "VIEW_SETTINGS_MAIN", user, msgId);
            case BACK_TO_MAIN -> new OutgoingResponse(input.chatId(), "VIEW_WELCOME", user, msgId);
            case ACCEPT_DEFAULTS -> new OutgoingResponse(input.chatId(), "VIEW_ADD_FIXED_EVENTS", user, msgId);

            // Main Categories
            case MENU_WAKE_SLEEP -> new OutgoingResponse(input.chatId(), "VIEW_SETTINGS_WAKE", user, msgId);
            case MENU_FOCUS_BREAK -> new OutgoingResponse(input.chatId(), "VIEW_SETTINGS_FOCUS", user, msgId);
            case MENU_CAPS_LIMITS -> new OutgoingResponse(input.chatId(), "VIEW_SETTINGS_CAPS", user, msgId);
            case MENU_GOALS_RULES -> new OutgoingResponse(input.chatId(), "VIEW_SETTINGS_GOALS", user, msgId);

            // Sub-menus
            case MENU_EDIT_HEAVY_CAP -> new OutgoingResponse(input.chatId(), "VIEW_SETTINGS_HEAVY_CAP", user, msgId);
            case MENU_EDIT_MAX_DAILY -> new OutgoingResponse(input.chatId(), "VIEW_SETTINGS_MAX_DAILY", user, msgId);
            case MENU_EDIT_WEEKLY_TARGET -> new OutgoingResponse(input.chatId(), "VIEW_SETTINGS_WEEKLY_TARGET", user, msgId);

            default -> null;
        };
    }
}
