package com.floppahost.adaptiveplanner.planner.domain.model;

import com.floppahost.adaptiveplanner.planner.domain.value.Email;
import lombok.Builder;
import lombok.Value;
import lombok.With;

import java.util.UUID;

/**
 * Identity/account entity.
 */
@Value
@Builder
@With
public class User {
    @Builder.Default
    UUID id = UUID.randomUUID();

    Email email;

    @Builder.Default
    boolean active = true;
}
