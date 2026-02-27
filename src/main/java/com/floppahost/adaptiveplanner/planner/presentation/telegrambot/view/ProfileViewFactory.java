package com.floppahost.adaptiveplanner.planner.presentation.telegrambot.view;

import com.floppahost.adaptiveplanner.planner.application.port.inbound.handletelegramupdate.dto.OutgoingResponse;
import com.floppahost.adaptiveplanner.planner.domain.model.User;
import com.floppahost.adaptiveplanner.planner.domain.value.UserProfile;
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

        return switch (response.viewName()) {
            case "VIEW_WELCOME" -> buildWelcomeMessage(response.chatId(), response.user(), response.editMessageId());
            case "VIEW_SETTINGS_MAIN" ->
                    buildSettingsMainMenu(response.chatId(), response.editMessageId(), response.user());
            case "VIEW_SETTINGS_WAKE_SLEEP" ->
                    buildWakeSleepMenu(response.chatId(), response.editMessageId(), response.user());
            case "VIEW_ADD_FIXED_EVENTS" -> buildFixedEventsMenu(response.chatId(), response.editMessageId());
            default -> new SendMessage(response.chatId().toString(), "Unknown view requested.");
        };
    }

    // ==========================================
    // 1. WELCOME MESSAGE
    // ==========================================
    private BotApiMethod<?> buildWelcomeMessage(Long chatId, User user, Integer messageId) {

        UserProfile profile = user.profile();

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

    // ==========================================
    // 2. SETTINGS MAIN MENU
    // ==========================================
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

    // ==========================================
    // 3. WAKE / SLEEP MENU
    // ==========================================
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

    // ==========================================
    // 4. ADD FIXED EVENTS MENU
    // ==========================================
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

    // ==========================================
    // HELPER METHOD
    // ==========================================
    private InlineKeyboardButton button(String text, String callbackData) {
        return InlineKeyboardButton.builder()
                .text(text)
                .callbackData(callbackData)
                .build();
    }
}
