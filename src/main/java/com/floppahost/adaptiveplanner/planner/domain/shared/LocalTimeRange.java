package com.floppahost.adaptiveplanner.planner.domain.shared;

import lombok.Getter;

import java.time.LocalTime;
import java.util.Objects;

@Getter
public final class LocalTimeRange implements TemporalRange<LocalTime> {
    private final LocalTime start;
    private final LocalTime end;

    private LocalTimeRange(LocalTime start, LocalTime end) {
        this.start = Objects.requireNonNull(start, "Start time cannot be null");
        this.end = Objects.requireNonNull(end, "End time cannot be null");

        if (!start.isBefore(end)) {
            throw new IllegalArgumentException("Start time must be before end time");
        }
    }

    public static LocalTimeRange of(LocalTime startTime, LocalTime endTime) {
        return new LocalTimeRange(startTime, endTime);
    }

    public boolean overlaps(LocalTimeRange other) {
        Objects.requireNonNull(other, "Time range can not be null");

        return this.start.isBefore(other.end) && this.end.isAfter(other.start);
    }

    @Override
    public LocalTime getStart() {
        return start;
    }

    @Override
    public LocalTime getEnd() {
        return end;
    }

    @Override
    public boolean isWithin(TemporalRange<LocalTime> range) {
        Objects.requireNonNull(range, "Time range can not be null");

        return !this.start.isBefore(range.getStart()) && !this.end.isAfter(range.getEnd());
    }
}
