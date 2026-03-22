package com.floppahost.adaptiveplanner.planner.domain.planning.engine;

import com.floppahost.adaptiveplanner.planner.domain.user.UserProfile;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public record PlanInputs(
        UUID userId,
        UserProfile profile,
        LocalDate day,
        List<DailyCommitment> commitments,
        int targetStudyMinutes
) {
}
