package com.floppahost.adaptiveplanner.planner.infrastructure.persistence.user.repository;

import com.floppahost.adaptiveplanner.planner.infrastructure.persistence.user.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface UserJpaRepository extends JpaRepository<UserEntity, UUID> {
}
