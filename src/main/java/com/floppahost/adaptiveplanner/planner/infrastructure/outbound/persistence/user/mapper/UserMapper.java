package com.floppahost.adaptiveplanner.planner.infrastructure.outbound.persistence.user.mapper;

import com.floppahost.adaptiveplanner.planner.domain.model.User;
import com.floppahost.adaptiveplanner.planner.domain.value.Email;
import com.floppahost.adaptiveplanner.planner.infrastructure.outbound.persistence.user.entity.UserEntity;
import com.floppahost.adaptiveplanner.planner.infrastructure.outbound.persistence.userprofile.mapper.UserProfileMapper;
import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring", uses = {UserProfileMapper.class})
public interface UserMapper {

    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    UserEntity toEntity(User user);

    User toDomain(UserEntity entity);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    void updateEntityFromDomain(User user, @MappingTarget UserEntity entity);

    @AfterMapping
    default void establishBidirectionalLink(@MappingTarget UserEntity entity) {
        if (entity.getProfile() != null) {
            entity.getProfile().setUser(entity);
            entity.getProfile().setId(entity.getId());
        }
    }

    default String map(Email email) {
        return email == null ? null : email.value();
    }

    default Email map(String email) {
        return email == null ? null : new Email(email);
    }
}
