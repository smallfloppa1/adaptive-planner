package com.floppahost.adaptiveplanner.planner.domain.planning.engine;


import com.floppahost.adaptiveplanner.planner.domain.planning.Block;
import com.floppahost.adaptiveplanner.planner.domain.planning.DayPlan;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class PlanningEngine {

    private final TimeGridService timeGridService;
    private final BlockAllocationService blockAllocationService;
    private final ScheduleValidationService validationService;

    public PlanningEngine(
            TimeGridService timeGridService,
            BlockAllocationService blockAllocationService,
            ScheduleValidationService validationService
    ) {
        this.timeGridService = timeGridService;
        this.blockAllocationService = blockAllocationService;
        this.validationService = validationService;
    }

    public DayPlan generateDayPlan(PlanInputs inputs) {
        TimeGridResult gridResult = timeGridService.computeDaySlotsAndFixedBlocks(
                inputs.userId(),
                inputs.profile(),
                inputs.day(),
                inputs.commitments()
        );

        AllocationResult allocation = blockAllocationService.allocateStudyBlocks(
                inputs.userId(),
                inputs.profile(),
                gridResult.freeSlots(),
                inputs.targetStudyMinutes(),
                null
        );

        List<Block> allBlocks = new ArrayList<>();
        allBlocks.addAll(gridResult.fixedBlocks());
        allBlocks.addAll(allocation.blocks());
        allBlocks.sort(Comparator.comparing(b -> b.getTimeRange().getStart()));

        DayPlan plan = DayPlan.builder()
                .userId(inputs.userId())
                .day(inputs.day())
                .blocks(allBlocks)
                .build();

        validationService.validateDayPlan(inputs.profile(), plan);

        return plan;
    }
}
