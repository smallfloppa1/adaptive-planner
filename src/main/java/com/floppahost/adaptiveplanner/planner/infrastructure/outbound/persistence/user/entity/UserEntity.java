package com.floppahost.adaptiveplanner.planner.infrastructure.outbound.persistence.user.entity;

import com.floppahost.adaptiveplanner.planner.infrastructure.outbound.persistence.common.AuditableEntity;
import com.floppahost.adaptiveplanner.planner.infrastructure.outbound.persistence.userprofile.entity.UserProfileEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Objects;
import java.util.UUID;

    @Getter
    @Entity
    @Table(name = "users")
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
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
                orphanRemoval = true,
                optional = false
        )
        private UserProfileEntity profile;

        public UserEntity(UUID id, String email, boolean isActive) {
            this.id = Objects.requireNonNull(id, "UserEntity.id must not be null");
            this.email = email;
            this.isActive = isActive;
        }

        public void attachProfile(UserProfileEntity profile) {
            this.profile = Objects.requireNonNull(profile, "UserEntity.profile must not be null");
            profile.attachTo(this);
        }
    }
