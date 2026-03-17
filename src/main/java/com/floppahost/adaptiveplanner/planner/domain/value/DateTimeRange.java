package com.floppahost.adaptiveplanner.planner.domain.value;

import lombok.Getter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Getter
public final class DateTimeRange {
    private final LocalDateTime startDateTime;
    private final LocalDateTime endDateTime;

    private DateTimeRange(
            LocalDateTime startDateTime,
            LocalDateTime endDateTime
    ) {
        if (endDateTime.isBefore(startDateTime) || endDateTime.equals(startDateTime)) {
            throw new IllegalArgumentException("DateTimeRange: endDateTime must be after startDateTime");
        }

        this.startDateTime = startDateTime;
        this.endDateTime = endDateTime;
    }

    public static DateTimeRange of(
            LocalDateTime startDateTime,
            LocalDateTime endDateTime
    ) {
        return new DateTimeRange(startDateTime, endDateTime);
    }

    public static DateTimeRange of(
            LocalDate startDate,
            LocalDate endDate,
            LocalTime startTime,
            LocalTime endTime
    ) {
        LocalDateTime startDateTime = LocalDateTime.of(startDate, startTime);
        LocalDateTime endDateTime = LocalDateTime.of(endDate, endTime);

        return new DateTimeRange(startDateTime, endDateTime);
    }
}
