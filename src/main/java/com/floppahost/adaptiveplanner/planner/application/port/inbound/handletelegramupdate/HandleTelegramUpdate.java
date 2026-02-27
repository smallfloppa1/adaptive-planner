package com.floppahost.adaptiveplanner.planner.application.port.inbound.handletelegramupdate;

import com.floppahost.adaptiveplanner.planner.application.port.inbound.handletelegramupdate.dto.IncomingUpdate;
import com.floppahost.adaptiveplanner.planner.application.port.inbound.handletelegramupdate.dto.OutgoingResponse;

@FunctionalInterface
public interface HandleTelegramUpdate {
    OutgoingResponse handle(IncomingUpdate input);
}
