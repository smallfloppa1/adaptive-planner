package com.floppahost.adaptiveplanner.planner.domain.calendar;

import java.util.UUID;

public class OneTimeEvent extends BaseEvent {

    private DateTimeRange dateTimeRange;

    private OneTimeEvent(UUID id, UUID userId, String title, FixedEventKind kind, DateTimeRange dateTimeRange) {
        super(id, userId, title, kind);
        this.dateTimeRange = dateTimeRange;
    }

    public static OneTimeEvent create(UUID userId, String title, FixedEventKind kind, DateTimeRange dateTimeRange) {
        return new OneTimeEvent(UUID.randomUUID(), userId, title, kind, dateTimeRange);
    }

}
