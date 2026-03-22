package com.floppahost.adaptiveplanner.planner.domain.calendar;

import com.floppahost.adaptiveplanner.planner.domain.shared.LocalDateTimeRange;
import lombok.Getter;

import java.util.Objects;
import java.util.UUID;

@Getter
public class OneTimeEvent extends BaseEvent {

    private final LocalDateTimeRange localDateTimeRange;

    private OneTimeEvent(UUID id, UUID userId, String title, EventKind kind, LocalDateTimeRange localDateTimeRange) {
        super(id, userId, title, kind);
        this.localDateTimeRange = Objects.requireNonNull(localDateTimeRange, "Date and time range cannot be null");;
    }

    public static OneTimeEvent create(UUID userId, String title, EventKind kind, LocalDateTimeRange localDateTimeRange) {
        UUID newEventId = UUID.randomUUID();

        return new OneTimeEvent(newEventId, userId, title, kind, localDateTimeRange);
    }

}
