package com.floppahost.adaptiveplanner.planner.application.port.outbound.user;

import com.floppahost.adaptiveplanner.planner.domain.user.User;

public interface SaveUserPort {
    User save(User user);
}
