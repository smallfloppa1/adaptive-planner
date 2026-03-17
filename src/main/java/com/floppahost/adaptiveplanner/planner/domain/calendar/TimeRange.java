package com.floppahost.adaptiveplanner.planner.domain.calendar;

import lombok.Getter;

import java.time.LocalTime;

@Getter
public final class TimeRange {
    private final LocalTime startTime;
    private final LocalTime endTime;

    private TimeRange(LocalTime startTime, LocalTime endTime) {
        if (endTime.isBefore(startTime) || endTime.equals(startTime)) {
            throw new IllegalArgumentException("TimeRange: endTime must be after startTime");
        }

        this.startTime = startTime;
        this.endTime = endTime;
    }

    public static TimeRange of(LocalTime startTime, LocalTime endTime) {
        return new TimeRange(startTime, endTime);
    }
}
