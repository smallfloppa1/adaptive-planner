package com.floppahost.adaptiveplanner.planner.domain.model;

import com.floppahost.adaptiveplanner.planner.domain.value.ProgramItemType;
import lombok.Builder;
import lombok.Value;
import lombok.With;

import java.util.UUID;

@Value
@Builder
@With
public class ProgramItem {
    UUID programId;
    int order;
    ProgramItemType itemType;
    String title;

    @Builder.Default
    UUID id = UUID.randomUUID();

    String description;

    @Builder.Default
    int estimatedTotalMinutes = 120;

    public ProgramItem(
            UUID programId,
            int order,
            ProgramItemType itemType,
            String title,
            UUID id,
            String description,
            int estimatedTotalMinutes
    ) {
        
        if (order <= 0) {
            throw new IllegalArgumentException("ProgramItem: order must be non-negative");
        }
        if (title == null || title.trim().isEmpty()) {
            throw new IllegalArgumentException("ProgramItem: title is required");
        }
        if (estimatedTotalMinutes <= 0) {
            throw new IllegalArgumentException("ProgramItem: estimated total minutes must be positive");
        }

        this.programId = programId;
        this.order = order;
        this.itemType = itemType;
        this.title = title;
        this.id = id;
        this.description = description;
        this.estimatedTotalMinutes = estimatedTotalMinutes;
    }
}
