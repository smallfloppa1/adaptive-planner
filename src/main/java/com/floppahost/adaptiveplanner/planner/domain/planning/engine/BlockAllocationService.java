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

    public AllocationResult allocateStudyBlocks(
            UUID userId,
            UserProfile profile,
            List<Slot> freeSlots,
            int targetStudyMinutes,
            UUID subjectId
    ) {

        List<Block> blocks = new ArrayList<>();
        int achievedStudyMinutes = 0;
        int usedFlexMinutes = 0;
        int heavyBlocksUsed = 0;

        int focusMinutes = profile.focusMinutes();
        int breakMinutes = profile.breakMinutes();
        int flexCap = profile.maxTotalPlannedMinutesPerDay();
        int maxHeavy = profile.maxHeavyBlocksPerDay();

        int target = Math.min(targetStudyMinutes, flexCap);

        for (Slot slot : freeSlots) {
            LocalDateTime cursor = slot.start();

            while (achievedStudyMinutes < target && usedFlexMinutes < flexCap) {

                if (heavyBlocksUsed >= maxHeavy) {
                    return new AllocationResult(blocks, usedFlexMinutes, heavyBlocksUsed);
                }

                int currentFocusMinutes = Math.min(focusMinutes, target - achievedStudyMinutes);
                currentFocusMinutes = Math.min(currentFocusMinutes, flexCap - usedFlexMinutes);

                LocalDateTime focusEnd = cursor.plus(Duration.ofMinutes(currentFocusMinutes));
                if (focusEnd.isAfter(slot.end())) {
                    break;
                }

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

                achievedStudyMinutes += currentFocusMinutes;
                usedFlexMinutes += currentFocusMinutes;
                heavyBlocksUsed++;
                cursor = focusEnd;

                if (breakMinutes > 0 && achievedStudyMinutes < target && usedFlexMinutes < flexCap) {

                    int currentBreakMinutes = Math.min(breakMinutes, flexCap - usedFlexMinutes);
                    LocalDateTime breakEnd = cursor.plus(Duration.ofMinutes(currentBreakMinutes));

                    if (breakEnd.isAfter(slot.end())) {
                        break;
                    }

                    blocks.add(Block.create(
                            userId,
                            BlockKind.BREAK,
                            "Break",
                            LocalDateTimeRange.of(cursor, breakEnd),
                            new BlockRef.Empty()
                    ));

                    usedFlexMinutes += currentBreakMinutes;
                    cursor = breakEnd;
                }
            }

            if (achievedStudyMinutes >= target || usedFlexMinutes >= flexCap) {
                break;
            }
        }

        return new AllocationResult(blocks, usedFlexMinutes, heavyBlocksUsed);
    }
}