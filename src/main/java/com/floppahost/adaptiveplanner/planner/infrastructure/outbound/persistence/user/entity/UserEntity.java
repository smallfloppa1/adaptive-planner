package com.floppahost.adaptiveplanner.planner.infrastructure.outbound.persistence.user.entity;

import com.floppahost.adaptiveplanner.planner.infrastructure.outbound.persistence.common.AuditableEntity;
import com.floppahost.adaptiveplanner.planner.infrastructure.outbound.persistence.userprofile.entity.UserProfileEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Objects;
import java.util.UUID;

@Getter
@Setter
@Entity
@NoArgsConstructor
@Table(name = "users")
public class UserEntity extends AuditableEntity {

    @Id
    @Column(nullable = false, unique = true)
    private UUID id;

    private String email;

    @Column(nullable = false)
    private boolean active;

    @OneToOne(
            mappedBy = "user",
            cascade = CascadeType.ALL,
            orphanRemoval = true,
            optional = false
    )
    private UserProfileEntity profile;

    public UserEntity(UUID id, String email, boolean active) {
        this.id = Objects.requireNonNull(id, "UserEntity.id must not be null");
        this.email = email;
        this.active = active;
    }
}
