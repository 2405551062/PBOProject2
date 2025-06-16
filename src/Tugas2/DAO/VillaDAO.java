package Tugas2.DAO;

import Tugas2.Model.Villas;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class VillaDAO {
    private final Connection conn;

    public VillaDAO(Connection conn) {
        this.conn = conn;
    }

    public List<Villas> getAllVillas() {
        List<Villas> villas = new ArrayList<>();
        String sql = "SELECT id, name, location, price FROM villas";

        try (PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                Villas villa = new Villas();
                villa.setId(rs.getInt("id"));
                villa.setName(rs.getString("name"));
                villa.setDescription(rs.getString("description"));
                villa.setAddress(rs.getString("address"));
                villas.add(villa);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return villas;
    }

    public Villas getVillaById(int id) {
        String sql = "SELECT id, name, location, price FROM villas WHERE id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    Villas villa = new Villas();
                    villa.setId(rs.getInt("id"));
                    villa.setName(rs.getString("name"));
                    villa.setDescription(rs.getString("description"));
                    villa.setAddress(rs.getString("address"));
                    return villa;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public boolean insertVilla(Villas villa) {
        String sql = "INSERT INTO villas (name, location, price) VALUES (?, ?, ?)";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, villa.getName());
            stmt.setString(2, villa.getDescription());
            stmt.setString(3, villa.getAddress());
            int rows = stmt.executeUpdate();
            return rows > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean updateVilla(Villas villa) {
        String sql = "UPDATE villas SET name = ?, location = ?, price = ? WHERE id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, villa.getName());
            stmt.setString(2, villa.getDescription());
            stmt.setString(3, villa.getAddress());
            stmt.setInt(4, villa.getId());
            int rows = stmt.executeUpdate();
            return rows > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean deleteVilla(int id) {
        String sql = "DELETE FROM villas WHERE id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            int rows = stmt.executeUpdate();
            return rows > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
}
