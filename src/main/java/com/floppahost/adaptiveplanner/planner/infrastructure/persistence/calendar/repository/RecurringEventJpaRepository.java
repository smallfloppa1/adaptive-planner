package com.floppahost.adaptiveplanner.planner.infrastructure.persistence.calendar.repository;

import com.floppahost.adaptiveplanner.planner.infrastructure.persistence.calendar.entity.RecurringEventEntity;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface RecurringEventJpaRepository extends JpaRepository<RecurringEventEntity, UUID> {

    @EntityGraph(attributePaths = {"blocks"})
    List<RecurringEventEntity> findAllByUserId(UUID userId);
    
}