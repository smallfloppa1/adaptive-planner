package com.floppahost.adaptiveplanner.planner.domain.service;

import com.floppahost.adaptiveplanner.planner.domain.dto.TimeGridResult;
import com.floppahost.adaptiveplanner.planner.domain.model.Block;
import com.floppahost.adaptiveplanner.planner.domain.model.FixedEvent;
import com.floppahost.adaptiveplanner.planner.domain.value.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("TimeGridService Tests")
class TimeGridServiceTest {

    private TimeGridService service;
    private UserProfile profile;
    private UUID userId;
    private LocalDate monday;

    @BeforeEach
    void setUp() {
        service = new TimeGridService();
        userId = UUID.randomUUID();
        monday = LocalDate.of(2024, 2, 19);

        profile = new UserProfile(
                LocalTime.of(7, 0),
                LocalTime.of(23, 0),
                7.0,
                40,
                10,
                6,
                8 * 60,
                10 * 60,
                true
        );
    }

    @Test
    @DisplayName("Should build correct day window")
    void shouldBuildDayWindow() {
        Slot window = service.buildDayWindow(profile, monday);

        assertThat(window.start())
                .isEqualTo(LocalDateTime.of(monday, LocalTime.of(7, 0)));

        assertThat(window.end())
                .isEqualTo(LocalDateTime.of(monday, LocalTime.of(23, 0)));
    }

    @Test
    @DisplayName("Should expand recurring fixed event")
    void shouldExpandRecurringEvent() {
        FixedEvent event = FixedEvent.builder()
                .userId(userId)
                .kind(FixedEventKind.CLASS)
                .title("Math")
                .weekday(DayOfWeek.MONDAY)
                .recurringTimeRange(new TimeRange(
                        LocalTime.of(9, 0),
                        LocalTime.of(11, 0)
                ))
                .build();

        var expanded = service.expandFixedEventsForDay(profile, monday, List.of(event));

        assertThat(expanded).hasSize(1);

        Slot slot = expanded.getFirst().getValue();
        assertThat(slot.start()).isEqualTo(LocalDateTime.of(monday, LocalTime.of(9, 0)));
        assertThat(slot.end()).isEqualTo(LocalDateTime.of(monday, LocalTime.of(11, 0)));
    }

    @Test
    @DisplayName("Should convert fixed event to block")
    void shouldConvertFixedEventToBlock() {
        FixedEvent event = FixedEvent.builder()
                .id(UUID.randomUUID())
                .userId(userId)
                .kind(FixedEventKind.CLASS)
                .title("Physics")
                .weekday(DayOfWeek.MONDAY)
                .recurringTimeRange(new TimeRange(
                        LocalTime.of(10, 0),
                        LocalTime.of(12, 0)
                ))
                .build();

        Slot slot = new Slot(
                LocalDateTime.of(monday, LocalTime.of(10, 0)),
                LocalDateTime.of(monday, LocalTime.of(12, 0))
        );

        Block block = service.fixedEventToBlock(userId, event, slot);

        assertThat(block.getKind()).isEqualTo(BlockKind.CLASS);
        assertThat(block.getStartsAt()).isEqualTo(slot.start());
        assertThat(block.getEndsAt()).isEqualTo(slot.end());
        assertThat(block.getRef().getFixedEventId()).isEqualTo(event.getId());
    }

    @Test
    @DisplayName("Should compute free slots correctly")
    void shouldComputeFreeSlots() {
        FixedEvent event = FixedEvent.builder()
                .userId(userId)
                .kind(FixedEventKind.CLASS)
                .title("Math")
                .weekday(DayOfWeek.MONDAY)
                .recurringTimeRange(new TimeRange(
                        LocalTime.of(9, 0),
                        LocalTime.of(11, 0)
                ))
                .build();

        TimeGridResult result = service.computeDaySlotsAndFixedBlocks(
                userId,
                profile,
                monday,
                List.of(event)
        );

        assertThat(result.freeSlots()).hasSize(2);

        Slot first = result.freeSlots().get(0);
        Slot second = result.freeSlots().get(1);

        assertThat(first.start()).isEqualTo(LocalDateTime.of(monday, LocalTime.of(7, 0)));
        assertThat(first.end()).isEqualTo(LocalDateTime.of(monday, LocalTime.of(9, 0)));

        assertThat(second.start()).isEqualTo(LocalDateTime.of(monday, LocalTime.of(11, 0)));
        assertThat(second.end()).isEqualTo(LocalDateTime.of(monday, LocalTime.of(23, 0)));
    }

    @Test
    @DisplayName("Should merge overlapping fixed events")
    void shouldMergeOverlappingEvents() {
        List<FixedEvent> events = List.of(
                createEvent(9, 0, 11, 0),
                createEvent(10, 30, 12, 0)
        );

        TimeGridResult result = service.computeDaySlotsAndFixedBlocks(
                userId,
                profile,
                monday,
                events
        );

        assertThat(result.fixedBlocks()).hasSize(2);
        assertThat(result.freeSlots()).hasSize(2);

        // Expect merged busy interval 09:00–12:00
        Slot free1 = result.freeSlots().get(0);
        Slot free2 = result.freeSlots().get(1);

        assertThat(free1.start()).isEqualTo(LocalDateTime.of(monday, LocalTime.of(7, 0)));
        assertThat(free1.end()).isEqualTo(LocalDateTime.of(monday, LocalTime.of(9, 0)));

        assertThat(free2.start()).isEqualTo(LocalDateTime.of(monday, LocalTime.of(12, 0)));
        assertThat(free2.end()).isEqualTo(LocalDateTime.of(monday, LocalTime.of(23, 0)));
    }

    private FixedEvent createEvent(int sh, int sm, int eh, int em) {
        return FixedEvent.builder()
                .userId(userId)
                .kind(FixedEventKind.CLASS)
                .title("Event")
                .weekday(DayOfWeek.MONDAY)
                .recurringTimeRange(new TimeRange(
                        LocalTime.of(sh, sm),
                        LocalTime.of(eh, em)
                ))
                .build();
    }
}
