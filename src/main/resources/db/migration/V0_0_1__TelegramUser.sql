CREATE TABLE telegram_user
(
    id               BIGSERIAL PRIMARY KEY,
    telegram_user_id BIGINT      NOT NULL UNIQUE,
    chat_id          BIGINT      NOT NULL,
    created_at       timestamptz NOT NULL,
    updated_at       timestamptz NOT NULL
);
