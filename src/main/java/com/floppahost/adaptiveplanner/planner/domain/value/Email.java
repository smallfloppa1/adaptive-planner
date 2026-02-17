package com.floppahost.adaptiveplanner.planner.domain.value;

import java.util.regex.Pattern;

/**
 * Email value object with validation.
 * Ensures email addresses are well-formed and non-empty.
 */
public record Email(String value) {
    private static final Pattern EMAIL_PATTERN = Pattern.compile(
            "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$"
    );

    public Email(String value) {
        if (value == null || value.trim().isEmpty()) {
            throw new IllegalArgumentException("Email: cannot be null or blank");
        }

        String trimmed = value.trim();
        if (!EMAIL_PATTERN.matcher(trimmed).matches()) {
            throw new IllegalArgumentException("Email: invalid format");
        }

        this.value = trimmed.toLowerCase(); // Normalize to lowercase
    }

    /**
     * Factory method for creating Email from string.
     */
    public static Email of(String value) {
        return new Email(value);
    }

    /**
     * Get the domain part of the email (after @).
     */
    public String getDomain() {
        return value.substring(value.indexOf('@') + 1);
    }

    /**
     * Get the local part of the email (before @).
     */
    public String getLocalPart() {
        return value.substring(0, value.indexOf('@'));
    }

}
