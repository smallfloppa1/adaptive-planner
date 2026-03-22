package com.floppahost.adaptiveplanner.planner.domain.service;

import com.floppahost.adaptiveplanner.planner.domain.planning.engine.AllocationResult;
import com.floppahost.adaptiveplanner.planner.domain.planning.engine.BlockAllocationService;
import com.floppahost.adaptiveplanner.planner.domain.planning.Block;
import com.floppahost.adaptiveplanner.planner.domain.planning.BlockKind;
import com.floppahost.adaptiveplanner.planner.domain.planning.Slot;
import com.floppahost.adaptiveplanner.planner.domain.user.UserProfile;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("BlockAllocationService Tests")
class BlockAllocationServiceTest {

    private BlockAllocationService allocator = new BlockAllocationService();
    private UserProfile profile;
    private UUID userId = UUID.fromString("5daaa33e-2bd5-47ea-a097-6b1e2e40448a");
    private UUID subjectId = UUID.fromString("b3181734-f174-4c8d-9151-ef48b604cb13");
    private LocalDate testDay = LocalDate.of(2024, 2, 19);

    @BeforeEach
    void setUp() {
        LocalTime wakeTime = LocalTime.of(7, 0);
        LocalTime sleepTime = LocalTime.of(23, 0);
        double minSleepHours = 7;
        int morningRoutineMinutes = 60;
        int eveningRoutineMinutes = 60;
        int focusMinutes = 40;
        int breakMinutes = 10;
        int maxHeavyBlocksPerDay = 3;
        int maxTotalPlannedMinutesPerDay = 8 * 60;
        int weeklyStudyTargetMinutes = 10 * 60;
        boolean strictEnforcement = true;

        profile = new UserProfile(
                wakeTime,
                sleepTime,
                minSleepHours,
                morningRoutineMinutes,
                eveningRoutineMinutes,
                focusMinutes,
                breakMinutes,
                maxHeavyBlocksPerDay,
                maxTotalPlannedMinutesPerDay,
                weeklyStudyTargetMinutes,
                strictEnforcement
        );
    }

    @Test
    @DisplayName("Should allocate study and break blocks in single free slot")
    void shouldAllocateStudyAndBreakBlocks() {
        // Given
        List<Slot> freeSlots = List.of(
            createSlot(9, 0, 13, 0)
        );

        // When
        AllocationResult result = allocator.allocateStudyBlocks(
            userId, profile, freeSlots, 200, null
        );

        // Then
        assertThat(result.blocks()).hasSize(8);
        assertThat(result.usedFlexibleMinutes()).isEqualTo(200); // 160 study + 40 break
        assertThat(result.heavyBlocksUsed()).isEqualTo(4);

        // Verify alternating pattern
        List<Block> blocks = result.blocks();
        for (int i = 0; i < blocks.size(); i += 2) {
            assertThat(blocks.get(i).getKind()).isEqualTo(BlockKind.STUDY);
            assertThat(blocks.get(i + 1).getKind()).isEqualTo(BlockKind.BREAK);
        }
    }

    @Test
    @DisplayName("Should respect max heavy blocks limit")
    void shouldRespectMaxHeavyBlocksLimit() {
        // Given
        UserProfile limitedProfile = profile.withMaxHeavyBlocksPerDay(2);

        List<Slot> freeSlots = List.of(
            createSlot(9, 0, 18, 0) // 9 hours available
        );

        // When - Request more than limit allows
        AllocationResult result = allocator.allocateStudyBlocks(
            userId, limitedProfile, freeSlots, 500, null
        );

        // Then - Should stop at max heavy blocks (2)
        assertThat(result.heavyBlocksUsed()).isEqualTo(2);
        
        long studyBlocks = result.blocks().stream()
            .filter(b -> b.getKind() == BlockKind.STUDY)
            .count();
        assertThat(studyBlocks).isEqualTo(2);
    }

    @Test
    @DisplayName("Should respect max flexible minutes limit")
    void shouldRespectMaxFlexibleMinutesLimit() {
        // Given
        UserProfile limitedProfile = profile.withMaxTotalPlannedMinutesPerDay(150);

        List<Slot> freeSlots = List.of(
            createSlot(9, 0, 18, 0)
        );

        // When - Request more than cap
        AllocationResult result = allocator.allocateStudyBlocks(
            userId, limitedProfile, freeSlots, 300, null
        );

        // Then - Should cap at 150 minutes
        assertThat(result.usedFlexibleMinutes()).isLessThanOrEqualTo(150);
    }

    @Test
    @DisplayName("Should allocate across multiple free slots")
    void shouldAllocateAcrossMultipleSlots() {
        // Given - Two separate free slots
        List<Slot> freeSlots = List.of(
            createSlot(9, 0, 10, 0),   // 60 minutes
            createSlot(14, 0, 16, 0)   // 120 minutes
        );

        // When
        AllocationResult result = allocator.allocateStudyBlocks(
            userId, profile, freeSlots, 150, null
        );

        // Then - Should use both slots
        assertThat(result.blocks()).isNotEmpty();
        assertThat(result.usedFlexibleMinutes()).isGreaterThan(0);
        
        // Verify blocks span across time gaps
        List<LocalDateTime> startTimes = result.blocks().stream()
            .map(b -> b.getTimeRange().getStart())
            .sorted()
            .toList();
        
        assertThat(startTimes).hasSizeGreaterThan(1);
    }

    @Test
    @DisplayName("Should not allocate partial focus blocks")
    void shouldNotAllocatePartialFocusBlocks() {
        // Given - Slot that can't fit full focus block
        List<Slot> freeSlots = List.of(
            createSlot(9, 0, 9, 30) // Only 30 minutes, less than 40 min focus
        );

        // When
        AllocationResult result = allocator.allocateStudyBlocks(
            userId, profile, freeSlots, 100, null
        );

        // Then - Should not allocate anything
        assertThat(result.blocks()).isEmpty();
        assertThat(result.usedFlexibleMinutes()).isEqualTo(0);
        assertThat(result.heavyBlocksUsed()).isEqualTo(0);
    }

    @Test
    @DisplayName("Should allocate focus block without break if no space for break")
    void shouldAllocateFocusWithoutBreakIfNoSpace() {
        // Given - Exactly 40 minutes (enough for focus, not for break)
        List<Slot> freeSlots = List.of(
            createSlot(9, 0, 9, 40)
        );

        // When
        AllocationResult result = allocator.allocateStudyBlocks(
            userId, profile, freeSlots, 40, null
        );

        // Then - Should allocate study block but no break
        assertThat(result.blocks()).hasSize(1);
        assertThat(result.blocks().getFirst().getKind()).isEqualTo(BlockKind.STUDY);
        assertThat(result.usedFlexibleMinutes()).isEqualTo(40);
    }

    @Test
    @DisplayName("Should attach subject ID to study blocks when provided")
    void shouldAttachSubjectIdWhenProvided() {
        // Given
        List<Slot> freeSlots = List.of(
            createSlot(9, 0, 10, 0)
        );

        // When
        AllocationResult result = allocator.allocateStudyBlocks(
            userId, profile, freeSlots, 40, subjectId
        );

        // Then
        List<Block> studyBlocks = result.blocks().stream()
            .filter(b -> b.getKind() == BlockKind.STUDY)
            .toList();

        assertThat(studyBlocks).isNotEmpty();
        assertThat(studyBlocks.getFirst().getRef().getSubjectId()).isEqualTo(subjectId);
    }

    @Test
    @DisplayName("Should not attach subject ID when null")
    void shouldNotAttachSubjectIdWhenNull() {
        // Given
        List<Slot> freeSlots = List.of(
            createSlot(9, 0, 10, 0)
        );

        // When
        AllocationResult result = allocator.allocateStudyBlocks(
            userId, profile, freeSlots, 40, null
        );

        // Then
        List<Block> studyBlocks = result.blocks().stream()
            .filter(b -> b.getKind() == BlockKind.STUDY)
            .toList();

        assertThat(studyBlocks).isNotEmpty();
        assertThat(studyBlocks.getFirst().ref().getSubjectId()).isNull();
    }

    @Test
    @DisplayName("Should handle profile with zero break minutes")
    void shouldHandleZeroBreakMinutes() {
        // Given
        UserProfile noBreakProfile = profile.withFocusCycle(
                profile.focusMinutes(),
                0
        );
        List<Slot> freeSlots = List.of(
            createSlot(9, 0, 11, 0)
        );

        // When
        AllocationResult result = allocator.allocateStudyBlocks(
            userId, noBreakProfile, freeSlots, 80, null
        );

        // Then - Should only have study blocks, no breaks
        assertThat(result.blocks()).allMatch(b -> b.getKind() == BlockKind.STUDY);
        assertThat(result.blocks()).hasSize(2); // Two 40-min blocks
    }

    @Test
    @DisplayName("Should stop when target reached")
    void shouldStopWhenTargetReached() {
        // Given - Large slot but small target
        List<Slot> freeSlots = List.of(
            createSlot(9, 0, 18, 0) // 9 hours
        );

        // When - Only request 100 minutes
        AllocationResult result = allocator.allocateStudyBlocks(
            userId, profile, freeSlots, 100, null
        );

        // Then - Should stop at ~100 minutes, not fill entire slot
        assertThat(result.usedFlexibleMinutes()).isLessThanOrEqualTo(110);
    }

    @Test
    @DisplayName("Should handle empty free slots list")
    void shouldHandleEmptyFreeSlots() {
        // Given
        List<Slot> freeSlots = List.of();

        // When
        AllocationResult result = allocator.allocateStudyBlocks(
            userId, profile, freeSlots, 100, null
        );

        // Then
        assertThat(result.blocks()).isEmpty();
        assertThat(result.usedFlexibleMinutes()).isEqualTo(0);
        assertThat(result.heavyBlocksUsed()).isEqualTo(0);
    }

    @Test
    @DisplayName("Should handle zero target minutes")
    void shouldHandleZeroTarget() {
        // Given
        List<Slot> freeSlots = List.of(
            createSlot(9, 0, 18, 0)
        );

        // When
        AllocationResult result = allocator.allocateStudyBlocks(
            userId, profile, freeSlots, 0, null
        );

        // Then
        assertThat(result.blocks()).isEmpty();
        assertThat(result.usedFlexibleMinutes()).isEqualTo(0);
    }

    @Test
    @DisplayName("Should not exceed flexible cap even if target is higher")
    void shouldCapTargetAtFlexibleLimit() {
        // Given
        List<Slot> freeSlots = List.of(
            createSlot(9, 0, 18, 0)
        );

        // When - Request more than flexible cap
        AllocationResult result = allocator.allocateStudyBlocks(
            userId, profile, freeSlots, 1000, null
        );

        // Then - Should cap at profile limit (480)
        assertThat(result.usedFlexibleMinutes()).isLessThanOrEqualTo(480);
    }

    @Test
    @DisplayName("Should create blocks with correct user ID")
    void shouldCreateBlocksWithCorrectUserId() {
        // Given
        List<Slot> freeSlots = List.of(
            createSlot(9, 0, 10, 0)
        );

        // When
        AllocationResult result = allocator.allocateStudyBlocks(
            userId, profile, freeSlots, 50, null
        );

        // Then
        assertThat(result.blocks()).allMatch(b -> b.getUserId().equals(userId));
    }

    @Test
    @DisplayName("Should create non-overlapping blocks")
    void shouldCreateNonOverlappingBlocks() {
        // Given
        List<Slot> freeSlots = List.of(
            createSlot(9, 0, 13, 0)
        );

        // When
        AllocationResult result = allocator.allocateStudyBlocks(
            userId, profile, freeSlots, 200, null
        );

        // Then - Verify no overlaps
        List<Block> blocks = result.blocks();
        for (int i = 0; i < blocks.size() - 1; i++) {
            Block current = blocks.get(i);
            Block next = blocks.get(i + 1);
            assertThat(current.getTimeRange().getEnd())
                .isBeforeOrEqualTo(next.getTimeRange().getStart());
        }
    }

    @Test
    @DisplayName("Should allocate all blocks within free slot boundaries")
    void shouldAllocateWithinSlotBoundaries() {
        // Given
        Slot slot = createSlot(9, 0, 12, 0);
        List<Slot> freeSlots = List.of(slot);

        // When
        AllocationResult result = allocator.allocateStudyBlocks(
            userId, profile, freeSlots, 150, null
        );

        // Then - All blocks should be within slot
        assertThat(result.blocks()).allMatch(block ->
            !block.getTimeRange().getStart().isBefore(slot.start()) &&
            !block.getTimeRange().getEnd().isAfter(slot.end())
        );
    }

    @Test
    @DisplayName("Should handle very small free slot")
    void shouldHandleVerySmallSlot() {
        // Given - Only 5 minutes
        List<Slot> freeSlots = List.of(
            createSlot(9, 0, 9, 5)
        );

        // When
        AllocationResult result = allocator.allocateStudyBlocks(
            userId, profile, freeSlots, 50, null
        );

        // Then - Can't fit any blocks
        assertThat(result.blocks()).isEmpty();
    }

    private Slot createSlot(int startHour, int startMinute, int endHour, int endMinute) {
        LocalDateTime start = LocalDateTime.of(testDay, LocalTime.of(startHour, startMinute));
        LocalDateTime end = LocalDateTime.of(testDay, LocalTime.of(endHour, endMinute));
        if (!end.isAfter(start)) end = end.plusDays(1);
        return new Slot(start, end);
    }
}
