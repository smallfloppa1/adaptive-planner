package com.floppahost.adaptiveplanner.planner.application.port.outbound.telegramuserregistry;

import com.floppahost.adaptiveplanner.planner.application.port.outbound.telegramuserregistry.dto.TelegramUserDto;

import java.util.Optional;

public interface TelegramUserRepository {

    Optional<TelegramUserDto> findByTelegramUserId(long telegramUserId);

    TelegramUserDto save(TelegramUserDto user);

    boolean isPresentByTelegramUserId(long telegramUserId);
}
