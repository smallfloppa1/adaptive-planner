package com.floppahost.adaptiveplanner.planner.application.usecase;

import com.floppahost.adaptiveplanner.planner.application.port.inbound.handletelegramupdate.HandleTelegramUpdate;
import com.floppahost.adaptiveplanner.planner.application.port.inbound.handletelegramupdate.dto.IncomingUpdate;
import com.floppahost.adaptiveplanner.planner.application.port.inbound.handletelegramupdate.dto.OutgoingResponse;
import com.floppahost.adaptiveplanner.planner.application.port.outbound.telegramuserrepository.TelegramUserRepository;
import com.floppahost.adaptiveplanner.planner.application.port.outbound.telegramuserrepository.dto.ChatState;
import com.floppahost.adaptiveplanner.planner.application.port.outbound.telegramuserrepository.dto.TelegramUserDto;
import com.floppahost.adaptiveplanner.planner.application.port.outbound.userrepository.UserRepository;
import com.floppahost.adaptiveplanner.planner.domain.model.FixedEvent;
import com.floppahost.adaptiveplanner.planner.domain.model.User;
import com.floppahost.adaptiveplanner.planner.domain.value.FixedEventKind;
import com.floppahost.adaptiveplanner.planner.domain.value.TimeRange;
import com.floppahost.adaptiveplanner.planner.domain.value.UserProfile;
import com.floppahost.adaptiveplanner.planner.presentation.telegrambot.routing.BotRoute;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.Locale;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class HandleTelegramUpdateUseCase implements HandleTelegramUpdate {

    private final TelegramUserRepository telegramUserRepository;
    private final UserRepository userRepository;

    @Override
    public OutgoingResponse handle(IncomingUpdate input) {
        log.debug("Processing incoming update. TelegramUserId: {}, isCallback: {}", input.userId(), input.isCallback());

        if (input.payload() == null || input.payload().isBlank()) return null;
        String payload = input.payload().trim();

        // Intercept onboarding command
        if ("/start".equalsIgnoreCase(payload)) {
            log.info("Telegram user [{}] initiated /start command", input.userId());
            return handleStart(input);
        }

        // Require registration for all other interactions
        Optional<TelegramUserDto> telegramUserDtoOpt = telegramUserRepository.findByTelegramUserId(input.userId());

        if (telegramUserDtoOpt.isEmpty()) {
            log.warn("Telegram user [{}] attempted to interact without registration", input.userId());
            return new OutgoingResponse(input.chatId(), "UNREGISTERED_ERROR", null, null);
        }

        TelegramUserDto telegramUserDto = telegramUserDtoOpt.get();

        User user = userRepository.findById(telegramUserDto.userId()).orElseThrow();

        // Route Callback Queries (Button Clicks)
        if (input.isCallback()) {
            Integer messageId = input.messageId();
            UserProfile currentProfile = user.getProfile();

            if (BotRoute.ADD_JOB_HOURS.getPayload().equals(payload)) {
                updateUserChatState(telegramUserDto, ChatState.WAITING_FOR_EVENT_NAME, "WORK");
                return new OutgoingResponse(input.chatId(), "VIEW_ASK_EVENT_NAME", user, messageId);
            }
            if (BotRoute.ADD_UNI_CLASS.getPayload().equals(payload)) {
                updateUserChatState(telegramUserDto, ChatState.WAITING_FOR_EVENT_NAME, "UNI_CLASS");
                return new OutgoingResponse(input.chatId(), "VIEW_ASK_EVENT_NAME", user, messageId);
            }

            // === Wake & Sleep ===
            if (payload.startsWith(BotRoute.PREFIX_SET_WAKE.getPayload())) {
                String wakeTime = payload.replace(BotRoute.PREFIX_SET_WAKE.getPayload(), "");

                return new OutgoingResponse(
                        input.chatId(),
                        buildViewNameWithParams("VIEW_SETTINGS_SLEEP", wakeTime),
                        user,
                        messageId
                );
            }

            if (payload.startsWith(BotRoute.PREFIX_SET_SLEEP.getPayload())) {
                String rawData = payload.replace(BotRoute.PREFIX_SET_SLEEP.getPayload(), "");
                String[] parts = rawData.split("_");

                if (parts.length < 2) return null;

                LocalTime newWake = LocalTime.parse(parts[0]);
                LocalTime newSleep = LocalTime.parse(parts[1]);

                user.updateSleepWindow(newWake, newSleep, currentProfile.minSleepHours());
                userRepository.save(user);

                return new OutgoingResponse(
                        input.chatId(),
                        "VIEW_SETTINGS_MAIN",
                        user,
                        messageId
                );
            }

            // === Focus & Break ===
            if (payload.startsWith(BotRoute.PREFIX_SET_FOCUS.getPayload())) {
                String rawData = payload.replace(BotRoute.PREFIX_SET_FOCUS.getPayload(), "");
                String[] parts = rawData.split("_");

                if (parts.length < 2) return null;

                String focusMinutesStr = parts[0];
                String breakMinutesStr = parts[1];

                user.updateFocusCycle(Integer.parseInt(focusMinutesStr), Integer.parseInt(breakMinutesStr));
                userRepository.save(user);

                return new OutgoingResponse(
                        input.chatId(),
                        "VIEW_SETTINGS_MAIN",
                        user,
                        messageId
                );
            }

            // === Caps & Limits ===
            if (payload.startsWith(BotRoute.PREFIX_SET_HEAVY_CAP.getPayload())) {
                String rawData = payload.replace(BotRoute.PREFIX_SET_HEAVY_CAP.getPayload(), "");

                int maxHeavyBlocks = Integer.parseInt(rawData);

                user.updateMaxHeavyBlocksPerDay(maxHeavyBlocks);
                userRepository.save(user);

                return new OutgoingResponse(input.chatId(), "VIEW_SETTINGS_CAPS", user, messageId);
            }

            if (payload.startsWith(BotRoute.PREFIX_SET_DAILY_LOAD.getPayload())) {
                String rawData = payload.replace(BotRoute.PREFIX_SET_DAILY_LOAD.getPayload(), "");

                int dailyLoadMinutes = Integer.parseInt(rawData);

                user.updateMaxDailyLoadMinutes(dailyLoadMinutes);
                userRepository.save(user);

                return new OutgoingResponse(input.chatId(), "VIEW_SETTINGS_CAPS", user, messageId);
            }

            // === Goals & Rules ===
            if (payload.startsWith(BotRoute.PREFIX_SET_WEEKLY_TARGET.getPayload())) {
                String rawData = payload.replace(BotRoute.PREFIX_SET_WEEKLY_TARGET.getPayload(), "");

                int targetMinutes = Integer.parseInt(rawData);

                user.updateWeeklyTargetMinutes(targetMinutes);
                userRepository.save(user);

                return new OutgoingResponse(input.chatId(), "VIEW_SETTINGS_GOALS", user, messageId);
            }

            if (BotRoute.TOGGLE_STRICT_ENFORCEMENT.getPayload().equals(payload)) {
                user.toggleStrictEnforcement();
                userRepository.save(user);

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
            user = userRepository.findById(tgUser.userId()).get();
        } else {
            user = User.registerWithoutEmail();
            userRepository.save(user);

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
            case MENU_EDIT_WEEKLY_TARGET ->
                    new OutgoingResponse(input.chatId(), "VIEW_SETTINGS_WEEKLY_TARGET", user, msgId);

            default -> null;
        };
    }

    private OutgoingResponse handleStatefulTextInput(IncomingUpdate input, String text, TelegramUserDto tgUser, User user) {
        try {
            switch (tgUser.state()) {
                case WAITING_FOR_EVENT_NAME -> {
                    // Current payload: "WORK" -> New payload: "WORK|Starbucks"
                    String newPayload = tgUser.statePayload() + "|" + text;
                    updateUserChatState(tgUser, ChatState.WAITING_FOR_EVENT_DAY, newPayload);
                    return new OutgoingResponse(input.chatId(), "VIEW_ASK_EVENT_DAY", user, null);
                }
                case WAITING_FOR_EVENT_DAY -> {
                    // User types "MONDAY" (or clicks a custom keyboard button)
                    String day = text.toUpperCase(Locale.ENGLISH);
                    String newPayload = tgUser.statePayload() + "|" + day;
                    updateUserChatState(tgUser, ChatState.WAITING_FOR_EVENT_START, newPayload);
                    return new OutgoingResponse(input.chatId(), "VIEW_ASK_EVENT_START", user, null);
                }
                case WAITING_FOR_EVENT_START -> {
                    LocalTime startTime = LocalTime.parse(text); // e.g., "09:00"
                    String newPayload = tgUser.statePayload() + "|" + startTime;
                    updateUserChatState(tgUser, ChatState.WAITING_FOR_EVENT_END, newPayload);
                    return new OutgoingResponse(input.chatId(), "VIEW_ASK_EVENT_END", user, null);
                }
                case WAITING_FOR_EVENT_END -> {
                    LocalTime endTime = LocalTime.parse(text);

                    // Parse the accumulated data: ["WORK", "Starbucks", "MONDAY", "09:00"]
                    String[] data = tgUser.statePayload().split("\\|");
                    FixedEventKind kind = FixedEventKind.valueOf(data[0]);
                    String name = data[1];
                    DayOfWeek day = DayOfWeek.valueOf(data[2]);
                    LocalTime startTime = LocalTime.parse(data[3]);

                    // Save to Domain!
                    FixedEvent event = FixedEvent.createBase(user.getId(), name, kind);
                    event.addRecurringBlock(day, TimeRange.of(startTime, endTime));
                    fixedEventPort.save(event);

                    // Reset state
                    updateUserChatState(tgUser, ChatState.IDLE, null);

                    return new OutgoingResponse(input.chatId(), "VIEW_ADD_FIXED_EVENTS", user, null);
                }
                default -> {
                    return null;
                }
            }
        } catch (Exception e) {
            log.warn("User input failed validation for state [{}]. Input: {}", tgUser.state(), text);
            // If they type "hello" instead of "09:00", we send an error view but DON'T change their state,
            // so they can try again.
            return new OutgoingResponse(input.chatId(), "VIEW_INVALID_INPUT_ERROR", user, null);
        }
    }

    private void updateUserChatState(TelegramUserDto tgUser, ChatState newState, String newPayload) {
        telegramUserRepository.save(new TelegramUserDto(
                tgUser.userId(),
                tgUser.telegramUserId(),
                tgUser.chatId(),
                newState,
                newPayload
        ));
    }

    private String buildViewNameWithParams(String viewName, String... params) {
        StringBuilder responseBuilder = new StringBuilder();
        responseBuilder.append(viewName.toUpperCase(Locale.ENGLISH));

        if (params != null) {
            responseBuilder.append("|");
            for (String param : params) {
                responseBuilder.append(param).append("_");
            }
            responseBuilder.deleteCharAt(responseBuilder.length() - 1); // Remove trailing underscore
        }

        return responseBuilder.toString();
    }
}
