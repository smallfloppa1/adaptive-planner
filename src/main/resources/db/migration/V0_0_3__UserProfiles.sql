CREATE TABLE user_profiles
(
    user_id                           UUID PRIMARY KEY,
    wake_time                         TIME             NOT NULL,
    sleep_time                        TIME             NOT NULL,
    min_sleep_hours                   DOUBLE PRECISION NOT NULL,
    focus_minutes                     INTEGER          NOT NULL,
    break_minutes                     INTEGER          NOT NULL,
    max_heavy_blocks_per_day          INTEGER          NOT NULL,
    max_total_planned_minutes_per_day INTEGER          NOT NULL,
    weekly_study_target_minutes       INTEGER          NOT NULL,
    strict_enforcement                BOOLEAN          NOT NULL,
    created_at                        TIMESTAMPTZ      NOT NULL,
    updated_at                        TIMESTAMPTZ      NOT NULL,

    CONSTRAINT fk_user_profiles_user
        FOREIGN KEY (user_id)
            REFERENCES users (id)
            ON DELETE CASCADE
);
