package com.floppahost.adaptiveplanner.planner.infrastructure.outbound.persistence.user.entity;

import com.floppahost.adaptiveplanner.planner.infrastructure.outbound.persistence.common.AuditableEntity;
import com.floppahost.adaptiveplanner.planner.infrastructure.outbound.persistence.userprofile.entity.UserProfileEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Getter
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "users")
public class UserEntity extends AuditableEntity {

    @Id
    @Column(nullable = false, unique = true)
    private UUID id;

    private String email;

    @Column(nullable = false)
    private boolean isActive;

    @OneToOne(
            mappedBy = "user",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    private UserProfileEntity profile;

    public void setProfile(UserProfileEntity profile) {
        this.profile = profile;
        if (profile != null) profile.setUser(this);
    }
}
