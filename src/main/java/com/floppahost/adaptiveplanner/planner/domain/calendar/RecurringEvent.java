package com.floppahost.adaptiveplanner.planner.domain.calendar;

import java.time.DayOfWeek;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

public class RecurringEvent extends BaseEvent {

    private final List<RecurringBlock> blocks;

    private RecurringEvent(UUID id, UUID userId, String title, FixedEventKind kind) {
        super(id, userId, title, kind);
        this.blocks = new ArrayList<>();
    }

    public static RecurringEvent create(UUID userId, String title, FixedEventKind kind) {
        return new RecurringEvent(UUID.randomUUID(), userId, title, kind);
    }

    public void addBlock(DayOfWeek day, TimeRange timeRange) {
        // Validation logic here to prevent overlapping days/times
        this.blocks.add(new RecurringBlock(day, timeRange));
    }

    public List<RecurringBlock> getBlocks() {
        return Collections.unmodifiableList(blocks);
    }
}
