package com.floppahost.adaptiveplanner.planner.domain.planning.engine;


import com.floppahost.adaptiveplanner.planner.domain.planning.Block;
import com.floppahost.adaptiveplanner.planner.domain.planning.BlockKind;
import com.floppahost.adaptiveplanner.planner.domain.planning.BlockStatus;
import com.floppahost.adaptiveplanner.planner.domain.planning.DayPlan;
import com.floppahost.adaptiveplanner.planner.domain.user.UserProfile;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

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

    public RedistributionResult redistributeWeekStudyMinutes(
            UUID userId,
            UserProfile profile,
            LocalDate today,
            Map<LocalDate, List<DailyCommitment>> commitmentsByDay,
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
                    commitmentsByDay.getOrDefault(day, List.of()),
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
