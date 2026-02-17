package com.floppahost.adaptiveplanner.planner.domain.dto;

import com.floppahost.adaptiveplanner.planner.domain.model.Block;
import com.floppahost.adaptiveplanner.planner.domain.value.Slot;

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
