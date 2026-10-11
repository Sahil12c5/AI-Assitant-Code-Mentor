package com.aimentor.util;

import java.util.regex.Pattern;

/**
 * Input validation utilities.
 */
public class ValidationUtil {
    private static final Pattern EMAIL_PATTERN =
            Pattern.compile("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");
    private static final int MAX_NAME_LENGTH = 100;
    private static final int MAX_EMAIL_LENGTH = 255;
    private static final int MIN_PASSWORD_LENGTH = 6;
    private static final int MAX_PASSWORD_LENGTH = 100;
    private static final int MAX_CODE_LENGTH = 50_000; // 50KB limit

    private ValidationUtil() {}

    /**
     * Validate email format and length.
     */
    public static String validateEmail(String email) {
        if (email == null || email.isBlank()) {
            return "Email is required";
        }
        email = email.trim().toLowerCase();
        if (email.length() > MAX_EMAIL_LENGTH) {
            return "Email is too long (max " + MAX_EMAIL_LENGTH + " characters)";
        }
        if (!EMAIL_PATTERN.matcher(email).matches()) {
            return "Invalid email format";
        }
        return null; // valid
    }

    /**
     * Validate name.
     */
    public static String validateName(String name) {
        if (name == null || name.isBlank()) {
            return "Name is required";
        }
        if (name.trim().length() > MAX_NAME_LENGTH) {
            return "Name is too long (max " + MAX_NAME_LENGTH + " characters)";
        }
        if (name.trim().length() < 2) {
            return "Name must be at least 2 characters";
        }
        return null;
    }

    /**
     * Validate password strength.
     */
    public static String validatePassword(String password) {
        if (password == null || password.isBlank()) {
            return "Password is required";
        }
        if (password.length() < MIN_PASSWORD_LENGTH) {
            return "Password must be at least " + MIN_PASSWORD_LENGTH + " characters";
        }
        if (password.length() > MAX_PASSWORD_LENGTH) {
            return "Password is too long (max " + MAX_PASSWORD_LENGTH + " characters)";
        }
        return null;
    }

    /**
     * Validate code submission size.
     */
    public static String validateCode(String code) {
        if (code == null || code.isBlank()) {
            return "Code cannot be empty";
        }
        if (code.length() > MAX_CODE_LENGTH) {
            return "Code exceeds maximum length (" + MAX_CODE_LENGTH + " characters)";
        }
        return null;
    }

    /**
     * Check if a string is null or blank.
     */
    public static boolean isBlank(String s) {
        return s == null || s.isBlank();
    }

    /**
     * Safely parse a long, returning null on failure.
     */
    public static Long parseLong(String s) {
        if (s == null || s.isBlank()) return null;
        try {
            return Long.parseLong(s.trim());
        } catch (NumberFormatException e) {
            return null;
        }
    }

    /**
     * Safely parse an int with a default value.
     */
    public static int parseInt(String s, int defaultValue) {
        if (s == null || s.isBlank()) return defaultValue;
        try {
            return Integer.parseInt(s.trim());
        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }
}
