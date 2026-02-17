package com.floppahost.adaptiveplanner.planner.domain.dto;

import com.floppahost.adaptiveplanner.planner.domain.model.Block;

import java.util.List;

/**
 * Result of block allocation operation.
 * Contains allocated blocks and usage metrics.
 */
public record AllocationResult(
        List<Block> blocks,
        int usedFlexibleMinutes,
        int heavyBlocksUsed
) {
}
