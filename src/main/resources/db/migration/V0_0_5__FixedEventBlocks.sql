CREATE TABLE fixed_event_blocks
(
    id             UUID PRIMARY KEY,
    fixed_event_id UUID        NOT NULL,
    day_of_week    day_of_week NOT NULL,
    start_time     TIME        NOT NULL,
    end_time       TIME        NOT NULL,

    CONSTRAINT fk_fixed_event_blocks_fixed_event
        FOREIGN KEY (fixed_event_id)
            REFERENCES fixed_events (id)
            ON DELETE CASCADE,

    CONSTRAINT chk_time_order CHECK (start_time < end_time),
    CONSTRAINT uq_event_block UNIQUE (fixed_event_id, day_of_week, start_time, end_time)
);