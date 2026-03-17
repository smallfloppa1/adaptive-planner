package com.floppahost.adaptiveplanner.planner.application.port.outbound.telegram;

import com.floppahost.adaptiveplanner.planner.domain.telegram.TelegramUser;

import java.util.Optional;

public interface LoadTelegramUserPort {

    Optional<TelegramUser> loadByTelegramId(long telegramUserId);

}
