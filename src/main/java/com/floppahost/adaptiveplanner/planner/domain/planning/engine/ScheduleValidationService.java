package com.floppahost.adaptiveplanner.planner.domain.planning.engine;


import com.floppahost.adaptiveplanner.planner.domain.planning.Block;
import com.floppahost.adaptiveplanner.planner.domain.planning.BlockKind;
import com.floppahost.adaptiveplanner.planner.domain.planning.DayPlan;
import com.floppahost.adaptiveplanner.planner.domain.shared.LocalDateTimeRange;
import com.floppahost.adaptiveplanner.planner.domain.user.UserProfile;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Set;

public class ScheduleValidationService {

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

    public void validateDayPlan(UserProfile profile, DayPlan plan) {
        LocalDateTime windowStart = LocalDateTime.of(plan.getDay(), profile.wakeTime());
        LocalDateTime windowEnd = LocalDateTime.of(plan.getDay(), profile.sleepTime());

        // Optional: support sleep after midnight
        if (windowEnd.isBefore(windowStart)) {
            windowEnd = windowEnd.plusDays(1);
        }

        List<Block> blocks = plan.getBlocks().stream()
                .sorted(Comparator.comparing(b -> b.getTimeRange().getStart()))
                .toList();

        LocalDateTime prevEnd = null;
        int heavyCount = 0;
        int flexMinutes = 0;

        for (Block block : blocks) {
            if (block.getTimeRange().isWithin(LocalDateTimeRange.of(windowStart, windowEnd))) {
                throw new PlanValidationException("Block outside planning window.");
            }

            if (prevEnd != null && block.getTimeRange().getStart().isBefore(prevEnd)) {
                throw new PlanValidationException("Blocks overlap.");
            }

            if (HEAVY_KINDS.contains(block.getKind())) {
                heavyCount++;
            }

            if (FLEX_KINDS.contains(block.getKind())) {
                flexMinutes += block.getPlannedMinutes();
            }

            prevEnd = block.getTimeRange().getEnd();
        }

        if (heavyCount > profile.maxHeavyBlocksPerDay()) {
            throw new PlanValidationException("Exceeded max heavy blocks per day.");
        }

        if (flexMinutes > profile.maxTotalPlannedMinutesPerDay()) {
            throw new PlanValidationException("Exceeded max flexible planned minutes per day.");
        }
    }
}
