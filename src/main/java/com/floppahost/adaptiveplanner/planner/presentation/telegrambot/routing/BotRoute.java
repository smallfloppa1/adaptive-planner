package com.floppahost.adaptiveplanner.planner.presentation.telegrambot.routing;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum BotRoute {

    // ==========================================
    // 1. MAIN NAVIGATION & ONBOARDING
    // ==========================================
    CMD_START("/start"),
    ACCEPT_DEFAULTS("ACCEPT_DEFAULTS"),
    ADJUST_PROFILE("ADJUST_PROFILE"),
    BACK_TO_MAIN("BACK_MAIN"),

    // ==========================================
    // 2. FIXED EVENTS WIZARD
    // ==========================================
    ADD_JOB_HOURS("ADD_JOB"),
    ADD_UNI_CLASS("ADD_CLASS"),
    SKIP_FIXED_EVENTS("SKIP_FIXED"),

    // ==========================================
    // 3. SETTINGS MENUS (Categories)
    // ==========================================
    MENU_WAKE_SLEEP("MENU_WAKE_SLEEP"),
    MENU_FOCUS_BREAK("MENU_FOCUS"),
    MENU_CAPS_LIMITS("MENU_CAPS"),
    MENU_GOALS_RULES("MENU_GOALS"),

    // ==========================================
    // 4. ACTION PREFIXES (For dynamic payloads)
    // ==========================================
    // Example usage: "WAKE_07:00"
    PREFIX_SET_WAKE("WAKE_"),
    PREFIX_SET_SLEEP("SLEEP_"),

    // Example usage: "FOCUS_50_10"
    PREFIX_SET_FOCUS("FOCUS_"),

    // Example usage: "HCAP_4"
    PREFIX_SET_HEAVY_CAP("HCAP_"),

    // Example usage: "DMAX_480"
    PREFIX_SET_DAILY_LOAD("DMAX_"),

    // Example usage: "WTAR_1200"
    PREFIX_SET_WEEKLY_TARGET("WTAR_"),

    // Exact toggle action
    TOGGLE_STRICT_ENFORCEMENT("TOGGLE_STRICT");

    private final String payload;

    /**
     * Helper to find an exact route match from incoming Telegram data.
     */
    public static BotRoute fromExactPayload(String data) {
        for (BotRoute route : BotRoute.values()) {
            if (route.getPayload().equals(data)) {
                return route;
            }
        }
        return null; // Return null or a default UNKNOWN route
    }
}