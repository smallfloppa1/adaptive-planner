package com.floppahost.adaptiveplanner.planner.domain.planning;

import java.time.Duration;
import java.time.LocalDateTime;

/**
 * Represents a time slot with start and end times.
 * Immutable value object used by time grid calculations.
 */
public record Slot(
        LocalDateTime start,
        LocalDateTime end
) {
    public Slot {
        if (!end.isAfter(start)) {
            throw new IllegalArgumentException("Slot: end must be after start");
        }
    }

    /**
     * Returns the duration of this slot in minutes.
     */
    public int getMinutes() {
        return (int) Duration.between(start, end).toMinutes();
    }

    /**
     * Checks if this slot overlaps with another slot.
     */
    public boolean overlapsWith(Slot other) {
        return this.start.isBefore(other.end) && this.end.isAfter(other.start);
    }

    /**
     * Checks if this slot contains the given time.
     */
    public boolean contains(LocalDateTime time) {
        return !time.isBefore(start) && time.isBefore(end);
    }
}
