package com.floppahost.adaptiveplanner.domain.dto;

import com.floppahost.adaptiveplanner.domain.model.DayPlan;

import java.util.List;

/**
 * Result of weekly study time redistribution.
 */
public record RedistributionResult(
        List<DayPlan> updatedPlans,
        int deficitMinutes
) {
}
