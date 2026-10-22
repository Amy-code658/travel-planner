package com.tourism.config;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

//Singleton database connection manager for MySQL JDBC.
// Provides a single shared connection instance across all DAO modules.

public class DBConnection {

    private static final String URL = "jdbc:mysql://localhost:3306/tourism_db?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC";
    private static final String USER = "tourism_user";
    private static final String PASSWORD = "tourism123";

    private static DBConnection instance;
    private Connection connection;

    static {
        // Register shutdown hook to cleanly stop MySQL cleanup thread and avoid Maven exec linger warnings
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            try {
                com.mysql.cj.jdbc.AbandonedConnectionCleanupThread.checkedShutdown();
            } catch (Exception ignored) {}
        }));
    }

    // Private constructor prevents external instantiation
    private DBConnection() {
        try {
            // Load MySQL JDBC Driver
            Class.forName("com.mysql.cj.jdbc.Driver");
            this.connection = DriverManager.getConnection(URL, USER, PASSWORD);
        } catch (ClassNotFoundException e) {
            System.err.println("❌ MySQL JDBC Driver not found. Ensure mysql-connector-j is in pom.xml.");
            e.printStackTrace();
        } catch (SQLException e) {
            System.err.println("❌ Failed to connect to MySQL database.");
            e.printStackTrace();
        }
    }

    //Retrieves the Singleton instance of DBConnection.
     
    public static synchronized DBConnection getInstance() {
        if (instance == null) {
            instance = new DBConnection();
        }
        return instance;
    }

    //Gets the active database connection. Re-establishes connection if closed.
     
    public Connection getConnection() {
        try {
            if (connection == null || connection.isClosed()) {
                connection = DriverManager.getConnection(URL, USER, PASSWORD);
            }
        } catch (SQLException e) {
            System.err.println("❌ Error re-establishing database connection.");
            e.printStackTrace();
        }
        return connection;
    }

    //Closes the active database connection when application shuts down.
    public void closeConnection() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
                System.out.println("ℹ️ Database connection closed.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Test method: Run this class directly to verify database connectivity.
    public static void main(String[] args) {
        System.out.println("⏳ Testing database connection...");
        try {
            DBConnection db = DBConnection.getInstance();
            Connection conn = db.getConnection();
            if (conn != null && !conn.isClosed()) {
                System.out.println("✅ [SUCCESS] Connected to tourism_db successfully as 'tourism_user'!");
            } else {
                System.err.println("❌ [FAILURE] Connection object is null or closed.");
            }
        } catch (Exception e) {
            System.err.println("❌ [EXCEPTION] Could not connect: " + e.getMessage());
            e.printStackTrace();
        }
    }
}