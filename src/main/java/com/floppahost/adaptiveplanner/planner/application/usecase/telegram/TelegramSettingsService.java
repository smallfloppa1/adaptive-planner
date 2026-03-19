package com.floppahost.adaptiveplanner.planner.application.usecase.telegram;

import com.floppahost.adaptiveplanner.planner.application.port.inbound.handletelegramupdate.dto.IncomingUpdate;
import com.floppahost.adaptiveplanner.planner.application.port.inbound.handletelegramupdate.dto.OutgoingResponse;
import com.floppahost.adaptiveplanner.planner.application.port.outbound.telegram.SaveTelegramUserPort;
import com.floppahost.adaptiveplanner.planner.application.port.outbound.user.SaveUserPort;
import com.floppahost.adaptiveplanner.planner.domain.telegram.ChatState;
import com.floppahost.adaptiveplanner.planner.domain.telegram.TelegramUser;
import com.floppahost.adaptiveplanner.planner.domain.user.User;
import com.floppahost.adaptiveplanner.planner.domain.user.UserProfile;
import com.floppahost.adaptiveplanner.planner.presentation.telegrambot.routing.BotRoute;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalTime;

@Component
@RequiredArgsConstructor
public class TelegramSettingsService {

    private final SaveUserPort saveUserPort;
    private final SaveTelegramUserPort saveTelegramUserPort;

    public OutgoingResponse handleCallback(String payload, TelegramUser tgUser, User user, IncomingUpdate input) {

        Integer messageId = input.messageId();
        UserProfile profile = user.getProfile();

        // 1. Event Flow Triggers
        if (BotRoute.ADD_JOB_HOURS.getPayload().equals(payload)) {
            return startFlow(tgUser, user, input, "WORK");
        }
        if (BotRoute.ADD_UNI_CLASS.getPayload().equals(payload)) {
            return startFlow(tgUser, user, input, "CLASS");
        }

        // 2. Sleep Window Updates
        if (payload.startsWith(BotRoute.PREFIX_SET_WAKE.getPayload())) {
            String wakeTime = payload.replace(BotRoute.PREFIX_SET_WAKE.getPayload(), "");
            return new OutgoingResponse(input.chatId(), "VIEW_SETTINGS_SLEEP|" + wakeTime, user, messageId);
        }

        if (payload.startsWith(BotRoute.PREFIX_SET_SLEEP.getPayload())) {
            String[] parts = payload.replace(BotRoute.PREFIX_SET_SLEEP.getPayload(), "").split("_");
            user.updateSleepWindow(LocalTime.parse(parts[0]), LocalTime.parse(parts[1]), profile.minSleepHours());
            return saveAndReturn(user, input, "VIEW_SETTINGS_MAIN");
        }

        // 3. Focus Cycle
        if (payload.startsWith(BotRoute.PREFIX_SET_FOCUS.getPayload())) {
            String[] parts = payload.replace(BotRoute.PREFIX_SET_FOCUS.getPayload(), "").split("_");
            user.updateFocusCycle(Integer.parseInt(parts[0]), Integer.parseInt(parts[1]));
            return saveAndReturn(user, input, "VIEW_SETTINGS_MAIN");
        }

        // 4. Caps and Targets
        if (payload.startsWith(BotRoute.PREFIX_SET_HEAVY_CAP.getPayload())) {
            user.updateMaxHeavyBlocksPerDay(Integer.parseInt(payload.replace(BotRoute.PREFIX_SET_HEAVY_CAP.getPayload(), "")));
            return saveAndReturn(user, input, "VIEW_SETTINGS_CAPS");
        }

        if (BotRoute.TOGGLE_STRICT_ENFORCEMENT.getPayload().equals(payload)) {
            user.toggleStrictEnforcement();
            return saveAndReturn(user, input, "VIEW_SETTINGS_GOALS");
        }

        return null;
    }

    private OutgoingResponse startFlow(TelegramUser tgUser, User user, IncomingUpdate input, String kind) {
        tgUser.transitionTo(ChatState.WAITING_FOR_EVENT_NAME, kind);
        saveTelegramUserPort.save(tgUser);
        return new OutgoingResponse(input.chatId(), "VIEW_ASK_EVENT_NAME", user, input.messageId());
    }

    private OutgoingResponse saveAndReturn(User user, IncomingUpdate input, String view) {
        saveUserPort.save(user);
        return new OutgoingResponse(input.chatId(), view, user, input.messageId());
    }
}