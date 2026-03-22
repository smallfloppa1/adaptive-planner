package com.floppahost.adaptiveplanner.planner.domain.calendar;

import com.floppahost.adaptiveplanner.planner.domain.shared.LocalTimeRange;

import java.time.DayOfWeek;
import java.util.*;

public class RecurringEvent extends BaseEvent {

    private final List<RecurringBlock> blocks;

    private RecurringEvent(UUID id, UUID userId, String title, EventKind kind) {
        super(id, userId, title, kind);
        this.blocks = new ArrayList<>();
    }

    public static RecurringEvent create(UUID userId, String title, EventKind kind) {
        UUID newEventId = UUID.randomUUID();

        return new RecurringEvent(newEventId, userId, title, kind);
    }

    public void addBlock(DayOfWeek day, LocalTimeRange dateTimeRange) {
        Objects.requireNonNull(day, "Day of the week cannot be null");
        Objects.requireNonNull(dateTimeRange, "Time range cannot be null");

        boolean isOverlapsWithExistingBlock = this.blocks.stream()
                .anyMatch(b -> b.dayOfWeek() == day && b.dateTimeRange().overlaps(dateTimeRange));

        if (isOverlapsWithExistingBlock) {
            throw new IllegalArgumentException("New block overlaps with an existing block on " + day);
        }

        RecurringBlock newBlock = new RecurringBlock(day, dateTimeRange);
        this.blocks.add(newBlock);
    }

    public List<RecurringBlock> getBlocks() {
        return Collections.unmodifiableList(blocks);
    }
}
