package com.floppahost.adaptiveplanner.planner.domain.planning.engine;

import com.floppahost.adaptiveplanner.planner.domain.planning.DayPlan;

import java.util.List;

/**
 * Result of weekly study time redistribution.
 */
public record RedistributionResult(
        List<DayPlan> updatedPlans,
        int deficitMinutes
) {
}
