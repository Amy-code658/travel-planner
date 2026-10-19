package com.tourism.dao;

import com.tourism.config.DBConnection;
import com.tourism.model.Destination;
import com.tourism.model.Package;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object for Travel Packages.
 * Provides query methods for active packages joined with destination details.
 */
public class PackageDAO {

    /**
     * Retrieves all active packages joined with their corresponding destination information.
     * Matches the contract provided by Dev 3.
     *
     * @return List of active Package objects with populated Destination references
     */
    public List<Package> findAllActive() {
        return getAllActivePackages();
    }

    /**
     * Retrieves all active packages joined with their corresponding destination information.
     *
     * @return List of active Package objects with populated Destination references
     */
    public List<Package> getAllActivePackages() {
        List<Package> packages = new ArrayList<>();
        String sql = "SELECT p.package_id, p.destination_id, p.title, p.description, " +
                     "p.duration_days, p.base_price, p.season, p.discount_percentage, p.is_active, " +
                     "d.name AS dest_name, d.country AS dest_country, d.description AS dest_desc, " +
                     "d.category AS dest_category, d.estimated_daily_cost, d.image_url " +
                     "FROM travel_packages p " +
                     "JOIN destinations d ON p.destination_id = d.destination_id " +
                     "WHERE p.is_active = TRUE";

        try {
            Connection conn = DBConnection.getInstance().getConnection();
            if (conn == null || conn.isClosed()) {
                return packages;
            }

            try (PreparedStatement ps = conn.prepareStatement(sql);
                 ResultSet rs = ps.executeQuery()) {

                while (rs.next()) {
                    Package pkg = mapRowToPackageWithDestination(rs);
                    packages.add(pkg);
                }
            }
        } catch (SQLException e) {
            System.err.println("Database error fetching active packages: " + e.getMessage());
            e.printStackTrace();
        }

        return packages;
    }

    /**
     * Retrieves a single package by its ID, with destination details attached.
     *
     * @param packageId the ID of the package
     * @return Package object or null if not found
     */
    public Package getPackageById(int packageId) {
        String sql = "SELECT p.package_id, p.destination_id, p.title, p.description, " +
                     "p.duration_days, p.base_price, p.season, p.discount_percentage, p.is_active, " +
                     "d.name AS dest_name, d.country AS dest_country, d.description AS dest_desc, " +
                     "d.category AS dest_category, d.estimated_daily_cost, d.image_url " +
                     "FROM travel_packages p " +
                     "JOIN destinations d ON p.destination_id = d.destination_id " +
                     "WHERE p.package_id = ?";

        try {
            Connection conn = DBConnection.getInstance().getConnection();
            if (conn == null || conn.isClosed()) {
                return null;
            }

            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setInt(1, packageId);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        return mapRowToPackageWithDestination(rs);
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("Database error fetching package by ID: " + packageId);
            e.printStackTrace();
        }

        return null;
    }

    private Package mapRowToPackageWithDestination(ResultSet rs) throws SQLException {
        Package pkg = new Package();
        pkg.setPackageId(rs.getInt("package_id"));
        pkg.setDestinationId(rs.getInt("destination_id"));
        pkg.setTitle(rs.getString("title"));
        pkg.setDescription(rs.getString("description"));
        pkg.setDurationDays(rs.getInt("duration_days"));
        pkg.setBasePrice(rs.getDouble("base_price"));
        pkg.setSeason(rs.getString("season"));
        pkg.setDiscountPercentage(rs.getDouble("discount_percentage"));
        pkg.setActive(rs.getBoolean("is_active"));

        Destination dest = new Destination(
            rs.getInt("destination_id"),
            rs.getString("dest_name"),
            rs.getString("dest_country"),
            rs.getString("dest_desc"),
            rs.getString("dest_category"),
            rs.getDouble("estimated_daily_cost"),
            rs.getString("image_url")
        );
        pkg.setDestination(dest);

        return pkg;
    }
}
