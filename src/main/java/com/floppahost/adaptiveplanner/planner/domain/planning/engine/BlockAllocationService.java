package com.floppahost.adaptiveplanner.planner.domain.planning.engine;

import com.floppahost.adaptiveplanner.planner.domain.planning.Block;
import com.floppahost.adaptiveplanner.planner.domain.planning.BlockKind;
import com.floppahost.adaptiveplanner.planner.domain.planning.BlockRef;
import com.floppahost.adaptiveplanner.planner.domain.planning.Slot;
import com.floppahost.adaptiveplanner.planner.domain.shared.LocalDateTimeRange;
import com.floppahost.adaptiveplanner.planner.domain.user.UserProfile;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;

public class BlockAllocationService {

    private static final Set<BlockKind> FLEX_KINDS = Set.of(
        BlockKind.STUDY,
        BlockKind.TASK,
        BlockKind.PROGRAM,
        BlockKind.BREAK
    );

    private static final Set<BlockKind> HEAVY_KINDS = Set.of(
        BlockKind.STUDY,
        BlockKind.TASK,
        BlockKind.PROGRAM
    );

    public AllocationResult allocateStudyBlocks(
            UUID userId,
            UserProfile profile,
            List<Slot> freeSlots,
            int targetStudyMinutes,
            UUID subjectId
    ) {
        
        List<Block> blocks = new ArrayList<>();
        int usedFlexMinutes = 0;
        int heavyBlocksUsed = 0;

        int focusMinutes = profile.focusMinutes();
        int breakMinutes = profile.breakMinutes();
        int flexCap = profile.maxTotalPlannedMinutesPerDay();
        int maxHeavy = profile.maxHeavyBlocksPerDay();

        int target = Math.min(targetStudyMinutes, flexCap);

        for (Slot slot : freeSlots) {
            LocalDateTime cursor = slot.start();

            while (usedFlexMinutes < target) {
                // Check heavy blocks limit
                if (heavyBlocksUsed >= maxHeavy) {
                    return new AllocationResult(blocks, usedFlexMinutes, heavyBlocksUsed);
                }

                // Try to fit a focus block
                LocalDateTime focusEnd = cursor.plus(Duration.ofMinutes(focusMinutes));
                if (focusEnd.isAfter(slot.end())) {
                    break;
                }

                // Create study block
                BlockRef ref = subjectId != null 
                    ? new BlockRef.ForSubject(subjectId)
                    : new BlockRef.Empty();

                blocks.add(Block.create(
                        userId,
                        BlockKind.STUDY,
                        "Study",
                        LocalDateTimeRange.of(cursor, focusEnd),
                        ref
                ));

                usedFlexMinutes += focusMinutes;
                heavyBlocksUsed++;
                cursor = focusEnd;

                // Try to add a break block
                if (breakMinutes > 0 && usedFlexMinutes < target) {
                    LocalDateTime breakEnd = cursor.plus(Duration.ofMinutes(breakMinutes));
                    
                    if (breakEnd.isAfter(slot.end())) {
                        break;
                    }
                    
                    if (usedFlexMinutes + breakMinutes > flexCap) {
                        break;
                    }

                    blocks.add(Block.create(
                            userId,
                            BlockKind.BREAK,
                            "Break",
                            LocalDateTimeRange.of(cursor, breakEnd),
                            new BlockRef.Empty()
                    ));

                    usedFlexMinutes += breakMinutes;
                    cursor = breakEnd;
                }
            }

            if (usedFlexMinutes >= target) {
                break;
            }
        }

        return new AllocationResult(blocks, usedFlexMinutes, heavyBlocksUsed);
    }
}
