package com.floppahost.adaptiveplanner.planner.domain.planning.engine;

import com.floppahost.adaptiveplanner.planner.domain.calendar.EventKind;
import com.floppahost.adaptiveplanner.planner.domain.planning.Block;
import com.floppahost.adaptiveplanner.planner.domain.planning.BlockKind;
import com.floppahost.adaptiveplanner.planner.domain.planning.BlockRef;
import com.floppahost.adaptiveplanner.planner.domain.planning.Slot;
import com.floppahost.adaptiveplanner.planner.domain.shared.LocalDateTimeRange;
import com.floppahost.adaptiveplanner.planner.domain.user.UserProfile;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;

public class TimeGridService {

    public TimeGridResult computeDaySlotsAndFixedBlocks(
            UUID userId,
            UserProfile profile,
            LocalDate day,
            List<DailyCommitment> commitments
    ) {
        Slot window = buildDayWindow(profile, day);

        List<Block> fixedBlocks = new ArrayList<>();
        List<Slot> busySlots = new ArrayList<>();

        for (DailyCommitment commitment : commitments) {
            // 1. Translate TimeRange into exact LocalDateTime Slots
            LocalDateTime start = LocalDateTime.of(day, commitment.dateTimeRange().getStart());
            LocalDateTime end = LocalDateTime.of(day, commitment.dateTimeRange().getEnd());
            Slot slot = new Slot(start, end);

            busySlots.add(slot);
            fixedBlocks.add(commitmentToBlock(userId, commitment, slot));
        }

        List<Slot> mergedBusySlots = mergeIntervals(busySlots);
        List<Slot> freeSlots = subtractSlots(window, mergedBusySlots);

        return new TimeGridResult(window, fixedBlocks, freeSlots);
    }

    private Slot buildDayWindow(UserProfile profile, LocalDate day) {
        LocalDateTime start = LocalDateTime.of(day, profile.wakeTime());
        LocalDateTime end = LocalDateTime.of(day, profile.sleepTime());
        return new Slot(start, end);
    }

    private Block commitmentToBlock(UUID userId, DailyCommitment commitment, Slot slot) {

        return Block.create(
                userId,
                mapEventKindToBlockKind(commitment.kind()),
                commitment.title(),
                LocalDateTimeRange.of(slot.start(), slot.end()),
                new BlockRef.ForEvent(commitment.eventId())
        );
    }

    private BlockKind mapEventKindToBlockKind(EventKind kind) {
        return switch (kind) {
            case CLASS -> BlockKind.CLASS;
            case WORK -> BlockKind.WORK;
            // ... map your other EventKinds to BlockKinds here ...
            default -> BlockKind.OTHER;
        };
    }

    /**
     * Merge overlapping/adjacent time intervals.
     */
    private List<Slot> mergeIntervals(List<Slot> intervals) {
        if (intervals.isEmpty()) {
            return new ArrayList<>();
        }

        List<Slot> sorted = new ArrayList<>(intervals);
        sorted.sort(Comparator.comparing(Slot::start));

        List<Slot> merged = new ArrayList<>();
        merged.add(sorted.getFirst());

        for (int i = 1; i < sorted.size(); i++) {
            Slot current = sorted.get(i);
            Slot last = merged.getLast();

            // overlapping or adjacent: current.start <= last.end
            if (!current.start().isAfter(last.end())) {
                LocalDateTime newEnd = current.end().isAfter(last.end())
                        ? current.end()
                        : last.end();
                merged.set(merged.size() - 1, new Slot(last.start(), newEnd));
            } else {
                merged.add(current);
            }
        }

        return merged;
    }

    /**
     * Subtract busy slots from window to get free slots.
     * Busy slots must be merged and sorted.
     */
    private List<Slot> subtractSlots(Slot window, List<Slot> busySlots) {
        List<Slot> freeSlots = new ArrayList<>();
        LocalDateTime cursor = window.start();

        for (Slot busy : busySlots) {

            if (!busy.end().isAfter(cursor)) {
                continue;
            }

            if (!busy.start().isBefore(window.end())) {
                break;
            }

            LocalDateTime busyStart = busy.start().isBefore(window.start())
                    ? window.start()
                    : busy.start();
            LocalDateTime busyEnd = busy.end().isAfter(window.end())
                    ? window.end()
                    : busy.end();

            if (busyStart.isAfter(cursor)) {
                freeSlots.add(new Slot(cursor, busyStart));
            }
            if (busyEnd.isAfter(cursor)) {
                cursor = busyEnd;
            }
        }

        if (cursor.isBefore(window.end())) {
            freeSlots.add(new Slot(cursor, window.end()));
        }

        return freeSlots;
    }
}
