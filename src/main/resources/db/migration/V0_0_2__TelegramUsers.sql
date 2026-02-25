CREATE TABLE telegram_users
(
    id               BIGSERIAL PRIMARY KEY,
    telegram_user_id BIGINT      NOT NULL UNIQUE,
    chat_id          BIGINT      NOT NULL,
    user_id          uuid        NOT NULL UNIQUE,
    created_at       timestamptz NOT NULL,
    updated_at       timestamptz NOT NULL,

    CONSTRAINT fk_telegram_user_user FOREIGN KEY (user_id)
        REFERENCES users (id)
        ON DELETE CASCADE
);
