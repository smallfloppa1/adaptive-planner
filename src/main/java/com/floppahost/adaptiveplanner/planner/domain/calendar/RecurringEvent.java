package com.floppahost.adaptiveplanner.planner.domain.calendar;

import com.floppahost.adaptiveplanner.planner.domain.shared.LocalTimeRange;

import java.time.DayOfWeek;
import java.util.*;

public class RecurringEvent extends BaseEvent {

    private final List<RecurringBlock> blocks;

    private RecurringEvent(UUID id, UUID userId, EventKind kind, String title, String location, List<RecurringBlock> blocks) {
        super(id, userId, kind, title, location);
        this.blocks = blocks;
        // todo: block list validation
    }

    private RecurringEvent(UUID id, UUID userId, EventKind kind, String title, List<RecurringBlock> blocks) {
        super(id, userId, kind, title);
        this.blocks = blocks;
        // todo: block list validation
    }

    public static RecurringEvent rehydrate(UUID id, UUID userId, EventKind kind, String title, String location, List<RecurringBlock> blocks) {
        return new RecurringEvent(id, userId, kind, title, location, blocks);
    }

    public static RecurringEvent create(UUID userId, EventKind kind, String title) {
        UUID newEventId = UUID.randomUUID();
        List<RecurringBlock> blocks = new ArrayList<>();

        return new RecurringEvent(newEventId, userId, kind, title, blocks);
    }

    public void addBlock(DayOfWeek day, LocalTimeRange dateTimeRange) {
        Objects.requireNonNull(day, "Day of the week cannot be null");
        Objects.requireNonNull(dateTimeRange, "Time range cannot be null");

        boolean isOverlapsWithExistingBlock = this.blocks.stream()
                .anyMatch(b -> b.dayOfWeek() == day && b.timeRange().overlaps(dateTimeRange));

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
