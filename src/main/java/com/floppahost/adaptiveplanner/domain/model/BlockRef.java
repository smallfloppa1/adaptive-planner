package com.floppahost.adaptiveplanner.domain.model;

import lombok.Builder;
import lombok.Value;
import lombok.With;

import java.util.UUID;

/**
 * Reference to the thing a Block represents.
 * Keep refs optional because different BlockKind uses different refs.
 */
@Value
@Builder
@With
public class BlockRef {
    UUID fixedEventId;
    UUID subjectId;
    UUID examId;
    UUID taskId;
    UUID programId;
    UUID programItemId;

    public static BlockRef empty() {
        return BlockRef.builder().build();
    }
}
