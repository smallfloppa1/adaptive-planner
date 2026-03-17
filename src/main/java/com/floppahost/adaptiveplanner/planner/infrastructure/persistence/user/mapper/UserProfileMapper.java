package com.floppahost.adaptiveplanner.planner.infrastructure.persistence.user.mapper;

import com.floppahost.adaptiveplanner.planner.domain.user.UserProfile;
import com.floppahost.adaptiveplanner.planner.infrastructure.persistence.user.entity.UserProfileEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface UserProfileMapper {

    @Mapping(target = "user", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    UserProfileEntity toEntity(UserProfile profile);

    UserProfile toDomain(UserProfileEntity entity);

    @Mapping(target = "user", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    void updateEntityFromDomain(UserProfile profile, @MappingTarget UserProfileEntity entity);
}
