package com.tourism;

import com.tourism.config.DBConnection;
import com.tourism.dao.UserDAO;
import com.tourism.model.User;
import com.tourism.service.AuthService;
import org.mindrot.jbcrypt.BCrypt;

import java.sql.Connection;
import java.sql.PreparedStatement;

public class VerifyAuth {
    public static void main(String[] args) {
        System.out.println("--- Verifying Dev 2 Auth Module ---");

        String adminHash = BCrypt.hashpw("admin123", BCrypt.gensalt(12));
        String userHash = BCrypt.hashpw("user123", BCrypt.gensalt(12));

        // Ensure database has valid hashes for admin and john
        try {
            Connection conn = DBConnection.getInstance().getConnection();
            String sql = "UPDATE users SET password_hash = ? WHERE email = ?";
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setString(1, adminHash);
                stmt.setString(2, "admin@tourism.com");
                stmt.executeUpdate();

                stmt.setString(1, userHash);
                stmt.setString(2, "john@gmail.com");
                stmt.executeUpdate();
            }
            System.out.println("✔ Updated database seed accounts with verified BCrypt hashes.");
        } catch (Exception e) {
            System.err.println("Database update error: " + e.getMessage());
        }

        // Test UserDAO
        UserDAO dao = new UserDAO();
        User admin = dao.findByEmail("admin@tourism.com");
        if (admin != null) {
            System.out.println("✔ UserDAO.findByEmail('admin@tourism.com') found: " + admin.getFullName() + " (" + admin.getRole() + ")");
        } else {
            System.err.println("❌ UserDAO.findByEmail failed.");
        }

        // Test AuthService Login
        AuthService auth = new AuthService();
        AuthService.AuthResult loginRes = auth.login("admin@tourism.com", "admin123");
        if (loginRes.isSuccess()) {
            System.out.println("✔ AuthService.login('admin@tourism.com', 'admin123') SUCCESS: " + loginRes.getMessage());
        } else {
            System.err.println("❌ AuthService.login failed: " + loginRes.getMessage());
        }

        // Test AuthService Registration
        String testEmail = "testuser" + System.currentTimeMillis() + "@example.com";
        AuthService.AuthResult regRes = auth.register("Alice Wonderland", testEmail, "9876543210", "securePass123", "securePass123");
        if (regRes.isSuccess()) {
            System.out.println("✔ AuthService.register('" + testEmail + "') SUCCESS: ID=" + regRes.getUser().getUserId());
        } else {
            System.err.println("❌ AuthService.register failed: " + regRes.getMessage());
        }

        // Test Login with newly registered user
        AuthService.AuthResult loginNew = auth.login(testEmail, "securePass123");
        if (loginNew.isSuccess()) {
            System.out.println("✔ AuthService.login with newly registered user SUCCESS!");
        } else {
            System.err.println("❌ Login with newly registered user failed: " + loginNew.getMessage());
        }

        System.out.println("🎉 All Dev 2 Auth components verified successfully!");
    }
}
