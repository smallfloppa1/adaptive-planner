package com.floppahost.adaptiveplanner.planner.infrastructure.outbound.persistence.userprofile.entity;

import com.floppahost.adaptiveplanner.planner.domain.value.ProfileSetupState;
import com.floppahost.adaptiveplanner.planner.infrastructure.outbound.persistence.common.AuditableEntity;
import com.floppahost.adaptiveplanner.planner.infrastructure.outbound.persistence.user.entity.UserEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.UUID;

@Getter
@Entity
@Table(name = "user_profiles")
public class UserProfileEntity extends AuditableEntity {

    @Id
    @Column(name = "user_id", nullable = false)
    private UUID id;

    @Setter
    @MapsId
    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private UserEntity user;

    // Setup flow state

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ProfileSetupState setupState;

    @Column(nullable = false)
    private int setupStep;

    // Productivity limits


    private LocalDate wakeTime;

    private LocalTime sleepTime;

    private Double sleepMinHours;

    private Integer focusMinutes;

    private Integer breakMinutes;

    private Integer maxHeavyBlocksPerDay;

    private Integer maxTotalPlannedMinutesPerDay;

    private Integer weeklyStudyTargetMinutes;

    private boolean strictEnforcement;

}
