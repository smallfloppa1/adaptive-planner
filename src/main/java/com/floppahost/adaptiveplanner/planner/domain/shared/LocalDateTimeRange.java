package com.floppahost.adaptiveplanner.planner.domain.shared;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Objects;

public final class LocalDateTimeRange implements TemporalRange<LocalDateTime> {
    private final LocalDateTime start;
    private final LocalDateTime end;

    private LocalDateTimeRange(
            LocalDateTime start,
            LocalDateTime end
    ) {
        this.start = Objects.requireNonNull(start, "Start date and time cannot be null");
        this.end = Objects.requireNonNull(end, "End date and time cannot be null");

        if (!start.isBefore(end)) {
            throw new IllegalArgumentException("Start date and time must be before end date and time");
        }
    }

    public static LocalDateTimeRange of(
            LocalDateTime startDateTime,
            LocalDateTime endDateTime
    ) {
        return new LocalDateTimeRange(startDateTime, endDateTime);
    }

    public Duration getDuration() {
        return Duration.between(start, end);
    }

    @Override
    public LocalDateTime getStart() {
        return start;
    }

    @Override
    public LocalDateTime getEnd() {
        return end;
    }

    @Override
    public boolean isWithin(TemporalRange<LocalDateTime> range) {
        Objects.requireNonNull(range, "Time range can not be null");

        return !this.start.isBefore(range.getStart()) && !this.end.isAfter(range.getEnd());
    }
}
