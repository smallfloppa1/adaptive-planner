package com.floppahost.adaptiveplanner.planner.domain.service;


import com.floppahost.adaptiveplanner.planner.domain.dto.PlanInputs;
import com.floppahost.adaptiveplanner.planner.domain.dto.RedistributionResult;
import com.floppahost.adaptiveplanner.planner.domain.model.Block;
import com.floppahost.adaptiveplanner.planner.domain.model.DayPlan;
import com.floppahost.adaptiveplanner.planner.domain.model.FixedEvent;
import com.floppahost.adaptiveplanner.planner.domain.value.BlockKind;
import com.floppahost.adaptiveplanner.planner.domain.value.BlockStatus;
import com.floppahost.adaptiveplanner.planner.domain.value.UserProfile;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Handles redistribution of missed study time across the week.
 * Implements Strategy C for deficit recovery.
 */
public class RedistributionService {

    private final PlanningEngine planningEngine;

    public RedistributionService(PlanningEngine planningEngine) {
        this.planningEngine = planningEngine;
    }

    /**
     * Calculate completed study minutes from a day plan.
     */
    private int getCompletedStudyMinutes(DayPlan plan) {
        int total = 0;
        for (Block block : plan.getBlocks()) {
            if (block.getKind() == BlockKind.STUDY &&
                block.getStatus() == BlockStatus.DONE) {
                total += block.getPlannedMinutes();
            }
        }
        return total;
    }

    /**
     * Redistribute week study minutes across remaining days.
     * Strategy C (MVP correct):
     * - Compute COMPLETED study minutes from week start up to yesterday
     * - Calculate deficit = weekly_target - completed
     * - Distribute deficit across remaining days evenly, capped by daily limits
     * - Generate full timeline day plans for remaining days
     * 
     * @param userId User ID
     * @param profile User profile
     * @param today Current date
     * @param fixedEventsByDay Map of date to fixed events
     * @param existingPlansByDay Map of date to existing plans
     * @param weeklyTargetMinutes Weekly study target
     * @return Redistribution result with updated plans
     */
    public RedistributionResult redistributeWeekStudyMinutes(
            UUID userId,
            UserProfile profile,
            LocalDate today,
            Map<LocalDate, List<FixedEvent>> fixedEventsByDay,
            Map<LocalDate, DayPlan> existingPlansByDay,
            int weeklyTargetMinutes
    ) {

        if (today.getDayOfWeek() == DayOfWeek.SUNDAY) {
            return new RedistributionResult(List.of(), 0);
        }
        
        LocalDate monday = today.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
        LocalDate sunday = monday.plusDays(6);

        int completedMinutes = calculateCompletedMinutes(
                monday,
                today.minusDays(1),
                existingPlansByDay
        );

        int deficit = Math.max(weeklyTargetMinutes - completedMinutes, 0);
        if (deficit == 0) {
            return new RedistributionResult(List.of(), 0);
        }

        List<LocalDate> remainingDays = new ArrayList<>();
        for (LocalDate d = today; !d.isAfter(sunday); d = d.plusDays(1)) {
            remainingDays.add(d);
        }

        if (remainingDays.isEmpty()) {
            return new RedistributionResult(List.of(), 0);
        }

        int base = deficit / remainingDays.size();
        int remainder = deficit % remainingDays.size();

        List<DayPlan> updatedPlans = new ArrayList<>();
        for (int i = 0; i < remainingDays.size(); i++) {
            LocalDate day = remainingDays.get(i);

            int targetForDay = base + (i < remainder ? 1 : 0);
            targetForDay = Math.min(
                    targetForDay,
                    profile.maxTotalPlannedMinutesPerDay()
            );

            PlanInputs inputs = new PlanInputs(
                    userId,
                    profile,
                    day,
                    fixedEventsByDay.getOrDefault(day, List.of()),
                    targetForDay
            );

            DayPlan newPlan = planningEngine.generateDayPlan(inputs);
            updatedPlans.add(newPlan);
        }

        return new RedistributionResult(updatedPlans, deficit);
    }

    private int calculateCompletedMinutes(
            LocalDate from,
            LocalDate to,
            Map<LocalDate, DayPlan> plans
    ) {
        int total = 0;

        for (LocalDate d = from; !d.isAfter(to); d = d.plusDays(1)) {
            DayPlan plan = plans.get(d);
            if (plan == null) continue;

            for (Block block : plan.getBlocks()) {
                if (block.getKind() == BlockKind.STUDY &&
                        block.getStatus() == BlockStatus.DONE) {
                    total += block.getPlannedMinutes();
                }
            }
        }

        return total;
    }
}
