package com.floppahost.adaptiveplanner.planner.infrastructure.persistence.calendar.repository;

import com.floppahost.adaptiveplanner.planner.infrastructure.persistence.calendar.entity.OneTimeEventEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface OneTimeEventJpaRepository extends JpaRepository<OneTimeEventEntity, UUID> {

    List<OneTimeEventEntity> findAllByUserId(UUID userId);

}