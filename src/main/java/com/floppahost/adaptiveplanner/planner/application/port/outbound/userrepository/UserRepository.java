package com.floppahost.adaptiveplanner.planner.application.port.outbound.userrepository;

import com.floppahost.adaptiveplanner.planner.domain.model.User;

import java.util.Optional;
import java.util.UUID;

public interface UserRepository {
    Optional<User> findById(UUID id);
    User save(User user);
    void deleteById(UUID id);
}
