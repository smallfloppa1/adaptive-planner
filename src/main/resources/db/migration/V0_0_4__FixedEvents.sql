CREATE TABLE fixed_events
(
    id         UUID PRIMARY KEY,
    user_id    UUID        NOT NULL,
    name       VARCHAR     NOT NULL,
    event_type event_type  NOT NULL,
    created_at TIMESTAMPTZ NOT NULL,
    updated_at TIMESTAMPTZ NOT NULL,

    CONSTRAINT fk_fixed_events_user
        FOREIGN KEY (user_id)
            REFERENCES users (id)
            ON DELETE CASCADE
);