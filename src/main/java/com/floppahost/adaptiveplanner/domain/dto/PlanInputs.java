package com.floppahost.adaptiveplanner.domain.dto;

import com.floppahost.adaptiveplanner.domain.model.FixedEvent;
import com.floppahost.adaptiveplanner.domain.model.UserProfile;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

/**
 * Input parameters for day plan generation.
 */
public record PlanInputs(
        UUID userId,
        UserProfile profile,
        LocalDate day,
        List<FixedEvent> fixedEvents,
        int targetStudyMinutes
) {
}
