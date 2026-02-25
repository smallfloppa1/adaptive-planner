package com.floppahost.adaptiveplanner.planner.infrastructure.outbound.persistence.user.mapper;

import com.floppahost.adaptiveplanner.planner.domain.model.User;
import com.floppahost.adaptiveplanner.planner.domain.value.Email;
import com.floppahost.adaptiveplanner.planner.infrastructure.outbound.persistence.user.entity.UserEntity;

public final class UserMapper {

    public static UserEntity toEntity(User user) {
        if (user == null) return null;
        if (user.id() == null) {
            throw new IllegalStateException("User.id is null. Domain must assign UUID.");
        }

        String email = (user.email() == null) ? null : user.email().value();

        return new UserEntity(
                user.id(),
                email,
                user.isActive()
        );
    }

    public static User toDomain(UserEntity entity) {
        if (entity == null) return null;

        Email email = (entity.getEmail() == null) ? null : new Email(entity.getEmail());

        return User.rehydrate(
                entity.getId(),
                email,
                entity.isActive()
        );
    }
}
