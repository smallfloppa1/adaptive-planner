package com.floppahost.adaptiveplanner.planner.infrastructure.persistence.calendar.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalTime;

@Getter
@Setter
@Entity
@Table(name = "one_time_events")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class OneTimeEventEntity extends BaseEventEntity {

    @Column(name = "event_date", nullable = false)
    private LocalDate date;

    @Column(nullable = false)
    private LocalTime startTime;

    @Column(nullable = false)
    private LocalTime endTime;

}