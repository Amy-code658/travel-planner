package com.tourism.dao;

import com.tourism.config.DBConnection;
import com.tourism.model.User;

import java.sql.*;

/**
 * Data Access Object for User entity.
 * Handles database operations for customer and admin accounts.
 */
public class UserDAO {

    /**
     * Finds a user by their email address.
     *
     * @param email The user's email.
     * @return User object if found, or null otherwise.
     */
    public User findByEmail(String email) {
        String sql = "SELECT user_id, full_name, email, password_hash, role, phone, created_at " +
                     "FROM users WHERE LOWER(email) = LOWER(?)";

        try {
            Connection conn = DBConnection.getInstance().getConnection();
            if (conn == null) {
                System.err.println("❌ [UserDAO] Database connection is null. Please ensure MySQL is running.");
                return null;
            }
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setString(1, email.trim());

                try (ResultSet rs = stmt.executeQuery()) {
                    if (rs.next()) {
                        return mapRowToUser(rs);
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("❌ [UserDAO] Error finding user by email: " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Finds a user by their user ID.
     *
     * @param userId The unique user ID.
     * @return User object if found, or null otherwise.
     */
    public User findById(int userId) {
        String sql = "SELECT user_id, full_name, email, password_hash, role, phone, created_at " +
                     "FROM users WHERE user_id = ?";

        try {
            Connection conn = DBConnection.getInstance().getConnection();
            if (conn == null) {
                System.err.println("❌ [UserDAO] Database connection is null. Please ensure MySQL is running.");
                return null;
            }
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setInt(1, userId);

                try (ResultSet rs = stmt.executeQuery()) {
                    if (rs.next()) {
                        return mapRowToUser(rs);
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("❌ [UserDAO] Error finding user by ID: " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Checks if an email is already registered in the database.
     *
     * @param email The email to check.
     * @return true if email exists, false otherwise.
     */
    public boolean emailExists(String email) {
        String sql = "SELECT 1 FROM users WHERE LOWER(email) = LOWER(?)";

        try {
            Connection conn = DBConnection.getInstance().getConnection();
            if (conn == null) {
                System.err.println("❌ [UserDAO] Database connection is null. Please ensure MySQL is running.");
                return false;
            }
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setString(1, email.trim());
                try (ResultSet rs = stmt.executeQuery()) {
                    return rs.next();
                }
            }
        } catch (SQLException e) {
            System.err.println("❌ [UserDAO] Error checking email existence: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Inserts a new customer user into the database.
     *
     * @param user The user entity to register.
     * @return The generated user ID, or -1 if registration failed.
     */
    public int registerUser(User user) {
        String sql = "INSERT INTO users (full_name, email, password_hash, role, phone) " +
                     "VALUES (?, ?, ?, ?, ?)";

        try {
            Connection conn = DBConnection.getInstance().getConnection();
            if (conn == null) {
                System.err.println("❌ [UserDAO] Database connection is null. Please ensure MySQL is running.");
                return -1;
            }
            try (PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
                stmt.setString(1, user.getFullName());
                stmt.setString(2, user.getEmail().trim().toLowerCase());
                stmt.setString(3, user.getPasswordHash());
                stmt.setString(4, user.getRole() != null ? user.getRole() : "CUSTOMER");
                stmt.setString(5, user.getPhone());

                int affectedRows = stmt.executeUpdate();
                if (affectedRows > 0) {
                    try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                        if (generatedKeys.next()) {
                            int generatedId = generatedKeys.getInt(1);
                            user.setUserId(generatedId);
                            return generatedId;
                        }
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("❌ [UserDAO] Error registering user: " + e.getMessage());
            e.printStackTrace();
        }
        return -1;
    }

    /**
     * Updates an existing user's profile information.
     *
     * @param user The user object containing updated information.
     * @return true if updated successfully, false otherwise.
     */
    public boolean updateProfile(User user) {
        String sql = "UPDATE users SET full_name = ?, phone = ? WHERE user_id = ?";

        try {
            Connection conn = DBConnection.getInstance().getConnection();
            if (conn == null) {
                System.err.println("❌ [UserDAO] Database connection is null. Please ensure MySQL is running.");
                return false;
            }
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setString(1, user.getFullName());
                stmt.setString(2, user.getPhone());
                stmt.setInt(3, user.getUserId());

                return stmt.executeUpdate() > 0;
            }
        } catch (SQLException e) {
            System.err.println("❌ [UserDAO] Error updating profile: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Updates a user's password hash.
     *
     * @param userId The ID of the user.
     * @param newPasswordHash The new BCrypt password hash.
     * @return true if updated successfully, false otherwise.
     */
    public boolean updatePassword(int userId, String newPasswordHash) {
        String sql = "UPDATE users SET password_hash = ? WHERE user_id = ?";

        try {
            Connection conn = DBConnection.getInstance().getConnection();
            if (conn == null) {
                System.err.println("❌ [UserDAO] Database connection is null. Please ensure MySQL is running.");
                return false;
            }
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setString(1, newPasswordHash);
                stmt.setInt(2, userId);

                return stmt.executeUpdate() > 0;
            }
        } catch (SQLException e) {
            System.err.println("❌ [UserDAO] Error updating password: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Helper method to map a ResultSet row to a User object.
     */
    private User mapRowToUser(ResultSet rs) throws SQLException {
        User user = new User();
        user.setUserId(rs.getInt("user_id"));
        user.setFullName(rs.getString("full_name"));
        user.setEmail(rs.getString("email"));
        user.setPasswordHash(rs.getString("password_hash"));
        user.setRole(rs.getString("role"));
        user.setPhone(rs.getString("phone"));

        Timestamp ts = rs.getTimestamp("created_at");
        if (ts != null) {
            user.setCreatedAt(ts.toLocalDateTime());
        }
        return user;
    }
}
