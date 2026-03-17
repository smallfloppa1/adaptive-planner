package com.floppahost.adaptiveplanner.planner.application.port.outbound.user;

import java.util.UUID;

public interface DeleteUserPort {
    void deleteById(UUID id);
}
