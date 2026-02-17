package com.floppahost.adaptiveplanner.domain.value;

import java.time.LocalDateTime;

public record DateTimeRange(
        LocalDateTime startDateTime,
        LocalDateTime endDateTime
) {
    public DateTimeRange {
        if (endDateTime.isBefore(startDateTime) || endDateTime.equals(startDateTime)) {
            throw new IllegalArgumentException("DateTimeRange: endDateTime must be after startDateTime");
        }
    }
}
