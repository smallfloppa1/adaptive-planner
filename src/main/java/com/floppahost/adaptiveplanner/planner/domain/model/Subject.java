package com.floppahost.adaptiveplanner.planner.domain.model;

import lombok.Builder;
import lombok.Value;
import lombok.With;

import java.util.UUID;

@Value
@Builder
@With
public class Subject {
    UUID userId;
    String name;

    @Builder.Default
    UUID id = UUID.randomUUID();

    @Builder.Default
    int weeklyTargetMinutes = 0;

    @Builder.Default
    int priority = 3; // 1..5

    public Subject(
            UUID userId,
            String name,
            UUID id,
            int weeklyTargetMinutes,
            int priority
    ) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Subject: name is required");
        }
        if (weeklyTargetMinutes < 0) {
            throw new IllegalArgumentException("Subject: weekly_target_minutes must be >= 0");
        }
        if (priority < 1 || priority > 5) {
            throw new IllegalArgumentException("Subject: priority must be between 1 and 5");
        }

        this.userId = userId;
        this.name = name;
        this.id = id;
        this.weeklyTargetMinutes = weeklyTargetMinutes;
        this.priority = priority;
    }
}
