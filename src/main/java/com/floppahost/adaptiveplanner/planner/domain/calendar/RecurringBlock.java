package com.floppahost.adaptiveplanner.planner.domain.calendar;

import com.floppahost.adaptiveplanner.planner.domain.shared.LocalTimeRange;

import java.time.DayOfWeek;
import java.util.Objects;

public record RecurringBlock(
        DayOfWeek dayOfWeek,
        LocalTimeRange timeRange
) {

    public RecurringBlock {
        Objects.requireNonNull(dayOfWeek, "Day of the week cannot be null");
        Objects.requireNonNull(timeRange, "Time range cannot be null");
    }
}