CREATE TABLE users
(
    user_id uuid PRIMARY KEY,

    CONSTRAINT fk_user_profiles_domain_user
        FOREIGN KEY (user_id)
            REFERENCES users (id)
            ON DELETE CASCADE
);
