package com.floppahost.adaptiveplanner.planner.infrastructure.outbound.persistence.userprofile.entity;

import com.floppahost.adaptiveplanner.planner.infrastructure.outbound.persistence.common.AuditableEntity;
import com.floppahost.adaptiveplanner.planner.infrastructure.outbound.persistence.user.entity.UserEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalTime;
import java.util.Objects;
import java.util.UUID;

@Getter
@Entity
@Table(name = "user_profiles")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class UserProfileEntity extends AuditableEntity {

    @Id
    @Column(name = "user_id", nullable = false)
    private UUID id;

    @MapsId
    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private UserEntity user;

    // Productivity limits

    @Column(nullable = false)
    private LocalTime wakeTime;

    @Column(nullable = false)
    private LocalTime sleepTime;

    @Column(nullable = false)
    private double minSleepHours;

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

    public UserProfileEntity(
            LocalTime wakeTime,
            LocalTime sleepTime,
            double minSleepHours,
            int focusMinutes,
            int breakMinutes,
            int maxHeavyBlocksPerDay,
            int maxTotalPlannedMinutesPerDay,
            int weeklyStudyTargetMinutes,
            boolean strictEnforcement
    ) {
        this.wakeTime = wakeTime;
        this.sleepTime = sleepTime;
        this.minSleepHours = minSleepHours;
        this.focusMinutes = focusMinutes;
        this.breakMinutes = breakMinutes;
        this.maxHeavyBlocksPerDay = maxHeavyBlocksPerDay;
        this.maxTotalPlannedMinutesPerDay = maxTotalPlannedMinutesPerDay;
        this.weeklyStudyTargetMinutes = weeklyStudyTargetMinutes;
        this.strictEnforcement = strictEnforcement;
    }

    public void attachTo(UserEntity user) {
        this.user = Objects.requireNonNull(user, "UserProfileEntity.user must not be null");
    }
}
