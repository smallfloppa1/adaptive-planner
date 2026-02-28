package com.floppahost.adaptiveplanner.planner.application.port.outbound.telegramuserrepository;

import com.floppahost.adaptiveplanner.planner.application.port.outbound.telegramuserrepository.dto.TelegramUserDto;

import java.util.Optional;

public interface TelegramUserRepository {

    Optional<TelegramUserDto> findByTelegramUserId(long telegramUserId);

    TelegramUserDto save(TelegramUserDto user);

    boolean isPresentByTelegramUserId(long telegramUserId);
}
