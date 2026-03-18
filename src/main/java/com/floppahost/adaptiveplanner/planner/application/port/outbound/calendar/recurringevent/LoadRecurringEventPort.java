package com.floppahost.adaptiveplanner.planner.application.port.outbound.calendar.recurringevent;

import com.floppahost.adaptiveplanner.planner.domain.calendar.RecurringEvent;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface LoadRecurringEventPort {

    List<RecurringEvent> loadAllByUserId(UUID userId);

}
