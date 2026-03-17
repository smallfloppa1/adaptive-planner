package com.floppahost.adaptiveplanner.planner.infrastructure.persistence.user.entity;

import com.floppahost.adaptiveplanner.planner.infrastructure.persistence.common.AuditableEntity;
import com.floppahost.adaptiveplanner.planner.infrastructure.persistence.userprofile.entity.UserProfileEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

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

}
