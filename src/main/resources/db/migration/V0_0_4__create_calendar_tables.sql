CREATE TABLE base_events
(
    id         UUID PRIMARY KEY,
    user_id    UUID                     NOT NULL,
    title      VARCHAR(255)             NOT NULL,
    kind       VARCHAR(50)              NOT NULL,
    location   VARCHAR(255),
    notes      TEXT,

    created_at TIMESTAMPTZ NOT NULL,
    updated_at TIMESTAMPTZ NOT NULL
);

CREATE INDEX idx_base_events_user_id ON base_events (user_id);

CREATE TABLE one_time_events
(
    id         UUID PRIMARY KEY,
    event_date DATE                   NOT NULL,
    start_time TIME WITHOUT TIME ZONE NOT NULL,
    end_time   TIME WITHOUT TIME ZONE NOT NULL,

    CONSTRAINT fk_one_time_base
        FOREIGN KEY (id)
            REFERENCES base_events (id)
            ON DELETE CASCADE
);

CREATE INDEX idx_one_time_date ON one_time_events (event_date);

CREATE TABLE recurring_events
(
    id UUID PRIMARY KEY,

    CONSTRAINT fk_recurring_base
        FOREIGN KEY (id)
            REFERENCES base_events (id)
            ON DELETE CASCADE
);

CREATE TABLE recurring_blocks
(
    id                 UUID PRIMARY KEY,
    recurring_event_id UUID                   NOT NULL,
    day_of_week        VARCHAR(20)            NOT NULL,
    start_time         TIME WITHOUT TIME ZONE NOT NULL,
    end_time           TIME WITHOUT TIME ZONE NOT NULL,

    CONSTRAINT fk_recurring_block_event
        FOREIGN KEY (recurring_event_id)
            REFERENCES recurring_events (id)
            ON DELETE CASCADE
);

CREATE INDEX idx_recurring_blocks_day ON recurring_blocks (day_of_week);