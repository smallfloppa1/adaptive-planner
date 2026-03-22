package com.floppahost.adaptiveplanner.planner.domain.calendar;

import com.floppahost.adaptiveplanner.planner.domain.shared.LocalDateTimeRange;
import lombok.Getter;

import java.util.Objects;
import java.util.UUID;

@Getter
public class OneTimeEvent extends BaseEvent {

    private final LocalDateTimeRange localDateTimeRange;

    private OneTimeEvent(UUID id, UUID userId, EventKind kind, String title, String location, LocalDateTimeRange localDateTimeRange) {
        super(id, userId, kind, title, location);
        this.localDateTimeRange = Objects.requireNonNull(localDateTimeRange, "Date and time range cannot be null");;
    }

    private OneTimeEvent(UUID id, UUID userId, EventKind kind, String title, LocalDateTimeRange localDateTimeRange) {
        super(id, userId, kind, title);
        this.localDateTimeRange = Objects.requireNonNull(localDateTimeRange, "Date and time range cannot be null");;
    }

    public static OneTimeEvent rehydrate(UUID id, UUID userId, EventKind kind, String title, String location, LocalDateTimeRange localDateTimeRange) {
        return new OneTimeEvent(id, userId, kind, title, location, localDateTimeRange);
    }

    public static OneTimeEvent create(UUID userId, EventKind kind, String title, LocalDateTimeRange localDateTimeRange) {
        UUID newEventId = UUID.randomUUID();

        return new OneTimeEvent(newEventId, userId, kind, title, localDateTimeRange);
    }

}
