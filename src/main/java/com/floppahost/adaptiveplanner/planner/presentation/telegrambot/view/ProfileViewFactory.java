package com.floppahost.adaptiveplanner.planner.presentation.telegrambot.view;

import com.floppahost.adaptiveplanner.planner.application.port.inbound.handletelegramupdate.dto.OutgoingResponse;
import com.floppahost.adaptiveplanner.planner.domain.user.User;
import com.floppahost.adaptiveplanner.planner.domain.user.UserProfile;
import com.floppahost.adaptiveplanner.planner.presentation.telegrambot.routing.BotRoute;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.botapimethods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardRow;


@Component
public class ProfileViewFactory {

    public BotApiMethod<?> buildView(OutgoingResponse response) {
        if (response.viewName() == null) return null;

        if (response.viewName().startsWith("VIEW_SETTINGS_SLEEP|")) {
            String wakeTime = getParamsFromView(response.viewName())[0]; // { wakeTime }
            return buildSleepMenu(response.chatId(), response.editMessageId(), wakeTime);
        }

        if (response.viewName().startsWith("VIEW_SETTINGS_FOCUS|")) {
            String[] params = getParamsFromView(response.viewName()); // { focusMinutes, breakMinutes }
            String focusMinutes = params[0];
            String breakMinutes = params[1];
        }

        return switch (response.viewName()) {
            case "VIEW_WELCOME" -> buildWelcomeMessage(response.chatId(), response.user(), response.editMessageId());
            case "VIEW_SETTINGS_MAIN" -> buildSettingsMainMenu(response.chatId(), response.editMessageId(), response.user());
            case "VIEW_SETTINGS_WAKE_SLEEP" -> buildWakeSleepMenu(response.chatId(), response.editMessageId(), response.user());
            case "VIEW_ADD_FIXED_EVENTS" -> buildFixedEventsMenu(response.chatId(), response.editMessageId());
            case "VIEW_SETTINGS_WAKE" -> buildWakeMenu(response.chatId(), response.editMessageId());
            // case "VIEW_SETTINGS_SLEEP" -> buildSleepMenu(response.chatId(), response.editMessageId());
            case "VIEW_SETTINGS_FOCUS" -> buildFocusMenu(response.chatId(), response.editMessageId());
            case "VIEW_SETTINGS_CAPS" -> buildCapsMenu(response.chatId(), response.editMessageId(), response.user());
            case "VIEW_SETTINGS_HEAVY_CAP" -> buildHeavyCapMenu(response.chatId(), response.editMessageId());
            case "VIEW_SETTINGS_MAX_DAILY" -> buildMaxDailyMenu(response.chatId(), response.editMessageId());
            case "VIEW_SETTINGS_GOALS" -> buildGoalsMenu(response.chatId(), response.editMessageId(), response.user());
            case "VIEW_SETTINGS_WEEKLY_TARGET" -> buildWeeklyTargetMenu(response.chatId(), response.editMessageId());
            default -> new SendMessage(response.chatId().toString(), "Unknown view requested.");
        };
    }

    private BotApiMethod<?> buildWelcomeMessage(Long chatId, User user, Integer messageId) {

        UserProfile profile = user.getProfile();

        String text = """
                🤖 <b>Welcome to TG AI Planner.</b>
                I build and enforce your daily timeline. You provide your fixed schedule, and I will automatically calculate your free time and schedule your study goals without burning you out.
                
                To start, I’ve generated a <b>Default Cognitive Profile</b> for you:
                🌅 <b>Day Window:</b> %s — %s
                💤 <b>Minimum Sleep:</b> %.1f hours
                
                <b>🧠 Cognitive Limits:</b>
                ⏱️ <b>Study Blocks:</b> %dm focus / %dm break
                🔋 <b>Deep Work Cap:</b> Max %d heavy blocks/day
                ⏳ <b>Max Daily Load:</b> %d mins/day
                
                <b>🎯 Goals & Rules:</b>
                📈 <b>Weekly Target:</b> %d mins/week
                🛡️ <b>Strict Enforcement:</b> %s
                
                Accept these safe defaults to start adding your fixed schedule, or adjust them first.""".formatted(
                profile.wakeTime().toString(),
                profile.sleepTime().toString(),
                profile.minSleepHours(),
                profile.focusMinutes(),
                profile.breakMinutes(),
                profile.maxHeavyBlocksPerDay(),
                profile.maxTotalPlannedMinutesPerDay(),
                profile.weeklyStudyTargetMinutes(),
                profile.strictEnforcement() ? "ON" : "OFF"
        );

        InlineKeyboardMarkup markup = InlineKeyboardMarkup.builder()
                .keyboardRow(new InlineKeyboardRow(button("✅ Looks Good! Let's Plan", BotRoute.ACCEPT_DEFAULTS.getPayload())))
                .keyboardRow(new InlineKeyboardRow(button("⚙️ Adjust My Profile", BotRoute.ADJUST_PROFILE.getPayload())))
                .build();

        if (messageId != null) {
            return EditMessageText.builder()
                    .chatId(chatId.toString())
                    .messageId(messageId)
                    .text(text)
                    .parseMode("HTML")
                    .replyMarkup(markup)
                    .build();
        } else {
            return SendMessage.builder()
                    .chatId(chatId.toString())
                    .text(text)
                    .parseMode("HTML")
                    .replyMarkup(markup)
                    .build();
        }
    }

    private EditMessageText buildSettingsMainMenu(Long chatId, Integer messageId, User user) {
        String text = """
                ⚙️ <b>Profile Settings</b>
                What would you like to adjust?""";

        InlineKeyboardMarkup markup = InlineKeyboardMarkup.builder()
                .keyboardRow(new InlineKeyboardRow(button("⏰ Wake & Sleep Time", BotRoute.MENU_WAKE_SLEEP.getPayload())))
                .keyboardRow(new InlineKeyboardRow(button("🧠 Focus & Break Durations", BotRoute.MENU_FOCUS_BREAK.getPayload())))
                .keyboardRow(new InlineKeyboardRow(button("🔋 Caps & Daily Limits", BotRoute.MENU_CAPS_LIMITS.getPayload())))
                .keyboardRow(new InlineKeyboardRow(button("🎯 Weekly Goals & Rules", BotRoute.MENU_GOALS_RULES.getPayload())))
                .keyboardRow(new InlineKeyboardRow(button("💾 Back to Main Menu", BotRoute.BACK_TO_MAIN.getPayload())))
                .build();

        return EditMessageText.builder()
                .chatId(chatId.toString())
                .messageId(messageId)
                .text(text)
                .parseMode("HTML")
                .replyMarkup(markup)
                .build();
    }

    private EditMessageText buildWakeSleepMenu(Long chatId, Integer messageId, User user) {
        String text = "⏰ <b>Set Wake & Sleep Times</b>\nWhat time do you usually wake up?";

        InlineKeyboardMarkup markup = InlineKeyboardMarkup.builder()
                // You can pass multiple buttons into a single row
                .keyboardRow(new InlineKeyboardRow(
                        button("06:00", BotRoute.PREFIX_SET_WAKE.getPayload() + "06:00"),
                        button("07:00", BotRoute.PREFIX_SET_WAKE.getPayload() + "07:00"),
                        button("08:00", BotRoute.PREFIX_SET_WAKE.getPayload() + "08:00")
                ))
                .keyboardRow(new InlineKeyboardRow(button("🔙 Back", BotRoute.ADJUST_PROFILE.getPayload())))
                .build();

        return EditMessageText.builder()
                .chatId(chatId.toString())
                .messageId(messageId)
                .text(text)
                .parseMode("HTML")
                .replyMarkup(markup)
                .build();
    }

    private EditMessageText buildFixedEventsMenu(Long chatId, Integer messageId) {
        String text = """
                Profile saved! 💾
                
                Now, let's build your foundation. Add your recurring, non-negotiable time blocks so I know when you are completely unavailable.""";

        InlineKeyboardMarkup markup = InlineKeyboardMarkup.builder()
                .keyboardRow(new InlineKeyboardRow(button("🏢 Add Job Hours", BotRoute.ADD_JOB_HOURS.getPayload())))
                .keyboardRow(new InlineKeyboardRow(button("🎓 Add Uni Lecture/Tutorial", BotRoute.ADD_UNI_CLASS.getPayload())))
                .keyboardRow(new InlineKeyboardRow(button("⏭️ Skip for now (Not Recommended)", BotRoute.SKIP_FIXED_EVENTS.getPayload())))
                .build();

        return EditMessageText.builder()
                .chatId(chatId.toString())
                .messageId(messageId)
                .text(text)
                .parseMode("HTML")
                .replyMarkup(markup)
                .build();
    }

    private EditMessageText buildWakeMenu(Long chatId, Integer messageId) {
        String text = "⏰ <b>Set Wake & Sleep Times</b>\nWhat time do you usually wake up?";
        InlineKeyboardMarkup markup = InlineKeyboardMarkup.builder()
                .keyboardRow(new InlineKeyboardRow(
                        button("06:00", BotRoute.PREFIX_SET_WAKE.getPayload() + "06:00"),
                        button("07:00", BotRoute.PREFIX_SET_WAKE.getPayload() + "07:00"),
                        button("08:00", BotRoute.PREFIX_SET_WAKE.getPayload() + "08:00")
                ))
                .keyboardRow(new InlineKeyboardRow(button("🔙 Back", BotRoute.ADJUST_PROFILE.getPayload())))
                .build();
        return EditMessageText.builder().chatId(chatId.toString()).messageId(messageId).text(text).parseMode("HTML").replyMarkup(markup).build();
    }

    private EditMessageText buildSleepMenu(Long chatId, Integer messageId, String wakeTime) {
        String text = "⏰ <b>Set Wake & Sleep Times</b>\nAnd what time is lights out?";

        InlineKeyboardMarkup markup = InlineKeyboardMarkup.builder()
                .keyboardRow(new InlineKeyboardRow(
                        // The payload becomes: "SLEEP_07:00_21:00"
                        button("21:00", BotRoute.PREFIX_SET_SLEEP.getPayload() + wakeTime + "_21:00"),
                        button("22:00", BotRoute.PREFIX_SET_SLEEP.getPayload() + wakeTime + "_22:00"),
                        button("23:00", BotRoute.PREFIX_SET_SLEEP.getPayload() + wakeTime + "_23:00")
                ))
                .keyboardRow(new InlineKeyboardRow(button("🔙 Back", BotRoute.ADJUST_PROFILE.getPayload())))
                .build();

        return EditMessageText.builder()
                .chatId(chatId.toString())
                .messageId(messageId)
                .text(text)
                .parseMode("HTML")
                .replyMarkup(markup)
                .build();
    }

    private EditMessageText buildFocusMenu(Long chatId, Integer messageId) {
        String text = "🧠 <b>Focus & Break</b>\nChoose your preferred Pomodoro rhythm:";
        InlineKeyboardMarkup markup = InlineKeyboardMarkup.builder()
                .keyboardRow(new InlineKeyboardRow(button("🍅 Standard (25m work / 5m break)", BotRoute.PREFIX_SET_FOCUS.getPayload() + "25_5")))
                .keyboardRow(new InlineKeyboardRow(button("⏳ Deep Work (50m work / 10m break)", BotRoute.PREFIX_SET_FOCUS.getPayload() + "50_10")))
                .keyboardRow(new InlineKeyboardRow(button("🔋 Marathon (90m work / 15m break)", BotRoute.PREFIX_SET_FOCUS.getPayload() + "90_15")))
                .keyboardRow(new InlineKeyboardRow(button("🔙 Back", BotRoute.ADJUST_PROFILE.getPayload())))
                .build();
        return EditMessageText.builder()
                .chatId(chatId.toString())
                .messageId(messageId)
                .text(text)
                .parseMode("HTML")
                .replyMarkup(markup)
                .build();
    }

    private EditMessageText buildCapsMenu(Long chatId, Integer messageId, User user) {
        String text = "🔋 <b>Caps & Limits</b>\n<i>Current: Max %d Heavy Blocks, %d mins total.</i>\nWhat do you want to change?".formatted(
                user.getProfile().maxHeavyBlocksPerDay(), user.getProfile().maxTotalPlannedMinutesPerDay()
        );
        InlineKeyboardMarkup markup = InlineKeyboardMarkup.builder()
                .keyboardRow(new InlineKeyboardRow(button("Edit Heavy Block Cap", BotRoute.MENU_EDIT_HEAVY_CAP.getPayload())))
                .keyboardRow(new InlineKeyboardRow(button("Edit Max Daily Mins", BotRoute.MENU_EDIT_MAX_DAILY.getPayload())))
                .keyboardRow(new InlineKeyboardRow(button("🔙 Back", BotRoute.ADJUST_PROFILE.getPayload())))
                .build();
        return EditMessageText.builder().chatId(chatId.toString()).messageId(messageId).text(text).parseMode("HTML").replyMarkup(markup).build();
    }

    private EditMessageText buildHeavyCapMenu(Long chatId, Integer messageId) {
        String text = "🔋 <b>Heavy Block Cap</b>\nSelect maximum heavy (Priority 1) blocks per day:";
        InlineKeyboardMarkup markup = InlineKeyboardMarkup.builder()
                .keyboardRow(new InlineKeyboardRow(
                        button("2", BotRoute.PREFIX_SET_HEAVY_CAP.getPayload() + "2"),
                        button("3", BotRoute.PREFIX_SET_HEAVY_CAP.getPayload() + "3"),
                        button("4", BotRoute.PREFIX_SET_HEAVY_CAP.getPayload() + "4"),
                        button("5", BotRoute.PREFIX_SET_HEAVY_CAP.getPayload() + "5")
                ))
                .keyboardRow(new InlineKeyboardRow(button("🔙 Back", BotRoute.MENU_CAPS_LIMITS.getPayload())))
                .build();

        return EditMessageText.builder()
                .chatId(chatId.toString())
                .messageId(messageId)
                .text(text)
                .parseMode("HTML")
                .replyMarkup(markup)
                .build();
    }

    private EditMessageText buildMaxDailyMenu(Long chatId, Integer messageId) {
        String text = "⏳ <b>Max Daily Load</b>\nSelect maximum minutes the bot can schedule per day:";
        InlineKeyboardMarkup markup = InlineKeyboardMarkup.builder()
                .keyboardRow(new InlineKeyboardRow(
                        button("2 Hours (120m)", BotRoute.PREFIX_SET_DAILY_LOAD.getPayload() + "120"),
                        button("4 Hours (240m)", BotRoute.PREFIX_SET_DAILY_LOAD.getPayload() + "240"),
                        button("6 Hours (360m)", BotRoute.PREFIX_SET_DAILY_LOAD.getPayload() + "360")
                ))
                .keyboardRow(new InlineKeyboardRow(
                        button("8 Hours (480m)", BotRoute.PREFIX_SET_DAILY_LOAD.getPayload() + "480"),
                        button("10 Hours (600m)", BotRoute.PREFIX_SET_DAILY_LOAD.getPayload() + "600")
                ))
                .keyboardRow(new InlineKeyboardRow(button("🔙 Back", BotRoute.MENU_CAPS_LIMITS.getPayload())))
                .build();

        return EditMessageText.builder()
                .chatId(chatId.toString())
                .messageId(messageId)
                .text(text)
                .parseMode("HTML")
                .replyMarkup(markup).
                build();
    }

    private EditMessageText buildGoalsMenu(Long chatId, Integer messageId, User user) {
        String text = "🎯 <b>Goals & Rules</b>\n<i>Current: %d mins/week.</i>\nWhat do you want to change?".formatted(
                user.getProfile().weeklyStudyTargetMinutes()
        );
        String strictToggleText = user.getProfile().strictEnforcement() ? "🛡️ Strict Enforcement: ON" : "🛡️ Strict Enforcement: OFF";

        InlineKeyboardMarkup markup = InlineKeyboardMarkup.builder()
                .keyboardRow(new InlineKeyboardRow(button("⏱️ Edit Weekly Target", BotRoute.MENU_EDIT_WEEKLY_TARGET.getPayload())))
                .keyboardRow(new InlineKeyboardRow(button(strictToggleText, BotRoute.TOGGLE_STRICT_ENFORCEMENT.getPayload())))
                .keyboardRow(new InlineKeyboardRow(button("🔙 Back", BotRoute.ADJUST_PROFILE.getPayload())))
                .build();
        return EditMessageText.builder().chatId(chatId.toString()).messageId(messageId).text(text).parseMode("HTML").replyMarkup(markup).build();
    }

    private EditMessageText buildWeeklyTargetMenu(Long chatId, Integer messageId) {
        String text = "📈 <b>Weekly Target</b>\nSelect your baseline goal for total study time across the week:";
        InlineKeyboardMarkup markup = InlineKeyboardMarkup.builder()
                .keyboardRow(new InlineKeyboardRow(
                        button("10 Hours (600m)", BotRoute.PREFIX_SET_WEEKLY_TARGET.getPayload() + "600"),
                        button("15 Hours (900m)", BotRoute.PREFIX_SET_WEEKLY_TARGET.getPayload() + "900")
                ))
                .keyboardRow(new InlineKeyboardRow(
                        button("20 Hours (1200m)", BotRoute.PREFIX_SET_WEEKLY_TARGET.getPayload() + "1200"),
                        button("25 Hours (1500m)", BotRoute.PREFIX_SET_WEEKLY_TARGET.getPayload() + "1500")
                ))
                .keyboardRow(new InlineKeyboardRow(button("🔙 Back", BotRoute.MENU_GOALS_RULES.getPayload())))
                .build();
        return EditMessageText.builder().chatId(chatId.toString()).messageId(messageId).text(text).parseMode("HTML").replyMarkup(markup).build();
    }

    // Helper methods
    private InlineKeyboardButton button(String text, String callbackData) {
        return InlineKeyboardButton.builder()
                .text(text)
                .callbackData(callbackData)
                .build();
    }

    private String[] getParamsFromView(String viewName) {
        String paramStr = viewName.split("\\|")[1];
        return paramStr.split("_");
    }
}
