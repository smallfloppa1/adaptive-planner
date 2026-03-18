package com.floppahost.adaptiveplanner.planner.domain.calendar;

import lombok.Getter;

import java.util.UUID;

@Getter
public abstract class BaseEvent {
    private final UUID id;
    private final UUID userId;
    private String title;
    private EventKind kind;
    private String location;
    private String notes;

    protected BaseEvent(UUID id, UUID userId, String title, EventKind kind) {
        this.id = id;
        this.userId = userId;
        this.title = title;
        this.kind = kind;
    }
}
