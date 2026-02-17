package com.floppahost.adaptiveplanner.domain.service;


import com.floppahost.adaptiveplanner.domain.dto.AllocationResult;
import com.floppahost.adaptiveplanner.domain.model.Block;
import com.floppahost.adaptiveplanner.domain.model.BlockRef;
import com.floppahost.adaptiveplanner.domain.value.Slot;
import com.floppahost.adaptiveplanner.domain.model.UserProfile;
import com.floppahost.adaptiveplanner.domain.value.BlockKind;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;

/**
 * Allocates study and break blocks into available time slots.
 * Respects user profile constraints for heavy blocks and flexible minutes.
 */
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

    /**
     * Allocate study blocks and break blocks in free slots.
     * Constraints enforced:
     * - profile.maxHeavyBlocksPerDay (counts STUDY blocks)
     * - profile.maxTotalPlannedMinutesPerDay (counts flexible blocks only)
     * 
     * @param userId User ID
     * @param profile User profile with constraints
     * @param freeSlots Available time slots
     * @param targetStudyMinutes Desired study minutes
     * @param subjectId Optional subject ID for study blocks
     * @return Allocation result with blocks and metrics
     */
    public AllocationResult allocateStudyBlocks(
            UUID userId,
            UserProfile profile,
            List<Slot> freeSlots,
            int targetStudyMinutes,
            UUID subjectId) {
        
        List<Block> blocks = new ArrayList<>();
        int usedFlexMinutes = 0;
        int heavyBlocksUsed = 0;

        int focusMinutes = profile.getFocusMinutes();
        int breakMinutes = profile.getBreakMinutes();
        int flexCap = profile.getMaxTotalPlannedMinutesPerDay();
        int maxHeavy = profile.getMaxHeavyBlocksPerDay();

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
                    ? BlockRef.builder().subjectId(subjectId).build()
                    : BlockRef.empty();

                blocks.add(Block.builder()
                    .userId(userId)
                    .kind(BlockKind.STUDY)
                    .title("Study")
                    .startsAt(cursor)
                    .endsAt(focusEnd)
                    .ref(ref)
                    .build());

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

                    blocks.add(Block.builder()
                        .userId(userId)
                        .kind(BlockKind.BREAK)
                        .title("Break")
                        .startsAt(cursor)
                        .endsAt(breakEnd)
                        .ref(BlockRef.empty())
                        .build());

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
