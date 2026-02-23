package com.floppahost.adaptiveplanner.planner.application.usecase;

import com.floppahost.adaptiveplanner.planner.application.port.inbound.handletelegramupdate.HandleTelegramUpdate;
import com.floppahost.adaptiveplanner.planner.application.port.inbound.handletelegramupdate.dto.IncomingText;
import com.floppahost.adaptiveplanner.planner.application.port.inbound.handletelegramupdate.dto.OutgoingText;
import com.floppahost.adaptiveplanner.planner.application.port.outbound.telegramuserregistry.TelegramUserRegistry;
import com.floppahost.adaptiveplanner.planner.application.port.outbound.telegramuserregistry.dto.TelegramUserDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class HandleTelegramUpdateUseCase implements HandleTelegramUpdate {

    private final TelegramUserRegistry userRegistry;

    @Override
    public OutgoingText handle(IncomingText input) {
        if (input == null || input.text() == null) return null;

        String text = input.text().trim();
        if (text.isBlank()) return null;

        if ("/start".equalsIgnoreCase(text)) {
            return handleStart(input);
        }

        // For now ignore everything else
        return null;
    }

    private OutgoingText handleStart(IncomingText input) {
        if (input.userId() == null) {
            // should be rare, but safe
            return new OutgoingText(input.chatId(), "Can't register you: Telegram user id is missing.");
        }

        long telegramUserId = input.userId();

        boolean alreadyRegistered = userRegistry.findByTelegramUserId(telegramUserId).isPresent();
        if (alreadyRegistered) {
            return new OutgoingText(input.chatId(), "You're already registered");
        }

        userRegistry.save(new TelegramUserDto(
                telegramUserId,
                input.chatId()
        ));

        return new OutgoingText(input.chatId(), "Registered ✅");
    }
}
