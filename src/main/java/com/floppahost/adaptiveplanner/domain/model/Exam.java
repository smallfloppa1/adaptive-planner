package com.floppahost.adaptiveplanner.domain.model;

import lombok.Builder;
import lombok.Value;
import lombok.With;

import java.time.LocalDateTime;
import java.util.UUID;

@Value
@Builder
@With
public class Exam {
    UUID userId;
    UUID subjectId;
    LocalDateTime startsAt;

    @Builder.Default
    UUID id = UUID.randomUUID();

    @Builder.Default
    int difficulty = 3;

    @Builder.Default
    int importance = 3;

    String location;
    String notes;

    public Exam(
            UUID userId,
            UUID subjectId,
            LocalDateTime startsAt,
            UUID id,
            int difficulty,
            int importance,
            String location,
            String notes
    ) {
        
        if (difficulty < 1 || difficulty > 5) {
            throw new IllegalArgumentException("Exam: difficulty must be between 1 and 5");
        }
        if (importance < 1 || importance > 5) {
            throw new IllegalArgumentException("Exam: importance must be between 1 and 5");
        }

        this.userId = userId;
        this.subjectId = subjectId;
        this.startsAt = startsAt;
        this.id = id;
        this.difficulty = difficulty;
        this.importance = importance;
        this.location = location;
        this.notes = notes;
    }
}
