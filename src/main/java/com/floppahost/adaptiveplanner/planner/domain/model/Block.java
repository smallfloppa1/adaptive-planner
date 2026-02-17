package com.floppahost.adaptiveplanner.planner.domain.model;

import com.floppahost.adaptiveplanner.planner.domain.value.BlockKind;
import com.floppahost.adaptiveplanner.planner.domain.value.BlockStatus;
import lombok.Builder;
import lombok.Getter;
import lombok.With;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Builder
@With
public class Block {
    private final UUID userId;
    private final BlockKind kind;
    private final String title;
    private final LocalDateTime startsAt;
    private final LocalDateTime endsAt;

    @Builder.Default
    private final UUID id = UUID.randomUUID();

    @Builder.Default
    private final BlockStatus status = BlockStatus.PLANNED;

    @Builder.Default
    private final BlockRef ref = BlockRef.empty();

    private final LocalDateTime completedAt;
    private final LocalDateTime skippedAt;
    private final LocalDateTime snoozedUntil;

    public Block(
            UUID userId,
            BlockKind kind,
            String title,
            LocalDateTime startsAt,
            LocalDateTime endsAt,
            UUID id,
            BlockStatus status,
            BlockRef ref,
            LocalDateTime completedAt,
            LocalDateTime skippedAt,
            LocalDateTime snoozedUntil
    ) {
        this.userId = userId;
        this.kind = kind;
        this.title = title;
        this.startsAt = startsAt;
        this.endsAt = endsAt;
        this.id = id;
        this.status = status;
        this.ref = ref;
        this.completedAt = completedAt;
        this.skippedAt = skippedAt;
        this.snoozedUntil = snoozedUntil;
    }

    public int getPlannedMinutes() {
        return (int) Duration.between(startsAt, endsAt).toMinutes();
    }
}
