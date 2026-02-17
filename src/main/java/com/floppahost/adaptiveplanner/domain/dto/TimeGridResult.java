package com.floppahost.adaptiveplanner.domain.dto;

import com.floppahost.adaptiveplanner.domain.model.Block;
import com.floppahost.adaptiveplanner.domain.value.Slot;

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
