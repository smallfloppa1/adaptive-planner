package com.floppahost.adaptiveplanner.planner.application.usecase.telegram;

import com.floppahost.adaptiveplanner.planner.application.port.inbound.handletelegramupdate.dto.IncomingUpdate;
import com.floppahost.adaptiveplanner.planner.application.port.inbound.handletelegramupdate.dto.OutgoingResponse;
import com.floppahost.adaptiveplanner.planner.application.port.outbound.calendar.recurringevent.SaveRecurringEventPort;
import com.floppahost.adaptiveplanner.planner.application.port.outbound.telegram.SaveTelegramUserPort;
import com.floppahost.adaptiveplanner.planner.domain.calendar.EventKind;
import com.floppahost.adaptiveplanner.planner.domain.calendar.RecurringEvent;
import com.floppahost.adaptiveplanner.planner.domain.shared.LocalTimeRange;
import com.floppahost.adaptiveplanner.planner.domain.telegram.ChatState;
import com.floppahost.adaptiveplanner.planner.domain.telegram.TelegramUser;
import com.floppahost.adaptiveplanner.planner.domain.user.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.Locale;

@Slf4j
@Component
@RequiredArgsConstructor
public class TelegramStateService {

    private final SaveRecurringEventPort saveRecurringEventPort;
    private final SaveTelegramUserPort saveTelegramUserPort;

    public OutgoingResponse handleStatefulTextInput(IncomingUpdate input, String text, TelegramUser tgUser, User user) {
        try {
            return switch (tgUser.getState()) {
                case WAITING_FOR_EVENT_NAME -> {
                    String newPayload = tgUser.getStatePayload() + "|" + text;
                    updateState(tgUser, ChatState.WAITING_FOR_EVENT_DAY, newPayload);
                    yield new OutgoingResponse(input.chatId(), "VIEW_ASK_EVENT_DAY", user, null);
                }
                case WAITING_FOR_EVENT_DAY -> {
                    String day = text.toUpperCase(Locale.ENGLISH);
                    String newPayload = tgUser.getStatePayload() + "|" + day;
                    updateState(tgUser, ChatState.WAITING_FOR_EVENT_START, newPayload);
                    yield new OutgoingResponse(input.chatId(), "VIEW_ASK_EVENT_START", user, null);
                }
                case WAITING_FOR_EVENT_START -> {
                    LocalTime startTime = LocalTime.parse(text);
                    String newPayload = tgUser.getStatePayload() + "|" + startTime;
                    updateState(tgUser, ChatState.WAITING_FOR_EVENT_END, newPayload);
                    yield new OutgoingResponse(input.chatId(), "VIEW_ASK_EVENT_END", user, null);
                }
                case WAITING_FOR_EVENT_END -> finalizeEvent(input, text, tgUser, user);
                default -> null;
            };
        } catch (Exception e) {
            log.warn("Input validation failed for state [{}]: {}", tgUser.getState(), text);
            return new OutgoingResponse(input.chatId(), "VIEW_INVALID_INPUT_ERROR", user, null);
        }
    }

    private OutgoingResponse finalizeEvent(IncomingUpdate input, String text, TelegramUser tgUser, User user) {
        LocalTime endTime = LocalTime.parse(text);
        String[] data = tgUser.getStatePayload().split("\\|");

        EventKind kind = EventKind.valueOf(data[0]);
        String name = data[1];
        DayOfWeek day = DayOfWeek.valueOf(data[2]);
        LocalTime startTime = LocalTime.parse(data[3]);

        RecurringEvent event = RecurringEvent.create(user.getId(), kind, name);
        event.addBlock(day, LocalTimeRange.of(startTime, endTime));

        saveRecurringEventPort.save(event);
        tgUser.resetState();
        saveTelegramUserPort.save(tgUser);

        return new OutgoingResponse(input.chatId(), "VIEW_ADD_FIXED_EVENTS", user, null);
    }

    private void updateState(TelegramUser tgUser, ChatState next, String payload) {
        tgUser.transitionTo(next, payload);
        saveTelegramUserPort.save(tgUser);
    }
}