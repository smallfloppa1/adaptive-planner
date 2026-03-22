package com.floppahost.adaptiveplanner.planner.domain.planning.engine;

import com.floppahost.adaptiveplanner.planner.domain.planning.Block;
import com.floppahost.adaptiveplanner.planner.domain.planning.Slot;

import java.util.List;

public record TimeGridResult(
        Slot dayWindow,
        List<Block> fixedBlocks,
        List<Slot> freeSlots
) {
}
