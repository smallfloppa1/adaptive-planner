package com.floppahost.adaptiveplanner.planner.domain.user;

import java.util.Objects;
import java.util.regex.Pattern;


public record Email(
        String value
) {
    private static final Pattern EMAIL_PATTERN = Pattern.compile(
            "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$"
    );

    public Email {
        Objects.requireNonNull(value, "Email value cannot be null");

        if (value.isBlank()) {
            throw new IllegalArgumentException("Email value cannot be blank");
        }

        String trimmed = value.trim();
        if (!EMAIL_PATTERN.matcher(trimmed).matches()) {
            throw new IllegalArgumentException("Invalid email format");
        }

        value = trimmed.toLowerCase();
    }
}
