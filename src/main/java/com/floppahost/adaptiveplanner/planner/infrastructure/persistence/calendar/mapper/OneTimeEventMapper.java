package com.floppahost.adaptiveplanner.planner.infrastructure.persistence.calendar.mapper;

import com.floppahost.adaptiveplanner.planner.domain.calendar.OneTimeEvent;
import com.floppahost.adaptiveplanner.planner.domain.shared.LocalDateTimeRange;
import com.floppahost.adaptiveplanner.planner.infrastructure.persistence.calendar.entity.OneTimeEventEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface OneTimeEventMapper {

    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "startsAt", source = "localDateTimeRange.start")
    @Mapping(target = "endsAt", source = "localDateTimeRange.end")
    OneTimeEventEntity toEntity(OneTimeEvent domain);

    default OneTimeEvent toDomain(OneTimeEventEntity entity) {
        if (entity == null) {
            return null;
        }

        return OneTimeEvent.rehydrate(
                entity.getId(),
                entity.getUserId(),
                entity.getKind(),
                entity.getTitle(),
                entity.getLocation(),
                LocalDateTimeRange.of(entity.getStartsAt(), entity.getEndsAt())
        );
    }
}
