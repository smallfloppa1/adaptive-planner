package com.floppahost.adaptiveplanner.planner.application.usecase.telegram;

import com.floppahost.adaptiveplanner.planner.application.port.inbound.handletelegramupdate.dto.IncomingUpdate;
import com.floppahost.adaptiveplanner.planner.application.port.inbound.handletelegramupdate.dto.OutgoingResponse;
import com.floppahost.adaptiveplanner.planner.application.port.outbound.telegram.LoadTelegramUserPort;
import com.floppahost.adaptiveplanner.planner.application.port.outbound.telegram.SaveTelegramUserPort;
import com.floppahost.adaptiveplanner.planner.application.port.outbound.user.LoadUserPort;
import com.floppahost.adaptiveplanner.planner.application.port.outbound.user.SaveUserPort;
import com.floppahost.adaptiveplanner.planner.domain.telegram.TelegramUser;
import com.floppahost.adaptiveplanner.planner.domain.user.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Component
@RequiredArgsConstructor
public class TelegramRegistrationService {

    private final SaveUserPort saveUserPort;
    private final LoadUserPort loadUserPort;

    private final SaveTelegramUserPort saveTelegramUserPort;
    private final LoadTelegramUserPort loadTelegramUserPort;

    @Transactional
    public OutgoingResponse handleStart(IncomingUpdate input) {
        return loadTelegramUserPort.loadByTelegramId(input.userId())
                .map(tgUser -> syncExistingUser(input, tgUser))
                .orElseGet(() -> registerNewUser(input));
    }


    private OutgoingResponse syncExistingUser(IncomingUpdate input, TelegramUser tgUser) {
        return loadUserPort.loadById(tgUser.getUserId())
                .map(user -> new OutgoingResponse(input.chatId(), "VIEW_WELCOME", user, null))
                .orElseGet(() -> {
                    log.error("Data Integrity Breach: TelegramUser {} has no User {}",
                            tgUser.getTelegramId(), tgUser.getUserId());
                    return new OutgoingResponse(input.chatId(), "SYSTEM_ERROR_UNSYNCED", null, null);
                });
    }


    @Transactional
    protected OutgoingResponse registerNewUser(IncomingUpdate input) {
        log.info("Registering new user for Telegram ID: {}", input.userId());

        User newUser = User.registerWithoutEmail();
        saveUserPort.save(newUser);

        TelegramUser newTgUser = TelegramUser.create(
                input.userId(),
                newUser.getId(),
                input.chatId()
        );
        saveTelegramUserPort.save(newTgUser);

        return new OutgoingResponse(input.chatId(), "VIEW_WELCOME", newUser, null);
    }
}