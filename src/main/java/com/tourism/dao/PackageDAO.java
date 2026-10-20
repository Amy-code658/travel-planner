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
import java.util.Optional;

/**
 * Data Access Object for Package entities.
 * Handles database operations for the travel_packages table with destination joins using PreparedStatement.
 * Fully unifies catalog search operations (Dev 3) and recommendation contract operations (Dev 4).
 */
public class PackageDAO {

    private static final String BASE_SELECT_SQL =
            "SELECT " +
            "    p.package_id, p.destination_id, p.title, p.description AS pkg_description, " +
            "    p.duration_days, p.base_price, p.season, p.discount_percentage, p.is_active, " +
            "    d.name AS dest_name, d.country AS dest_country, d.description AS dest_description, " +
            "    d.category AS dest_category, d.estimated_daily_cost AS dest_daily_cost, d.image_url AS dest_image_url " +
            "FROM travel_packages p " +
            "LEFT JOIN destinations d ON p.destination_id = d.destination_id ";

    /**
     * Retrieves all active travel packages joined with their destination details.
     *
     * @return List of active Package entities.
     */
    public List<Package> findAllActive() {
        List<Package> list = new ArrayList<>();
        String sql = BASE_SELECT_SQL + "WHERE p.is_active = TRUE ORDER BY p.package_id ASC";

        Connection conn = DBConnection.getInstance().getConnection();
        if (conn == null) {
            System.err.println("❌ Database connection is unavailable in PackageDAO.findAllActive()");
            return list;
        }

        try (PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                list.add(mapResultSetToPackage(rs));
            }
        } catch (SQLException e) {
            System.err.println("❌ Error retrieving active packages: " + e.getMessage());
            e.printStackTrace();
        }

        return list;
    }

    /**
     * Backward-compatible alias for findAllActive() (Dev 4 contract).
     *
     * @return List of active Package objects with populated Destination references
     */
    public List<Package> getAllActivePackages() {
        return findAllActive();
    }

    /**
     * Retrieves all travel packages (both active and inactive), joined with destination details.
     *
     * @return List of all Package entities.
     */
    public List<Package> findAll() {
        List<Package> list = new ArrayList<>();
        String sql = BASE_SELECT_SQL + "ORDER BY p.package_id ASC";

        Connection conn = DBConnection.getInstance().getConnection();
        if (conn == null) {
            System.err.println("❌ Database connection is unavailable in PackageDAO.findAll()");
            return list;
        }

        try (PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                list.add(mapResultSetToPackage(rs));
            }
        } catch (SQLException e) {
            System.err.println("❌ Error retrieving all packages: " + e.getMessage());
            e.printStackTrace();
        }

        return list;
    }

    /**
     * Finds a single package by its primary key ID, joined with destination details.
     *
     * @param packageId The primary key ID.
     * @return Optional containing the Package if found, or empty Optional.
     */
    public Optional<Package> findById(int packageId) {
        String sql = BASE_SELECT_SQL + "WHERE p.package_id = ?";

        Connection conn = DBConnection.getInstance().getConnection();
        if (conn == null) {
            System.err.println("❌ Database connection is unavailable in PackageDAO.findById()");
            return Optional.empty();
        }

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, packageId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToPackage(rs));
                }
            }
        } catch (SQLException e) {
            System.err.println("❌ Error finding package by ID " + packageId + ": " + e.getMessage());
            e.printStackTrace();
        }

        return Optional.empty();
    }

    /**
     * Retrieves a single package by its ID, with destination details attached.
     * Backward-compatible method matching Dev 4 contract.
     *
     * @param packageId the ID of the package
     * @return Package object or null if not found
     */
    public Package getPackageById(int packageId) {
        return findById(packageId).orElse(null);
    }

    /**
     * Finds all active packages for a given destination ID.
     *
     * @param destinationId The foreign key destination ID.
     * @return List of matching active packages.
     */
    public List<Package> findByDestinationId(int destinationId) {
        List<Package> list = new ArrayList<>();
        String sql = BASE_SELECT_SQL + "WHERE p.destination_id = ? AND p.is_active = TRUE ORDER BY p.base_price ASC";

        Connection conn = DBConnection.getInstance().getConnection();
        if (conn == null) {
            System.err.println("❌ Database connection is unavailable in PackageDAO.findByDestinationId()");
            return list;
        }

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, destinationId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    list.add(mapResultSetToPackage(rs));
                }
            }
        } catch (SQLException e) {
            System.err.println("❌ Error finding packages for destination ID " + destinationId + ": " + e.getMessage());
            e.printStackTrace();
        }

        return list;
    }

    /**
     * Dynamically searches and filters active packages using parameterized queries.
     *
     * @param keyword  Search term for title, description, destination name, or country (optional).
     * @param category Category filter e.g. "Adventure", "Beach" (optional, "All" ignored).
     * @param maxPrice Maximum final price filter (optional if null or <= 0).
     * @param season   Season filter e.g. "Summer", "Winter" (optional, "All" ignored).
     * @return List of packages matching all specified criteria.
     */
    public List<Package> searchPackages(String keyword, String category, Double maxPrice, String season) {
        List<Package> list = new ArrayList<>();
        List<Object> params = new ArrayList<>();

        StringBuilder sql = new StringBuilder(BASE_SELECT_SQL);
        sql.append("WHERE p.is_active = TRUE ");

        // Keyword filter: searches package title, description, destination name, country
        if (keyword != null && !keyword.trim().isEmpty()) {
            String pattern = "%" + keyword.trim().toLowerCase() + "%";
            sql.append("AND (LOWER(p.title) LIKE ? ")
               .append("OR LOWER(p.description) LIKE ? ")
               .append("OR LOWER(d.name) LIKE ? ")
               .append("OR LOWER(d.country) LIKE ?) ");
            params.add(pattern);
            params.add(pattern);
            params.add(pattern);
            params.add(pattern);
        }

        // Category filter
        if (category != null && !category.trim().isEmpty() &&
            !"All".equalsIgnoreCase(category.trim()) &&
            !"All Categories".equalsIgnoreCase(category.trim())) {
            sql.append("AND d.category = ? ");
            params.add(category.trim());
        }

        // Max price filter based on effective discounted price
        if (maxPrice != null && maxPrice > 0) {
            sql.append("AND (p.base_price - (p.base_price * (p.discount_percentage / 100.0))) <= ? ");
            params.add(maxPrice);
        }

        // Season filter
        if (season != null && !season.trim().isEmpty() &&
            !"All".equalsIgnoreCase(season.trim()) &&
            !"All Seasons".equalsIgnoreCase(season.trim())) {
            sql.append("AND p.season = ? ");
            params.add(season.trim());
        }

        sql.append("ORDER BY p.package_id ASC");

        Connection conn = DBConnection.getInstance().getConnection();
        if (conn == null) {
            System.err.println("❌ Database connection is unavailable in PackageDAO.searchPackages()");
            return list;
        }

        try (PreparedStatement stmt = conn.prepareStatement(sql.toString())) {
            for (int i = 0; i < params.size(); i++) {
                Object param = params.get(i);
                if (param instanceof String s) {
                    stmt.setString(i + 1, s);
                } else if (param instanceof Double d) {
                    stmt.setDouble(i + 1, d);
                } else if (param instanceof Integer n) {
                    stmt.setInt(i + 1, n);
                } else {
                    stmt.setObject(i + 1, param);
                }
            }

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    list.add(mapResultSetToPackage(rs));
                }
            }
        } catch (SQLException e) {
            System.err.println("❌ Error searching packages: " + e.getMessage());
            e.printStackTrace();
        }

        return list;
    }

    /**
     * Retrieves all distinct seasons available among active travel packages.
     *
     * @return List of season names.
     */
    public List<String> findAllSeasons() {
        List<String> seasons = new ArrayList<>();
        String sql = "SELECT DISTINCT season FROM travel_packages WHERE is_active = TRUE AND season IS NOT NULL ORDER BY season ASC";

        Connection conn = DBConnection.getInstance().getConnection();
        if (conn == null) {
            return seasons;
        }

        try (PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                String s = rs.getString("season");
                if (s != null && !s.trim().isEmpty()) {
                    seasons.add(s);
                }
            }
        } catch (SQLException e) {
            System.err.println("❌ Error retrieving package seasons: " + e.getMessage());
            e.printStackTrace();
        }

        return seasons;
    }

    /**
     * Maps a ResultSet row to a Package object, including its joined Destination object.
     *
     * @param rs Active ResultSet positioned at current row.
     * @return Populated Package instance.
     * @throws SQLException If column reading fails.
     */
    private Package mapResultSetToPackage(ResultSet rs) throws SQLException {
        Package pkg = new Package();
        pkg.setPackageId(rs.getInt("package_id"));
        pkg.setDestinationId(rs.getInt("destination_id"));
        pkg.setTitle(rs.getString("title"));
        pkg.setDescription(rs.getString("pkg_description"));
        pkg.setDurationDays(rs.getInt("duration_days"));
        pkg.setBasePrice(rs.getDouble("base_price"));
        pkg.setSeason(rs.getString("season"));
        pkg.setDiscountPercentage(rs.getDouble("discount_percentage"));
        pkg.setActive(rs.getBoolean("is_active"));

        // Map joined destination if present
        int destId = rs.getInt("destination_id");
        if (destId > 0 && rs.getString("dest_name") != null) {
            Destination dest = new Destination();
            dest.setDestinationId(destId);
            dest.setName(rs.getString("dest_name"));
            dest.setCountry(rs.getString("dest_country"));
            dest.setDescription(rs.getString("dest_description"));
            dest.setCategory(rs.getString("dest_category"));
            dest.setEstimatedDailyCost(rs.getDouble("dest_daily_cost"));
            dest.setImageUrl(rs.getString("dest_image_url"));
            pkg.setDestination(dest);
        }

        return pkg;
    }
}
