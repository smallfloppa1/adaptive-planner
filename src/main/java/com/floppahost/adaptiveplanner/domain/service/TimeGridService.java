package com.floppahost.adaptiveplanner.domain.service;

import com.floppahost.adaptiveplanner.domain.dto.TimeGridResult;
import com.floppahost.adaptiveplanner.domain.model.*;
import com.floppahost.adaptiveplanner.domain.value.BlockKind;
import com.floppahost.adaptiveplanner.domain.value.FixedEventKind;
import com.floppahost.adaptiveplanner.domain.value.Slot;
import com.floppahost.adaptiveplanner.domain.value.Weekday;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

public class TimeGridService {

    /**
     * Convert LocalDate to Weekday enum.
     */
    private Weekday weekdayOf(LocalDate day) {
        return Weekday.values()[day.getDayOfWeek().getValue() - 1];
    }

    /**
     * Build the day window (wake time to sleep time) for a given day.
     */
    public Slot buildDayWindow(UserProfile profile, LocalDate day) {
        LocalDateTime start = LocalDateTime.of(day, profile.getWakeTime());
        LocalDateTime end = LocalDateTime.of(day, profile.getSleepTime());
        return new Slot(start, end);
    }

    /**
     * Expand fixed events to concrete slots for a specific day.
     * Returns pairs of (event, slot).
     */
    public List<Map.Entry<FixedEvent, Slot>> expandFixedEventsForDay(
            UserProfile profile,
            LocalDate day,
            List<FixedEvent> fixedEvents
    ) {
        Weekday targetWeekday = weekdayOf(day);
        List<Map.Entry<FixedEvent, Slot>> result = new ArrayList<>();

        for (FixedEvent event : fixedEvents) {
            if (!event.isActive()) {
                continue;
            }

            // One-time event: dtRange materialized already
            if (event.getOneTimeRange() != null) {
                LocalDate eventDate = event.getOneTimeRange().startDateTime().toLocalDate();
                if (eventDate.equals(day)) {
                    Slot slot = new Slot(
                            event.getOneTimeRange().startDateTime(),
                            event.getOneTimeRange().endDateTime()
                    );
                    result.add(Map.entry(event, slot));
                }
                continue;
            }

            // Recurring weekly event
            if (event.getWeekday() == targetWeekday && event.getRecurringTimeRange() != null) {
                LocalDateTime start = LocalDateTime.of(day, event.getRecurringTimeRange().startTime());
                LocalDateTime end = LocalDateTime.of(day, event.getRecurringTimeRange().endTime());
                result.add(Map.entry(event, new Slot(start, end)));
            }
        }

        // Sort by start time
        result.sort(Comparator.comparing(e -> e.getValue().start()));
        return result;
    }

    /**
     * Convert fixed event to a Block.
     */
    public Block fixedEventToBlock(UUID userId, FixedEvent event, Slot slot) {
        BlockKind blockKind = mapFixedEventKindToBlockKind(event.getKind());

        return Block.builder()
                .userId(userId)
                .kind(blockKind)
                .title(event.getTitle())
                .startsAt(slot.start())
                .endsAt(slot.end())
                .ref(BlockRef.builder().fixedEventId(event.getId()).build())
                .build();
    }

    /**
     * Map FixedEventKind to BlockKind.
     */
    private BlockKind mapFixedEventKindToBlockKind(FixedEventKind kind) {
        return switch (kind) {
            case CLASS, LECTURE -> BlockKind.CLASS;
            case MEETING -> BlockKind.TASK;
            case MEAL -> BlockKind.MEAL;
            case EXERCISE, COMMUTE, PERSONAL, OTHER -> BlockKind.OTHER;
        };
    }

    /**
     * Compute day slots and fixed blocks for a given day.
     * Returns day window, fixed blocks, and free slots.
     */
    public TimeGridResult computeDaySlotsAndFixedBlocks(
            UUID userId,
            UserProfile profile,
            LocalDate day,
            List<FixedEvent> fixedEvents
    ) {
        Slot window = buildDayWindow(profile, day);

        List<Map.Entry<FixedEvent, Slot>> expanded =
                expandFixedEventsForDay(profile, day, fixedEvents);

        List<Block> fixedBlocks = expanded.stream()
                .map(entry -> fixedEventToBlock(userId, entry.getKey(), entry.getValue()))
                .collect(Collectors.toList());

        List<Slot> busySlots = expanded.stream()
                .map(Map.Entry::getValue)
                .collect(Collectors.toList());

        List<Slot> mergedBusySlots = mergeIntervals(busySlots);
        List<Slot> freeSlots = subtractSlots(window, mergedBusySlots);

        return new TimeGridResult(window, fixedBlocks, freeSlots);
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
