CREATE TABLE users
(
    id         uuid PRIMARY KEY,
    email      VARCHAR(255),
    is_active  BOOLEAN     NOT NULL,
    created_at timestamptz NOT NULL,
    updated_at timestamptz NOT NULL
);
