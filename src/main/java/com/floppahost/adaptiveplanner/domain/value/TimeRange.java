package com.floppahost.adaptiveplanner.domain.value;

import java.time.LocalTime;

public record TimeRange(
        LocalTime startTime,
        LocalTime endTime
) {
    public TimeRange {
        if (endTime.isBefore(startTime) || endTime.equals(startTime)) {
            throw new IllegalArgumentException("TimeRange: endTime must be after startTime");
        }
    }
}
