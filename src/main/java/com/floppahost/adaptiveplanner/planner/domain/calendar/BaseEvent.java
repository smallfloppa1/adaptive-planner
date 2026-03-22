package com.floppahost.adaptiveplanner.planner.domain.calendar;

import lombok.Getter;

import java.util.Objects;
import java.util.UUID;

@Getter
public abstract class BaseEvent {

    private final UUID id;
    private final UUID userId;
    private final EventKind kind;

    private String title;
    private String location;
    private String notes;

    protected BaseEvent(UUID id, UUID userId, String title, EventKind kind) {

        validateTitle(title);

        this.id = Objects.requireNonNull(id, "Event ID cannot be null");
        this.userId = Objects.requireNonNull(userId, "User ID cannot be null");
        this.kind = Objects.requireNonNull(kind, "Event kind cannot be null");
        this.title = title;
    }

    public void updateTitle(String title) {
        validateTitle(title);

        this.title = title;
    }

    public void updateLocation(String location) {
        this.location = location;
    }

    public void updateNotes(String notes) {
        this.notes = notes;
    }

    private void validateTitle(String title) {
        Objects.requireNonNull(title, "Event title cannot be null");

        if (title.isBlank()) {
            throw new IllegalArgumentException("Event title cannot be blank");
        }
    }
}
