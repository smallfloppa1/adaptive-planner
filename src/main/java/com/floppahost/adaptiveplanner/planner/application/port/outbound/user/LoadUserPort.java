package com.floppahost.adaptiveplanner.planner.application.port.outbound.user;

import com.floppahost.adaptiveplanner.planner.domain.user.User;

import java.util.Optional;
import java.util.UUID;

public interface LoadUserPort {
    Optional<User> loadById(UUID id);
}
