package com.floppahost.adaptiveplanner.domain.model;

import com.floppahost.adaptiveplanner.domain.value.TaskStatus;
import com.floppahost.adaptiveplanner.domain.value.TaskType;
import lombok.Builder;
import lombok.Value;
import lombok.With;

import java.time.LocalDateTime;
import java.util.UUID;

@Value
@Builder
@With
public class Task {
    UUID userId;
    String title;

    @Builder.Default
    UUID id = UUID.randomUUID();

    String description;

    @Builder.Default
    TaskType taskType = TaskType.OTHER;

    UUID subjectId;

    @Builder.Default
    int estimatedMinutes = 30;

    @Builder.Default
    int priority = 3;

    LocalDateTime deadline;

    @Builder.Default
    TaskStatus status = TaskStatus.TODO;

    public Task(
            UUID userId,
            String title,
            UUID id,
            String description,
            TaskType taskType,
            UUID subjectId,
            int estimatedMinutes,
            int priority,
            LocalDateTime deadline,
            TaskStatus status
    ) {
        
        if (title == null || title.trim().isEmpty()) {
            throw new IllegalArgumentException("Task: title is required");
        }
        if (estimatedMinutes <= 0) {
            throw new IllegalArgumentException("Task: estimated minutes must be positive");
        }
        if (priority < 1 || priority > 5) {
            throw new IllegalArgumentException("Task: priority must be between 1 and 5");
        }

        this.userId = userId;
        this.title = title;
        this.id = id;
        this.description = description;
        this.taskType = taskType;
        this.subjectId = subjectId;
        this.estimatedMinutes = estimatedMinutes;
        this.priority = priority;
        this.deadline = deadline;
        this.status = status;
    }
}
