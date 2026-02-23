package com.floppahost.adaptiveplanner.planner.infrastructure.outbound.persistence.user.entity;

import com.floppahost.adaptiveplanner.planner.infrastructure.outbound.persistence.common.AuditableEntity;
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
}
