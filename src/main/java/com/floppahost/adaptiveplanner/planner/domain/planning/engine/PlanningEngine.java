package com.floppahost.adaptiveplanner.planner.domain.planning.engine;


import com.floppahost.adaptiveplanner.planner.domain.planning.Block;
import com.floppahost.adaptiveplanner.planner.domain.planning.DayPlan;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * Central orchestrator for creating day plans.
 * Coordinates time grid, block allocation, and validation services.
 */
public class PlanningEngine {

    private final TimeGridService timeGridService;
    private final BlockAllocationService blockAllocationService;
    private final ScheduleValidationService validationService;

    public PlanningEngine(
            TimeGridService timeGridService,
            BlockAllocationService blockAllocationService,
            ScheduleValidationService validationService) {
        this.timeGridService = timeGridService;
        this.blockAllocationService = blockAllocationService;
        this.validationService = validationService;
    }

    /**
     * Generate a complete day plan.
     * Process:
     * 1. Compute time grid with fixed events and free slots
     * 2. Allocate study/break blocks in free slots
     * 3. Combine all blocks and validate
     * 
     * @param inputs Plan generation inputs
     * @return Valid day plan
     * @throws PlanValidationException if the generated plan is invalid
     */
    public DayPlan generateDayPlan(PlanInputs inputs) {
        // Step 1: Compute time grid
        TimeGridResult gridResult = timeGridService.computeDaySlotsAndFixedBlocks(
            inputs.userId(),
            inputs.profile(),
            inputs.day(),
            inputs.fixedEvents()
        );

        // Step 2: Allocate study blocks
        AllocationResult allocation = blockAllocationService.allocateStudyBlocks(
            inputs.userId(),
            inputs.profile(),
            gridResult.freeSlots(),
            inputs.targetStudyMinutes(),
            null // subjectId - can be parameterized if needed
        );

        // Step 3: Combine and sort all blocks
        List<Block> allBlocks = new ArrayList<>();
        allBlocks.addAll(gridResult.fixedBlocks());
        allBlocks.addAll(allocation.blocks());
        allBlocks.sort(Comparator.comparing(Block::getStartsAt));

        // Step 4: Create plan
        DayPlan plan = DayPlan.builder()
            .userId(inputs.userId())
            .day(inputs.day())
            .blocks(allBlocks)
            .build();

        // Step 5: Validate
        validationService.validateDayPlan(inputs.profile(), plan);

        return plan;
    }
}
