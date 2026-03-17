package com.floppahost.adaptiveplanner.planner.domain.calendar;

import java.time.DayOfWeek;

public record RecurringBlock(
        DayOfWeek dayOfWeek,
        TimeRange timeRange
) {
}