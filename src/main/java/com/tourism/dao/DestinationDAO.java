package com.tourism.dao;

import com.tourism.config.DBConnection;
import com.tourism.model.Destination;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Data Access Object for Destination entities.
 * Handles database operations for the destinations table using PreparedStatement.
 */
public class DestinationDAO {

    /**
     * Retrieves all destinations ordered by name.
     *
     * @return List of Destination objects.
     */
    public List<Destination> findAll() {
        List<Destination> list = new ArrayList<>();
        String sql = "SELECT destination_id, name, country, description, category, estimated_daily_cost, image_url " +
                     "FROM destinations ORDER BY name ASC";

        Connection conn = DBConnection.getInstance().getConnection();
        if (conn == null) {
            System.err.println("❌ Database connection is unavailable in DestinationDAO.findAll()");
            return list;
        }

        try (PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                list.add(mapResultSetToDestination(rs));
            }
        } catch (SQLException e) {
            System.err.println("❌ Error retrieving destinations: " + e.getMessage());
            e.printStackTrace();
        }

        return list;
    }

    /**
     * Finds a single destination by its primary key ID.
     *
     * @param destinationId The primary key ID.
     * @return Optional containing the Destination if found, or empty Optional.
     */
    public Optional<Destination> findById(int destinationId) {
        String sql = "SELECT destination_id, name, country, description, category, estimated_daily_cost, image_url " +
                     "FROM destinations WHERE destination_id = ?";

        Connection conn = DBConnection.getInstance().getConnection();
        if (conn == null) {
            System.err.println("❌ Database connection is unavailable in DestinationDAO.findById()");
            return Optional.empty();
        }

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, destinationId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToDestination(rs));
                }
            }
        } catch (SQLException e) {
            System.err.println("❌ Error finding destination by ID " + destinationId + ": " + e.getMessage());
            e.printStackTrace();
        }

        return Optional.empty();
    }

    /**
     * Finds destinations by category (e.g., Adventure, Beach, Culture, Nature, Luxury).
     *
     * @param category The category name.
     * @return List of matching Destination objects.
     */
    public List<Destination> findByCategory(String category) {
        List<Destination> list = new ArrayList<>();
        if (category == null || category.trim().isEmpty() || "All".equalsIgnoreCase(category.trim())) {
            return findAll();
        }

        String sql = "SELECT destination_id, name, country, description, category, estimated_daily_cost, image_url " +
                     "FROM destinations WHERE category = ? ORDER BY name ASC";

        Connection conn = DBConnection.getInstance().getConnection();
        if (conn == null) {
            System.err.println("❌ Database connection is unavailable in DestinationDAO.findByCategory()");
            return list;
        }

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, category.trim());
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    list.add(mapResultSetToDestination(rs));
                }
            }
        } catch (SQLException e) {
            System.err.println("❌ Error finding destinations by category '" + category + "': " + e.getMessage());
            e.printStackTrace();
        }

        return list;
    }

    /**
     * Searches destinations matching the given keyword in name, country, or description.
     *
     * @param keyword The search term.
     * @return List of matching destinations.
     */
    public List<Destination> search(String keyword) {
        List<Destination> list = new ArrayList<>();
        if (keyword == null || keyword.trim().isEmpty()) {
            return findAll();
        }

        String sql = "SELECT destination_id, name, country, description, category, estimated_daily_cost, image_url " +
                     "FROM destinations " +
                     "WHERE LOWER(name) LIKE ? OR LOWER(country) LIKE ? OR LOWER(description) LIKE ? " +
                     "ORDER BY name ASC";

        Connection conn = DBConnection.getInstance().getConnection();
        if (conn == null) {
            System.err.println("❌ Database connection is unavailable in DestinationDAO.search()");
            return list;
        }

        String searchPattern = "%" + keyword.trim().toLowerCase() + "%";

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, searchPattern);
            stmt.setString(2, searchPattern);
            stmt.setString(3, searchPattern);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    list.add(mapResultSetToDestination(rs));
                }
            }
        } catch (SQLException e) {
            System.err.println("❌ Error searching destinations with keyword '" + keyword + "': " + e.getMessage());
            e.printStackTrace();
        }

        return list;
    }

    /**
     * Retrieves all distinct destination categories present in the database.
     *
     * @return List of category names.
     */
    public List<String> findAllCategories() {
        List<String> categories = new ArrayList<>();
        String sql = "SELECT DISTINCT category FROM destinations ORDER BY category ASC";

        Connection conn = DBConnection.getInstance().getConnection();
        if (conn == null) {
            return categories;
        }

        try (PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                String cat = rs.getString("category");
                if (cat != null && !cat.trim().isEmpty()) {
                    categories.add(cat);
                }
            }
        } catch (SQLException e) {
            System.err.println("❌ Error retrieving destination categories: " + e.getMessage());
            e.printStackTrace();
        }

        return categories;
    }

    /**
     * Helper method to map a single ResultSet row to a Destination model.
     *
     * @param rs The active ResultSet positioned at the desired row.
     * @return Instantiated Destination object.
     * @throws SQLException If column extraction fails.
     */
    private Destination mapResultSetToDestination(ResultSet rs) throws SQLException {
        Destination destination = new Destination();
        destination.setDestinationId(rs.getInt("destination_id"));
        destination.setName(rs.getString("name"));
        destination.setCountry(rs.getString("country"));
        destination.setDescription(rs.getString("description"));
        destination.setCategory(rs.getString("category"));
        destination.setEstimatedDailyCost(rs.getDouble("estimated_daily_cost"));
        destination.setImageUrl(rs.getString("image_url"));
        return destination;
    }
}
