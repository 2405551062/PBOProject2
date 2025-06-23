package Tugas2.DAO;

import Tugas2.Model.Vouchers;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class VouchersDAO {
    private final Connection conn;

    public VouchersDAO(Connection conn) {
        this.conn = conn;
    }

    // GET /vouchers - Ambil semua voucher
    public List<Vouchers> getAllVouchers() {
        List<Vouchers> vouchers = new ArrayList<>();
        String sql = "SELECT * FROM vouchers ORDER BY id";

        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                vouchers.add(mapResultSetToVoucher(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return vouchers;
    }

    // GET /vouchers/{id} - Ambil voucher berdasarkan ID
    public Vouchers getVoucherById(int id) {
        String sql = "SELECT * FROM vouchers WHERE id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToVoucher(rs);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }

    // POST /vouchers - Tambahkan voucher baru
    public boolean insertVoucher(Vouchers voucher) {
        String sql = """
            INSERT INTO vouchers (code, description, discount, start_date, end_date)
            VALUES (?, ?, ?, ?, ?)
        """;

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, voucher.getCode());
            stmt.setString(2, voucher.getDescription());
            stmt.setDouble(3, voucher.getDiscount());
            stmt.setString(4, voucher.getStartDate());
            stmt.setString(5, voucher.getEndDate());

            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false;
    }

    // PUT /vouchers/{id} - Update voucher berdasarkan ID
    public boolean updateVoucher(int id, Vouchers voucher) {
        String sql = """
            UPDATE vouchers
            SET code = ?, description = ?, discount = ?, start_date = ?, end_date = ?
            WHERE id = ?
        """;

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, voucher.getCode());
            stmt.setString(2, voucher.getDescription());
            stmt.setDouble(3, voucher.getDiscount());
            stmt.setString(4, voucher.getStartDate());
            stmt.setString(5, voucher.getEndDate());
            stmt.setInt(6, id);

            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false;
    }

    // DELETE /vouchers/{id} - Hapus voucher berdasarkan ID
    public boolean deleteVoucher(int id) {
        String sql = "DELETE FROM vouchers WHERE id = ?";

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false;
    }

    // Helper method untuk mapping ResultSet ke objek Vouchers
    private Vouchers mapResultSetToVoucher(ResultSet rs) throws SQLException {
        Vouchers voucher = new Vouchers();
        voucher.setId(rs.getInt("id"));
        voucher.setCode(rs.getString("code"));
        voucher.setDescription(rs.getString("description"));
        voucher.setDiscount(rs.getDouble("discount"));
        voucher.setStartDate(rs.getString("start_date"));
        voucher.setEndDate(rs.getString("end_date"));
        return voucher;
    }
}
