package Tugas2.DAO;

import Tugas2.Model.Bookings;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class BookingsDAO {
    private final Connection conn;

    public BookingsDAO(Connection conn) {
        this.conn = conn;
    }

    public List<Bookings> getBookingsByVillaId(int villaId) {
        List<Bookings> bookings = new ArrayList<>();
        String sql = """
            SELECT b.* FROM bookings b
            JOIN room_types rt ON b.room_type = rt.id
            WHERE rt.villa = ?
            ORDER BY b.checkin_date
        """;

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, villaId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    bookings.add(mapResultSetToBooking(rs));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return bookings;
    }

    public List<Bookings> getBookingsByCustomerId(int customerId) {
        List<Bookings> bookings = new ArrayList<>();
        String sql = "SELECT * FROM bookings WHERE customer = ? ORDER BY checkin_date";

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, customerId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    bookings.add(mapResultSetToBooking(rs));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return bookings;
    }

    public boolean insertBooking(Bookings booking) {
        String sql = """
            INSERT INTO bookings (customer, room_type, checkin_date, checkout_date, price, voucher,
                                  final_price, payment_status, has_checkedin, has_checkedout)
            VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
        """;

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, booking.getCustomer());
            stmt.setInt(2, booking.getRoomType());
            stmt.setString(3, booking.getCheckinDate());
            stmt.setString(4, booking.getCheckoutDate());
            stmt.setInt(5, booking.getPrice());
            if (booking.getVoucher() != null) {
                stmt.setInt(6, booking.getVoucher());
            } else {
                stmt.setNull(6, Types.INTEGER);
            }
            stmt.setInt(7, booking.getFinalPrice());
            stmt.setString(8, booking.getPaymentStatus());
            stmt.setInt(9, booking.getHasCheckedIn());
            stmt.setInt(10, booking.getHasCheckedOut());

            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false;
    }

    private Bookings mapResultSetToBooking(ResultSet rs) throws SQLException {
        Bookings booking = new Bookings();
        booking.setId(rs.getInt("id"));
        booking.setCustomer(rs.getInt("customer"));
        booking.setRoomType(rs.getInt("room_type"));
        booking.setCheckinDate(rs.getString("checkin_date"));
        booking.setCheckoutDate(rs.getString("checkout_date"));
        booking.setPrice(rs.getInt("price"));
        int voucher = rs.getInt("voucher");
        booking.setVoucher(rs.wasNull() ? null : voucher);
        booking.setFinalPrice(rs.getInt("final_price"));
        booking.setPaymentStatus(rs.getString("payment_status"));
        booking.setHasCheckedIn(rs.getInt("has_checkedin"));
        booking.setHasCheckedOut(rs.getInt("has_checkedout"));
        return booking;
    }
}
