package com.floppahost.adaptiveplanner.planner.domain.model;

import com.floppahost.adaptiveplanner.planner.domain.value.ProgramStatus;
import lombok.Builder;
import lombok.Value;
import lombok.With;

import java.time.LocalDate;
import java.util.UUID;

/**
 * Optional long-term plan (e.g., Java DSA prep).
 */
@Value
@Builder
@With
public class Program {
    UUID userId;
    String title;
    String objective;
    LocalDate startDate;
    LocalDate endDate;

    @Builder.Default
    UUID id = UUID.randomUUID();

    @Builder.Default
    int weeklyTargetMinutes = 5 * 60;

    @Builder.Default
    int priority = 3; // 1..5

    @Builder.Default
    ProgramStatus status = ProgramStatus.ACTIVE;

    public Program(
            UUID userId,
            String title,
            String objective,
            LocalDate startDate,
            LocalDate endDate,
            UUID id,
            int weeklyTargetMinutes,
            int priority,
            ProgramStatus status
    ) {
        
        if (title == null || title.trim().isEmpty()) {
            throw new IllegalArgumentException("Program: title is required");
        }
        if (endDate.isBefore(startDate)) {
            throw new IllegalArgumentException("Program: end date must be after program start date");
        }
        if (weeklyTargetMinutes < 0) {
            throw new IllegalArgumentException("Program: weekly target minutes must be positive");
        }
        if (priority < 1 || priority > 5) {
            throw new IllegalArgumentException("Program: priority must be between 1 and 5");
        }

        this.userId = userId;
        this.title = title;
        this.objective = objective;
        this.startDate = startDate;
        this.endDate = endDate;
        this.id = id;
        this.weeklyTargetMinutes = weeklyTargetMinutes;
        this.priority = priority;
        this.status = status;
    }
}
