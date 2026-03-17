package com.floppahost.adaptiveplanner.planner.application.port.outbound.telegram;

import com.floppahost.adaptiveplanner.planner.domain.telegram.TelegramUser;

public interface SaveTelegramUserPort {

    void save(TelegramUser user);

}
