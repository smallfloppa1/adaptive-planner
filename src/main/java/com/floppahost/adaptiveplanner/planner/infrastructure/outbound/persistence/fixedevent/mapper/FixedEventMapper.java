package com.floppahost.adaptiveplanner.planner.infrastructure.outbound.persistence.fixedevent.mapper;

import com.floppahost.adaptiveplanner.planner.domain.model.FixedEvent;
import com.floppahost.adaptiveplanner.planner.infrastructure.outbound.persistence.fixedevent.entity.FixedEventEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface FixedEventMapper {

    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    FixedEventEntity toEntity(FixedEvent fixedEvent);

    FixedEvent toDomain(FixedEventEntity entity);
}
