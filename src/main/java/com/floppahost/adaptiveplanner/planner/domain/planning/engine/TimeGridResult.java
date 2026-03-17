package com.floppahost.adaptiveplanner.planner.domain.planning.engine;

import com.floppahost.adaptiveplanner.planner.domain.planning.Block;
import com.floppahost.adaptiveplanner.planner.domain.planning.Slot;

import java.util.List;

/**
 * Result of time grid computation.
 * Contains day window, fixed blocks, and free slots.
 */
public record TimeGridResult(
        Slot dayWindow,
        List<Block> fixedBlocks,
        List<Slot> freeSlots
) {
}
