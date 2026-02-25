package com.floppahost.adaptiveplanner.planner.infrastructure.outbound.persistence.user.mapper;

import com.floppahost.adaptiveplanner.planner.domain.model.User;
import com.floppahost.adaptiveplanner.planner.domain.value.Email;
import com.floppahost.adaptiveplanner.planner.domain.value.UserProfile;
import com.floppahost.adaptiveplanner.planner.infrastructure.outbound.persistence.user.entity.UserEntity;
import com.floppahost.adaptiveplanner.planner.infrastructure.outbound.persistence.userprofile.entity.UserProfileEntity;
import com.floppahost.adaptiveplanner.planner.infrastructure.outbound.persistence.userprofile.mapper.UserProfileMapper;

public final class UserMapper {

    public static UserEntity toEntity(User user) {
        if (user == null) {
            throw new IllegalArgumentException("User must not be null when mapping to UserEntity");
        }
        if (user.id() == null) {
            throw new IllegalArgumentException("Cannot map User to UserEntity: User.id must not be null");
        }
        if (user.profile() == null) {
            throw new IllegalArgumentException("Cannot map User to UserEntity: User.profile must not be null");
        }

        String email = (user.email() == null) ? null : user.email().value();

        UserProfileEntity userProfileEntity = UserProfileMapper.toEntity(user.profile());

        UserEntity userEntity = new UserEntity(
                user.id(),
                email,
                user.isActive()
        );

        userEntity.attachProfile(userProfileEntity);

        return userEntity;
    }

    public static User toDomain(UserEntity entity) {
        if (entity == null) {
            throw new IllegalArgumentException("UserEntity must not be null when mapping to User");
        }

        Email email = (entity.getEmail() == null) ? null : new Email(entity.getEmail());

        UserProfile userProfile = UserProfileMapper.toDomain(entity.getProfile());

        return User.rehydrate(
                entity.getId(),
                email,
                entity.isActive(),
                userProfile
        );
    }
}
