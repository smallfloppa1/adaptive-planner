package com.floppahost.adaptiveplanner.planner.infrastructure.persistence.calendar.mapper;

import com.floppahost.adaptiveplanner.planner.domain.calendar.RecurringBlock;
import com.floppahost.adaptiveplanner.planner.domain.calendar.RecurringEvent;
import com.floppahost.adaptiveplanner.planner.infrastructure.persistence.calendar.entity.RecurringEventEntity;
import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring", uses = {RecurringBlockMapper.class})
public abstract class RecurringEventMapper {

    @Autowired
    protected RecurringBlockMapper blockMapper;

    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    public abstract RecurringEventEntity toEntity(RecurringEvent domain);

    @AfterMapping
    protected void linkBlocks(@MappingTarget RecurringEventEntity entity) {
        if (entity.getBlocks() != null) {
            entity.getBlocks().forEach(block -> block.setRecurringEvent(entity));
        }
    }

    public RecurringEvent toDomain(RecurringEventEntity entity) {
        if (entity == null) {
            return null;
        }

        List<RecurringBlock> blocks = entity.getBlocks().stream()
                .map(blockMapper::toDomain)
                .collect(Collectors.toCollection(ArrayList::new));

        return RecurringEvent.rehydrate(
                entity.getId(),
                entity.getUserId(),
                entity.getKind(),
                entity.getTitle(),
                entity.getLocation(),
                blocks
        );
    }
}
