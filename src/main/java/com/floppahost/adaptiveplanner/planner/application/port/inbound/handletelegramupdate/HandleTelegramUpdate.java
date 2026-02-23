package com.floppahost.adaptiveplanner.planner.application.port.inbound.handletelegramupdate;

import com.floppahost.adaptiveplanner.planner.application.port.inbound.handletelegramupdate.dto.IncomingText;
import com.floppahost.adaptiveplanner.planner.application.port.inbound.handletelegramupdate.dto.OutgoingText;

@FunctionalInterface
public interface HandleTelegramUpdate {
    OutgoingText handle(IncomingText input);
}
