package com.floppahost.adaptiveplanner.planner.domain.planning.engine;

import com.floppahost.adaptiveplanner.planner.domain.calendar.EventKind;
import com.floppahost.adaptiveplanner.planner.domain.shared.LocalTimeRange;

import java.util.UUID;

public record DailyCommitment(
        UUID eventId,
        LocalTimeRange dateTimeRange,
        String title,
        EventKind kind
) {
}
