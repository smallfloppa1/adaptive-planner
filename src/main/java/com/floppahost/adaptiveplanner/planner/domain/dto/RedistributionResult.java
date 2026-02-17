package com.floppahost.adaptiveplanner.planner.domain.dto;

import com.floppahost.adaptiveplanner.planner.domain.model.DayPlan;

import java.util.List;

/**
 * Result of weekly study time redistribution.
 */
public record RedistributionResult(
        List<DayPlan> updatedPlans,
        int deficitMinutes
) {
}
