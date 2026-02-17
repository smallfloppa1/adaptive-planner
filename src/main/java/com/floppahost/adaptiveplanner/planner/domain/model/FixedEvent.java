package com.floppahost.adaptiveplanner.planner.domain.model;


import com.floppahost.adaptiveplanner.planner.domain.value.DateTimeRange;
import com.floppahost.adaptiveplanner.planner.domain.value.FixedEventKind;
import com.floppahost.adaptiveplanner.planner.domain.value.TimeRange;
import com.floppahost.adaptiveplanner.planner.domain.value.Weekday;
import lombok.Builder;
import lombok.Value;
import lombok.With;

import java.util.UUID;

@Value
@Builder
@With
public class FixedEvent {
    UUID userId;
    FixedEventKind kind;
    String title;

    @Builder.Default
    UUID id = UUID.randomUUID();

    // Recurring weekly
    Weekday weekday;
    TimeRange recurringTimeRange;

    // One-time
    DateTimeRange oneTimeRange;

    String location;
    String notes;

    @Builder.Default
    boolean active = true;

    public FixedEvent(
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

        this.userId = userId;
        this.kind = kind;
        this.title = title;
        this.id = id;
        this.weekday = weekday;
        this.recurringTimeRange = recurringTimeRange;
        this.oneTimeRange = oneTimeRange;
        this.location = location;
        this.notes = notes;
        this.active = active;
    }
}
