package com.floppahost.adaptiveplanner.planner.infrastructure.persistence.calendar.entity;

import com.floppahost.adaptiveplanner.planner.domain.calendar.FixedEventKind;
import com.floppahost.adaptiveplanner.planner.infrastructure.persistence.common.AuditableEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@Entity
@Table(name = "base_events")
@Inheritance(strategy = InheritanceType.JOINED)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public abstract class BaseEventEntity extends AuditableEntity {

    @Id
    @Column(nullable = false, unique = true)
    private UUID id;

    @Column(nullable = false)
    private UUID userId;

    @Column(nullable = false)
    private String title;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private FixedEventKind kind;

    private String location;

    @Column(columnDefinition = "TEXT")
    private String notes;
}

