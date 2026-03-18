package com.floppahost.adaptiveplanner.planner.application.port.outbound.calendar.recurringevent;

import com.floppahost.adaptiveplanner.planner.domain.calendar.RecurringEvent;

public interface SaveRecurringEventPort {

    void save(RecurringEvent event);

}
