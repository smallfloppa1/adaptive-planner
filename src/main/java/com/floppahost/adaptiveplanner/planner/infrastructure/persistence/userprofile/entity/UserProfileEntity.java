package com.floppahost.adaptiveplanner.planner.infrastructure.persistence.userprofile.entity;

import com.floppahost.adaptiveplanner.planner.infrastructure.persistence.common.AuditableEntity;
import com.floppahost.adaptiveplanner.planner.infrastructure.persistence.user.entity.UserEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalTime;
import java.util.UUID;

@Getter
@Setter
@Entity
@Table(name = "user_profiles")
@NoArgsConstructor
public class UserProfileEntity extends AuditableEntity {

    @Id
    @Column(name = "user_id", nullable = false)
    private UUID id;

    @MapsId
    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private UserEntity user;

    @Column(nullable = false)
    private LocalTime wakeTime;

    @Column(nullable = false)
    private LocalTime sleepTime;

    @Column(nullable = false)
    private double minSleepHours;

    @Column(nullable = false)
    private int morningRoutineMinutes;

    @Column(nullable = false)
    private int eveningRoutineMinutes;

    @Column(nullable = false)
    private int focusMinutes;

    @Column(nullable = false)
    private int breakMinutes;

    @Column(nullable = false)
    private int maxHeavyBlocksPerDay;

    @Column(nullable = false)
    private int maxTotalPlannedMinutesPerDay;

    @Column(nullable = false)
    private int weeklyStudyTargetMinutes;

    @Column(nullable = false)
    private boolean strictEnforcement;

}
