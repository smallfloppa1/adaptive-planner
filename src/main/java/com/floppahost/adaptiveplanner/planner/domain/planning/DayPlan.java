package com.floppahost.adaptiveplanner.planner.domain.planning;

import java.time.LocalDate;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

public record DayPlan(
        UUID userId,
        LocalDate date,
        List<Block> blocks
) {
    public DayPlan {
        Objects.requireNonNull(userId, "User ID cannot be null");
        Objects.requireNonNull(date, "Date cannot be null");
        Objects.requireNonNull(blocks, "Blocks cannot be null");
    }

    public int getTotalPlannedMinutes() {
        return blocks.stream()
                .mapToInt(Block::getPlannedMinutes)
                .sum();
    }

    public double getCompletionRatio() {
        if (blocks.isEmpty()) {
            return 1.0;
        }
        long done = blocks.stream()
                .filter(block -> block.getStatus() == BlockStatus.DONE)
                .count();

        return (double) done / blocks.size();
    }
}
