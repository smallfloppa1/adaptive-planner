CREATE TABLE users
(
    id         UUID PRIMARY KEY,
    email      VARCHAR(255),
    active     BOOLEAN     NOT NULL,
    created_at TIMESTAMPTZ NOT NULL,
    updated_at TIMESTAMPTZ NOT NULL
);
