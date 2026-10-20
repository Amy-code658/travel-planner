package com.tourism.service;

import com.tourism.config.DBConnection;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Supplies aggregated data for the admin charts.
 * The shared connection is never closed here because DBConnection is a singleton.
 */
public class AnalyticsService {

    /**
     * Destinations ranked by number of bookings, cancelled bookings excluded.
     * Insertion order is preserved so the highest count comes first.
     */
    public Map<String, Integer> getTopDestinations(int limit) throws SQLException {
        String sql = "SELECT d.name, COUNT(b.booking_id) AS total_bookings "
                + "FROM bookings b "
                + "JOIN travel_packages p ON b.package_id = p.package_id "
                + "JOIN destinations d ON p.destination_id = d.destination_id "
                + "WHERE b.booking_status <> 'CANCELLED' "
                + "GROUP BY d.destination_id, d.name "
                + "ORDER BY total_bookings DESC "
                + "LIMIT ?";

        Map<String, Integer> result = new LinkedHashMap<>();
        Connection connection = DBConnection.getInstance().getConnection();
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, limit);
            try (ResultSet rows = statement.executeQuery()) {
                while (rows.next()) {
                    result.put(rows.getString("name"), rows.getInt("total_bookings"));
                }
            }
        }
        return result;
    }

    /**
     * Revenue per month (yyyy-MM). Revenue is the booking total minus any refund,
     * so cancelled bookings only contribute the amount the company kept.
     */
    public Map<String, BigDecimal> getMonthlyRevenue() throws SQLException {
        String sql = "SELECT DATE_FORMAT(booking_date, '%Y-%m') AS booking_month, "
                + "SUM(total_cost - refund_amount) AS revenue "
                + "FROM bookings "
                + "GROUP BY booking_month "
                + "ORDER BY booking_month";

        Map<String, BigDecimal> result = new LinkedHashMap<>();
        Connection connection = DBConnection.getInstance().getConnection();
        try (PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet rows = statement.executeQuery()) {
            while (rows.next()) {
                result.put(rows.getString("booking_month"), rows.getBigDecimal("revenue"));
            }
        }
        return result;
    }
}
