package com.floppahost.adaptiveplanner.planner.domain.planning.engine;

import com.floppahost.adaptiveplanner.planner.domain.planning.Block;

import java.util.List;

public record AllocationResult(
        List<Block> blocks,
        int usedFlexibleMinutes,
        int heavyBlocksUsed
) {
}
