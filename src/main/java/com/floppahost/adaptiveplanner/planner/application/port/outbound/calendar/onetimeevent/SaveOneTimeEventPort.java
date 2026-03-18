package com.floppahost.adaptiveplanner.planner.application.port.outbound.calendar.onetimeevent;

import com.floppahost.adaptiveplanner.planner.domain.calendar.OneTimeEvent;

public interface SaveOneTimeEventPort {

    void save(OneTimeEvent event);

}
