package com.floppahost.adaptiveplanner.planner.infrastructure.persistence.calendar.mapper;

import com.floppahost.adaptiveplanner.planner.domain.calendar.RecurringBlock;
import com.floppahost.adaptiveplanner.planner.domain.shared.LocalTimeRange;
import com.floppahost.adaptiveplanner.planner.infrastructure.persistence.calendar.entity.RecurringBlockEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface RecurringBlockMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "recurringEvent", ignore = true)
    @Mapping(target = "startsAt", source = "timeRange.start")
    @Mapping(target = "endsAt", source = "timeRange.end")
    RecurringBlockEntity toEntity(RecurringBlock domain);

    default RecurringBlock toDomain(RecurringBlockEntity entity) {
        if (entity == null) {
            return null;
        }

        return new RecurringBlock(
                entity.getDayOfWeek(),
                LocalTimeRange.of(entity.getStartsAt(), entity.getEndsAt())
        );
    }
}
