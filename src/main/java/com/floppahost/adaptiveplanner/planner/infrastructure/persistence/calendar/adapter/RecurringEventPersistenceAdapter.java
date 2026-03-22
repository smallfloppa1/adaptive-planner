package com.floppahost.adaptiveplanner.planner.infrastructure.persistence.calendar.adapter;

import com.floppahost.adaptiveplanner.planner.application.port.outbound.calendar.recurringevent.LoadRecurringEventPort;
import com.floppahost.adaptiveplanner.planner.application.port.outbound.calendar.recurringevent.SaveRecurringEventPort;
import com.floppahost.adaptiveplanner.planner.domain.calendar.RecurringEvent;
import com.floppahost.adaptiveplanner.planner.infrastructure.persistence.calendar.entity.RecurringEventEntity;
import com.floppahost.adaptiveplanner.planner.infrastructure.persistence.calendar.mapper.RecurringEventMapper;
import com.floppahost.adaptiveplanner.planner.infrastructure.persistence.calendar.repository.RecurringEventJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class RecurringEventPersistenceAdapter implements SaveRecurringEventPort, LoadRecurringEventPort {

    private final RecurringEventJpaRepository repository;
    private final RecurringEventMapper mapper;

    @Override
    public void save(RecurringEvent event) {
        RecurringEventEntity entity = mapper.toEntity(event);
        repository.save(entity);
    }

    @Override
    public List<RecurringEvent> loadAllByUserId(UUID userId) {
        return repository.findAllByUserId(userId).stream()
                .map(mapper::toDomain)
                .toList();
    }

}
