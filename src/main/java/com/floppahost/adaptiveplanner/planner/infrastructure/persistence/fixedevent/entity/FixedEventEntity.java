package com.floppahost.adaptiveplanner.planner.infrastructure.persistence.fixedevent.entity;

import com.floppahost.adaptiveplanner.planner.infrastructure.persistence.common.AuditableEntity;
import com.floppahost.adaptiveplanner.planner.infrastructure.persistence.fixedevent.enums.EventKind;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Getter
@Entity
@NoArgsConstructor
@Table(name = "fixed_events")
public class FixedEventEntity extends AuditableEntity {

    @Id
    @Column(nullable = false, unique = true)
    private UUID id;

    @Column(nullable = false)
    private UUID userId;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    @JdbcTypeCode(SqlTypes.NAMED_ENUM)
    private EventKind eventKind;


    @OneToMany(mappedBy = "fixedEvent", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<FixedEventBlockEntity> blocks = new ArrayList<>();

    public void addBlock(FixedEventBlockEntity block) {
        blocks.add(block);
        block.setFixedEvent(this);
    }

    public void removeBlock(FixedEventBlockEntity block) {
        blocks.remove(block);
        block.setFixedEvent(null);
    }
}
