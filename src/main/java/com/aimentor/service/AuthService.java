package com.aimentor.service;

import com.aimentor.dao.UserDAO;
import com.aimentor.model.User;
import com.aimentor.util.PasswordUtil;
import com.aimentor.util.ValidationUtil;

import java.sql.SQLException;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Authentication service handling registration, login, and session management.
 */
public class AuthService {
    private static final Logger LOGGER = Logger.getLogger(AuthService.class.getName());
    private final UserDAO userDAO = new UserDAO();

    /**
     * Register a new user.
     * @return the created User on success
     * @throws AuthException on validation failures or duplicate email
     */
    public User register(String name, String email, String password) throws AuthException, SQLException {
        // Validate inputs
        String nameErr = ValidationUtil.validateName(name);
        if (nameErr != null) throw new AuthException(nameErr);

        String emailErr = ValidationUtil.validateEmail(email);
        if (emailErr != null) throw new AuthException(emailErr);

        String passErr = ValidationUtil.validatePassword(password);
        if (passErr != null) throw new AuthException(passErr);

        email = email.trim().toLowerCase();

        // Check uniqueness
        if (userDAO.emailExists(email)) {
            throw new AuthException("An account with this email already exists");
        }

        // Create user
        String hash = PasswordUtil.hashPassword(password);
        User user = new User(name.trim(), email, hash, User.Role.STUDENT);
        userDAO.create(user);
        LOGGER.info("User registered: " + user.getEmail());
        return user;
    }

    /**
     * Authenticate a user with email and password.
     * @return the authenticated User on success
     * @throws AuthException on invalid credentials
     */
    public User login(String email, String password) throws AuthException, SQLException {
        if (ValidationUtil.isBlank(email) || ValidationUtil.isBlank(password)) {
            throw new AuthException("Email and password are required");
        }

        email = email.trim().toLowerCase();
        Optional<User> userOpt = userDAO.findByEmail(email);

        if (userOpt.isEmpty()) {
            // Use same message for email not found and wrong password (security)
            throw new AuthException("Invalid email or password");
        }

        User user = userOpt.get();
        if (!PasswordUtil.checkPassword(password, user.getPasswordHash())) {
            throw new AuthException("Invalid email or password");
        }

        LOGGER.info("User logged in: " + user.getEmail());
        return user;
    }

    /**
     * Get user by ID (for session validation).
     */
    public Optional<User> getUserById(long id) throws SQLException {
        return userDAO.findById(id);
    }

    /**
     * Custom exception for authentication errors.
     */
    public static class AuthException extends Exception {
        public AuthException(String message) {
            super(message);
        }
    }
}
