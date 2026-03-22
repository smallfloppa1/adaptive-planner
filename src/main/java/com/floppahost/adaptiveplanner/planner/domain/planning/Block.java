package com.floppahost.adaptiveplanner.planner.domain.planning;

import com.floppahost.adaptiveplanner.planner.domain.shared.LocalDateTimeRange;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

@Getter
public class Block {
    private final UUID id;
    private final UUID userId;
    private final BlockKind kind;
    private final String title;
    private final LocalDateTimeRange timeRange;
    private final BlockRef ref;
    private BlockStatus status;
    private LocalDateTime completedAt;
    private LocalDateTime skippedAt;
    private LocalDateTime snoozedUntil;

    private Block(UUID id, UUID userId, BlockKind kind, String title, LocalDateTimeRange timeRange, BlockStatus status, BlockRef ref, LocalDateTime completedAt, LocalDateTime skippedAt, LocalDateTime snoozedUntil) {
        this.id = Objects.requireNonNull(id, "Block ID cannot be null");
        this.userId = Objects.requireNonNull(userId, "User ID cannot be null");
        this.kind = Objects.requireNonNull(kind, "Block kind cannot be null");
        this.title = Objects.requireNonNull(title, "Title cannot be null");
        this.timeRange = Objects.requireNonNull(timeRange, "Time range cannot be null");
        this.status = Objects.requireNonNull(status, "Block status cannot be null");
        this.ref = Objects.requireNonNull(ref, "Block reference cannot be null");
        this.completedAt = completedAt;
        this.skippedAt = skippedAt;
        this.snoozedUntil = snoozedUntil;
    }

    public static Block create(UUID userId, BlockKind kind, String title, LocalDateTimeRange localDateTimeRange, BlockRef ref) {
        UUID newBlockId = UUID.randomUUID();
        BlockStatus newBlockStatus = BlockStatus.PLANNED;

        return new Block(
                newBlockId,
                userId,
                kind,
                title,
                localDateTimeRange,
                newBlockStatus,
                ref,
                null,
                null,
                null
        );
    }

    public void complete() {
        // todo: add status validation
        this.status = BlockStatus.DONE;
        this.completedAt = LocalDateTime.now();
    }

    public void skip() {
        // todo: add status validation
        this.status = BlockStatus.SKIPPED;
        this.skippedAt = LocalDateTime.now();
    }

    public int getPlannedMinutes() {
        return (int) timeRange.getDuration().toMinutes();
    }
}
