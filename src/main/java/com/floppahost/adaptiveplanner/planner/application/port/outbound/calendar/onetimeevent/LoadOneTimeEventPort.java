package com.floppahost.adaptiveplanner.planner.application.port.outbound.calendar.onetimeevent;

import com.floppahost.adaptiveplanner.planner.domain.calendar.OneTimeEvent;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface LoadOneTimeEventPort {

    Optional<OneTimeEvent> loadById(UUID id);
    List<OneTimeEvent> loadAllByUserIdAndDate(UUID userId, LocalDate date);

}
