package com.floppahost.adaptiveplanner.planner.domain.model;


import com.floppahost.adaptiveplanner.planner.domain.value.DateTimeRange;
import com.floppahost.adaptiveplanner.planner.domain.value.FixedEventKind;
import com.floppahost.adaptiveplanner.planner.domain.value.TimeRange;
import lombok.Getter;

import java.time.DayOfWeek;
import java.util.UUID;

@Getter
public class FixedEvent {

    UUID id = UUID.randomUUID();

    UUID userId;
    String title;
    FixedEventKind kind;

    // Recurring weekly
    DayOfWeek recurringWeekday;
    TimeRange recurringTimeRange;

    // One-time
    DateTimeRange oneTimeRange;

    String location;
    String notes;

    boolean active = true;

    private FixedEvent(UUID userId, FixedEventKind kind, String title) {
        this.userId = userId;
        this.kind = kind;
        this.title = title;
    }

    public static FixedEvent createBase(UUID userId, String title, FixedEventKind kind) {
        return new FixedEvent(
                userId,
                kind,
                title
        );
    }

    public void addOneTimeBlock(DateTimeRange range) {
        if (recurringWeekday != null || recurringTimeRange != null) {
            throw new IllegalArgumentException("FixedEvent: cannot be both recurring and one-time");
        }

        if (oneTimeRange != null) {
            throw new IllegalArgumentException("FixedEvent: cannot have multiple one-time blocks");
        }

        this.oneTimeRange = range;
    }

    public void addRecurringBlock(DayOfWeek weekday, TimeRange range) {
        if (oneTimeRange != null) {
            throw new IllegalArgumentException("FixedEvent: cannot be both recurring and one-time");
        }

        if (recurringWeekday != null || recurringTimeRange != null) {
            throw new IllegalArgumentException("FixedEvent: cannot have multiple recurring blocks");
        }

        this.recurringWeekday = weekday;
        this.recurringTimeRange = range;
    }

    /*public FixedEvent create(
            UUID userId,
            FixedEventKind kind,
            String title,
            UUID id,
            Weekday weekday,
            TimeRange recurringTimeRange,
            DateTimeRange oneTimeRange,
            String location,
            String notes,
            boolean active
    ) {
        
        boolean recurring = weekday != null || recurringTimeRange != null;
        boolean onetime = oneTimeRange != null;

        if (recurring && onetime) {
            throw new IllegalArgumentException("FixedEvent: cannot be both recurring and one-time");
        }
        if (!recurring && !onetime) {
            throw new IllegalArgumentException(
                "FixedEvent: must be either recurring (weekday+time_range) or one-time (dt_range)"
            );
        }
        if (recurring) {
            if (weekday == null || recurringTimeRange == null) {
                throw new IllegalArgumentException("FixedEvent: Recurring requires weekday and time_range");
            }
        }
        if (title == null || title.trim().isEmpty()) {
            throw new IllegalArgumentException("FixedEvent: title is required");
        }

        return new FixedEvent(
                userId,
                kind,
                title,
                id,
                weekday,
                recurringTimeRange,
                oneTimeRange,
                location,
                notes,
                active
        );
    }*/
}
