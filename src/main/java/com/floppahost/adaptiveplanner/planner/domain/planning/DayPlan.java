package com.floppahost.adaptiveplanner.planner.domain.planning;

import lombok.Builder;
import lombok.Singular;
import lombok.Value;
import lombok.With;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Value
@Builder
@With
public class DayPlan {
    UUID userId;
    LocalDate day;

    @Builder.Default
    UUID id = UUID.randomUUID();

    @Singular
    List<Block> blocks;

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
