package com.floppahost.adaptiveplanner.planner.domain.user;

import java.util.regex.Pattern;


public record Email(
        String value
) {
    private static final Pattern EMAIL_PATTERN = Pattern.compile(
            "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$"
    );

    public Email {
        if (value == null || value.trim().isEmpty()) {
            throw new IllegalArgumentException("Email: cannot be null or blank");
        }

        String trimmed = value.trim();
        if (!EMAIL_PATTERN.matcher(trimmed).matches()) {
            throw new IllegalArgumentException("Email: invalid format");
        }

        value = trimmed.toLowerCase();
    }

    public static Email of(String value) {
        return new Email(value);
    }
}
