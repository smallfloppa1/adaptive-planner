package com.floppahost.adaptiveplanner.planner.infrastructure.persistence.user.mapper;

import com.floppahost.adaptiveplanner.planner.domain.user.User;
import com.floppahost.adaptiveplanner.planner.domain.user.Email;
import com.floppahost.adaptiveplanner.planner.infrastructure.persistence.user.entity.UserEntity;
import lombok.RequiredArgsConstructor;
import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;


@RequiredArgsConstructor
@Mapper(componentModel = "spring", uses = {UserProfileMapper.class})
public abstract class UserMapper {

    private final UserProfileMapper profileMapper;

    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    public abstract UserEntity toEntity(User user);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    public abstract void updateEntityFromDomain(User user, @MappingTarget UserEntity entity);

    public User toDomain(UserEntity entity) {
        if (entity == null) {
            return null;
        }

        return User.rehydrate(
                entity.getId(),
                map(entity.getEmail()),
                profileMapper.toDomain(entity.getProfile())
        );
    }

    @AfterMapping
    protected void establishBidirectionalLink(@MappingTarget UserEntity entity) {
        if (entity.getProfile() != null) {
            entity.getProfile().setUser(entity);
            entity.getProfile().setId(entity.getId());
        }
    }

    protected String map(Email email) {
        return email == null ? null : email.value();
    }

    protected Email map(String email) {
        return email == null ? null : new Email(email);
    }
}
