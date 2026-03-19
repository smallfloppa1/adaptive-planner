package com.floppahost.adaptiveplanner.planner.application.usecase.telegram;

import com.floppahost.adaptiveplanner.planner.application.port.inbound.handletelegramupdate.dto.IncomingUpdate;
import com.floppahost.adaptiveplanner.planner.application.port.inbound.handletelegramupdate.dto.OutgoingResponse;
import com.floppahost.adaptiveplanner.planner.domain.user.User;
import com.floppahost.adaptiveplanner.planner.presentation.telegrambot.routing.BotRoute;
import org.springframework.stereotype.Component;

@Component
public class TelegramMenuService {

    public OutgoingResponse handleMenuNavigation(BotRoute route, User user, IncomingUpdate input) {
        Integer msgId = input.messageId();
        String view = switch (route) {
            case ADJUST_PROFILE -> "VIEW_SETTINGS_MAIN";
            case BACK_TO_MAIN -> "VIEW_WELCOME";
            case ACCEPT_DEFAULTS -> "VIEW_ADD_FIXED_EVENTS";
            case MENU_WAKE_SLEEP -> "VIEW_SETTINGS_WAKE";
            case MENU_FOCUS_BREAK -> "VIEW_SETTINGS_FOCUS";
            case MENU_CAPS_LIMITS -> "VIEW_SETTINGS_CAPS";
            case MENU_GOALS_RULES -> "VIEW_SETTINGS_GOALS";
            case MENU_EDIT_HEAVY_CAP -> "VIEW_SETTINGS_HEAVY_CAP";
            case MENU_EDIT_MAX_DAILY -> "VIEW_SETTINGS_MAX_DAILY";
            case MENU_EDIT_WEEKLY_TARGET -> "VIEW_SETTINGS_WEEKLY_TARGET";
            default -> null;
        };

        return view != null ? new OutgoingResponse(input.chatId(), view, user, msgId) : null;
    }
}