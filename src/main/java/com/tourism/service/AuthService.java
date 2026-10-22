package com.tourism.service;

import com.tourism.dao.UserDAO;
import com.tourism.model.User;
import com.tourism.model.UserSession;
import org.mindrot.jbcrypt.BCrypt;

import java.util.regex.Pattern;

/**
 * Service handling authentication, registration, password hashing (BCrypt),
 * and session state management.
 */
public class AuthService {

    private final UserDAO userDAO;
    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");

    public AuthService() {
        this.userDAO = new UserDAO();
    }

    public AuthService(UserDAO userDAO) {
        this.userDAO = userDAO;
    }

    /**
     * Authenticates a user with email and plain-text password.
     *
     * @param email The user email.
     * @param plainPassword The plain text password entered by user.
     * @return AuthResult containing success status, message, and authenticated User.
     */
    public AuthResult login(String email, String plainPassword) {
        if (email == null || email.trim().isEmpty()) {
            return AuthResult.failure("Please enter your email address.");
        }
        if (plainPassword == null || plainPassword.isEmpty()) {
            return AuthResult.failure("Please enter your password.");
        }

        User user = userDAO.findByEmail(email.trim());
        if (user == null) {
            return AuthResult.failure("No account found with this email.");
        }

        try {
            boolean passwordMatches = BCrypt.checkpw(plainPassword, user.getPasswordHash());
            if (!passwordMatches) {
                return AuthResult.failure("Incorrect password. Please try again.");
            }
        } catch (IllegalArgumentException e) {
            return AuthResult.failure("Corrupted password hash. Please contact system admin.");
        }

        // Set active session
        UserSession.getInstance().login(user);
        return AuthResult.success("Login successful!", user);
    }

    /**
     * Registers a new customer account.
     *
     * @param fullName Full name of the user.
     * @param email Email address.
     * @param phone Phone number.
     * @param plainPassword Password.
     * @param confirmPassword Confirmation password.
     * @return AuthResult containing success or validation error details.
     */
    public AuthResult register(String fullName, String email, String phone, String plainPassword, String confirmPassword) {
        // Validation checks
        if (fullName == null || fullName.trim().length() < 2) {
            return AuthResult.failure("Full Name must be at least 2 characters.");
        }

        if (email == null || !EMAIL_PATTERN.matcher(email.trim()).matches()) {
            return AuthResult.failure("Please enter a valid email address (e.g. name@domain.com).");
        }

        if (phone == null || phone.trim().replaceAll("[^0-9]", "").length() < 7) {
            return AuthResult.failure("Please enter a valid phone number (at least 7 digits).");
        }

        if (plainPassword == null || plainPassword.length() < 6) {
            return AuthResult.failure("Password must be at least 6 characters long.");
        }

        if (!plainPassword.equals(confirmPassword)) {
            return AuthResult.failure("Passwords do not match. Please re-enter.");
        }

        // Check if email already registered
        if (userDAO.emailExists(email.trim())) {
            return AuthResult.failure("An account with this email already exists. Please login instead.");
        }

        // Hash password with BCrypt (12 salt rounds for strong security)
        String hashedPassword = BCrypt.hashpw(plainPassword, BCrypt.gensalt(12));

        User newUser = new User(fullName.trim(), email.trim().toLowerCase(), hashedPassword, "CUSTOMER", phone.trim());
        int newUserId = userDAO.registerUser(newUser);

        if (newUserId > 0) {
            newUser.setUserId(newUserId);
            return AuthResult.success("Registration successful! You can now log in.", newUser);
        } else {
            return AuthResult.failure("Registration failed due to a database error. Please try again.");
        }
    }

    /**
     * Logs out the currently active user session.
     */
    public void logout() {
        UserSession.getInstance().logout();
    }

    /**
     * Changes password for a user.
     */
    public AuthResult changePassword(int userId, String oldPassword, String newPassword, String confirmNewPassword) {
        User user = userDAO.findById(userId);
        if (user == null) {
            return AuthResult.failure("User not found.");
        }

        if (!BCrypt.checkpw(oldPassword, user.getPasswordHash())) {
            return AuthResult.failure("Current password does not match.");
        }

        if (newPassword == null || newPassword.length() < 6) {
            return AuthResult.failure("New password must be at least 6 characters long.");
        }

        if (!newPassword.equals(confirmNewPassword)) {
            return AuthResult.failure("New passwords do not match.");
        }

        String newHash = BCrypt.hashpw(newPassword, BCrypt.gensalt(12));
        boolean updated = userDAO.updatePassword(userId, newHash);
        if (updated) {
            return AuthResult.success("Password changed successfully!", user);
        } else {
            return AuthResult.failure("Failed to update password. Please try again.");
        }
    }

    /**
     * Result wrapper for authentication operations.
     */
    public static class AuthResult {
        private final boolean success;
        private final String message;
        private final User user;

        private AuthResult(boolean success, String message, User user) {
            this.success = success;
            this.message = message;
            this.user = user;
        }

        public static AuthResult success(String message, User user) {
            return new AuthResult(true, message, user);
        }

        public static AuthResult failure(String message) {
            return new AuthResult(false, message, null);
        }

        public boolean isSuccess() {
            return success;
        }

        public String getMessage() {
            return message;
        }

        public User getUser() {
            return user;
        }
    }
}
