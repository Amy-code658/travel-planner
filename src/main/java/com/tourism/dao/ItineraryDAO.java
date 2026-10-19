package com.tourism.dao;

import com.tourism.config.DBConnection;
import com.tourism.model.ItineraryDay;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object for itinerary days.
 * Interacts with the itinerary_days table using PreparedStatements.
 */
public class ItineraryDAO {

    /**
     * Retrieves all day-by-day itinerary records for a specific travel package,
     * ordered sequentially by day number.
     *
     * @param packageId the ID of the travel package
     * @return List of ItineraryDay objects
     */
    public List<ItineraryDay> getItineraryByPackageId(int packageId) {
        List<ItineraryDay> itinerary = new ArrayList<>();
        String sql = "SELECT itinerary_id, package_id, day_number, title, activity_description " +
                     "FROM itinerary_days " +
                     "WHERE package_id = ? " +
                     "ORDER BY day_number ASC";

        try {
            Connection conn = DBConnection.getInstance().getConnection();
            if (conn == null || conn.isClosed()) {
                return itinerary;
            }

            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setInt(1, packageId);
                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        ItineraryDay day = new ItineraryDay(
                            rs.getInt("itinerary_id"),
                            rs.getInt("package_id"),
                            rs.getInt("day_number"),
                            rs.getString("title"),
                            rs.getString("activity_description")
                        );
                        itinerary.add(day);
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("Database error fetching itinerary for package ID: " + packageId);
            e.printStackTrace();
        }

        return itinerary;
    }

    /**
     * Inserts a new itinerary day record into the database.
     *
     * @param day the ItineraryDay object to insert
     * @return true if insertion was successful, false otherwise
     */
    public boolean addItineraryDay(ItineraryDay day) {
        String sql = "INSERT INTO itinerary_days (package_id, day_number, title, activity_description) " +
                     "VALUES (?, ?, ?, ?)";

        try {
            Connection conn = DBConnection.getInstance().getConnection();
            if (conn == null || conn.isClosed()) {
                return false;
            }

            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setInt(1, day.getPackageId());
                ps.setInt(2, day.getDayNumber());
                ps.setString(3, day.getTitle());
                ps.setString(4, day.getActivityDescription());
                return ps.executeUpdate() > 0;
            }
        } catch (SQLException e) {
            System.err.println("Database error inserting itinerary day: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
}
