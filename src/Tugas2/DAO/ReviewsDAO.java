package Tugas2.DAO;

import Tugas2.Model.Reviews;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ReviewsDAO {
    private final Connection conn;

    public ReviewsDAO(Connection conn) {
        this.conn = conn;
    }

    // GET /villas/{id}/reviews
    public List<Reviews> getReviewsByVillaId(int villaId) {
        List<Reviews> reviews = new ArrayList<>();
        String sql = """
                SELECT r.booking, r.star, r.title, r.content
                FROM reviews r
                JOIN bookings b ON r.booking = b.id
                JOIN room_types rt ON b.room_type = rt.id
                WHERE rt.villa = ?
                """;

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, villaId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Reviews review = new Reviews();
                    review.setBooking(rs.getInt("booking"));
                    review.setStar(rs.getInt("star"));
                    review.setTitle(rs.getString("title"));
                    review.setContent(rs.getString("content"));
                    reviews.add(review);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return reviews;
    }

    public List<Reviews> getReviewsByCustomerId(int customerId) {
        List<Reviews> reviews = new ArrayList<>();
        String sql = """
        SELECT r.booking, r.star, r.title, r.content
        FROM reviews r
        JOIN bookings b ON r.booking = b.id
        WHERE b.customer = ?
    """;

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, customerId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Reviews review = new Reviews();
                    review.setBooking(rs.getInt("booking"));
                    review.setStar(rs.getInt("star"));
                    review.setTitle(rs.getString("title"));
                    review.setContent(rs.getString("content"));
                    reviews.add(review);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return reviews;
    }

    // POST /customers/{customerId}/bookings/{bookingId}/reviews
    public boolean insertReview(Reviews review) {
        String sql = "INSERT INTO reviews (booking, star, title, content) VALUES (?, ?, ?, ?)";

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, review.getBooking());
            stmt.setInt(2, review.getStar());
            stmt.setString(3, review.getTitle());
            stmt.setString(4, review.getContent());
            int rows = stmt.executeUpdate();
            return rows > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false;
    }
}


