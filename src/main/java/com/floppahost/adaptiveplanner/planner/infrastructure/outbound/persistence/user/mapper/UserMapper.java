package com.floppahost.adaptiveplanner.planner.infrastructure.outbound.persistence.user.mapper;

import com.floppahost.adaptiveplanner.planner.domain.model.User;
import com.floppahost.adaptiveplanner.planner.domain.value.Email;
import com.floppahost.adaptiveplanner.planner.infrastructure.outbound.persistence.user.entity.UserEntity;

public final class UserMapper {

    public static UserEntity toEntity(User user) {
        if (user == null) return null;
        if (user.getId() == null) {
            throw new IllegalStateException("User.id is null. Domain must assign UUID.");
        }

        String email = (user.getEmail() == null) ? null : user.getEmail().value();

        return new UserEntity(
                user.getId(),
                email,
                user.isActive()
        );
    }

    public static User toDomain(UserEntity entity) {
        if (entity == null) return null;

        Email email = (entity.getEmail() == null) ? null : new Email(entity.getEmail());

        return User.builder()
                .id(entity.getId())
                .email(email)
                .active(entity.isActive())
                .build();
    }
}
