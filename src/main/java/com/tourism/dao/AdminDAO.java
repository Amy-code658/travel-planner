package com.tourism.dao;

import com.tourism.config.DBConnection;
import com.tourism.model.CustomerBookingDossier;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object for Administrative insights and customer roster audits.
 * Joins customers, their selected packages, travel dates, and payment fulfillment states.
 */
public class AdminDAO {

    /**
     * Retrieves all registered customers and their associated package choices.
     * Includes customers who signed in/registered even if they haven't booked a package yet.
     */
    public List<CustomerBookingDossier> getAllCustomerBookings() {
        String sql = "SELECT " +
                     "    u.user_id, " +
                     "    u.full_name, " +
                     "    u.email, " +
                     "    u.phone, " +
                     "    u.created_at AS user_created_at, " +
                     "    b.booking_id, " +
                     "    b.package_id, " +
                     "    p.title AS package_title, " +
                     "    d.name AS destination_name, " +
                     "    d.country AS destination_country, " +
                     "    d.category AS destination_category, " +
                     "    p.duration_days, " +
                     "    b.travel_date, " +
                     "    b.number_of_guests, " +
                     "    b.booking_status, " +
                     "    b.payment_status, " +
                     "    b.booking_date, " +
                     "    d.image_url " +
                     "FROM users u " +
                     "LEFT JOIN bookings b ON u.user_id = b.user_id " +
                     "LEFT JOIN travel_packages p ON b.package_id = p.package_id " +
                     "LEFT JOIN destinations d ON p.destination_id = d.destination_id " +
                     "WHERE u.role = 'CUSTOMER' " +
                     "ORDER BY b.booking_date DESC, u.created_at DESC";

        List<CustomerBookingDossier> list = new ArrayList<>();
        try {
            Connection conn = DBConnection.getInstance().getConnection();
            try (PreparedStatement stmt = conn.prepareStatement(sql);
                 ResultSet rs = stmt.executeQuery()) {

                while (rs.next()) {
                    CustomerBookingDossier d = new CustomerBookingDossier();
                    d.setUserId(rs.getInt("user_id"));
                    d.setFullName(rs.getString("full_name"));
                    d.setEmail(rs.getString("email"));
                    d.setPhone(rs.getString("phone"));

                    Timestamp userCreatedTs = rs.getTimestamp("user_created_at");
                    if (userCreatedTs != null) {
                        d.setUserCreatedAt(userCreatedTs.toLocalDateTime());
                    }

                    int bId = rs.getInt("booking_id");
                    if (!rs.wasNull()) {
                        d.setBookingId(bId);
                        d.setPackageId(rs.getInt("package_id"));
                        d.setPackageTitle(rs.getString("package_title"));
                        d.setDestinationName(rs.getString("destination_name"));
                        d.setDestinationCountry(rs.getString("destination_country"));
                        d.setDestinationCategory(rs.getString("destination_category"));
                        d.setDurationDays(rs.getInt("duration_days"));

                        Date tDate = rs.getDate("travel_date");
                        if (tDate != null) {
                            d.setTravelDate(tDate.toLocalDate());
                        }

                        d.setNumberOfGuests(rs.getInt("number_of_guests"));
                        d.setBookingStatus(rs.getString("booking_status"));
                        d.setPaymentStatus(rs.getString("payment_status"));

                        Timestamp bDate = rs.getTimestamp("booking_date");
                        if (bDate != null) {
                            d.setBookingDate(bDate.toLocalDateTime());
                        }

                        d.setDestinationImageUrl(rs.getString("image_url"));
                    }

                    list.add(d);
                }
            }
        } catch (SQLException e) {
            System.err.println("❌ [AdminDAO] Error fetching customer bookings roster: " + e.getMessage());
            e.printStackTrace();
        }
        return list;
    }

    /**
     * Updates the payment fulfillment status of a booking (e.g. PAID, PENDING, PARTIALLY PAID, REFUNDED).
     */
    public boolean updatePaymentStatus(int bookingId, String newPaymentStatus) {
        String sql = "UPDATE bookings SET payment_status = ? WHERE booking_id = ?";
        try {
            Connection conn = DBConnection.getInstance().getConnection();
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setString(1, newPaymentStatus);
                stmt.setInt(2, bookingId);
                return stmt.executeUpdate() > 0;
            }
        } catch (SQLException e) {
            System.err.println("❌ [AdminDAO] Error updating payment status: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Updates the booking trip status (e.g. CONFIRMED, COMPLETED, CANCELLED).
     */
    public boolean updateBookingStatus(int bookingId, String newStatus) {
        String sql = "UPDATE bookings SET booking_status = ? WHERE booking_id = ?";
        try {
            Connection conn = DBConnection.getInstance().getConnection();
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setString(1, newStatus);
                stmt.setInt(2, bookingId);
                return stmt.executeUpdate() > 0;
            }
        } catch (SQLException e) {
            System.err.println("❌ [AdminDAO] Error updating booking status: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }
}
