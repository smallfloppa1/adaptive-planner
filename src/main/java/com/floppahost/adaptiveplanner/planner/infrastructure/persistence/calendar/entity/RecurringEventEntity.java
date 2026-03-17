package com.floppahost.adaptiveplanner.planner.infrastructure.persistence.calendar.entity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Entity
@Table(name = "recurring_events")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class RecurringEventEntity extends BaseEventEntity {

    @OneToMany(
            mappedBy = "recurringEvent",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    private List<RecurringBlockEntity> blocks = new ArrayList<>();

    public void addBlock(RecurringBlockEntity block) {
        blocks.add(block);
        block.setRecurringEvent(this);
    }
}