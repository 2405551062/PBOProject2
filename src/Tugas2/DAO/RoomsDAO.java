package Tugas2.DAO;

import Tugas2.Model.Rooms;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class RoomsDAO {
    private final Connection conn;

    public RoomsDAO(Connection conn) {
        this.conn = conn;
    }

    public List<Rooms> getRoomsByVillaId(int villaId) {
        List<Rooms> rooms = new ArrayList<>();
        String sql = "SELECT * FROM room_types WHERE villa = ?";

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, villaId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Rooms r = new Rooms();
                    r.setId(rs.getInt("id"));
                    r.setVilla(rs.getInt("villa"));
                    r.setName(rs.getString("name"));
                    r.setQuantity(rs.getInt("quantity"));
                    r.setCapacity(rs.getInt("capacity"));
                    r.setPrice(rs.getInt("price"));
                    r.setBedSize(rs.getString("bed_size"));
                    r.setHasDesk(rs.getInt("has_desk"));
                    r.setHasAc(rs.getInt("has_ac"));
                    r.setHasTv(rs.getInt("has_tv"));
                    r.setHasWifi(rs.getInt("has_wifi"));
                    r.setHasShower(rs.getInt("has_shower"));
                    r.setHasHotwater(rs.getInt("has_hotwater"));
                    r.setHasFridge(rs.getInt("has_fridge"));
                    rooms.add(r);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return rooms;
    }

    public boolean insertRoom(Rooms room) {
        String sql = """
            INSERT INTO room_types (villa, name, quantity, capacity, price, bed_size,
                                    has_desk, has_ac, has_tv, has_wifi,
                                    has_shower, has_hotwater, has_fridge)
            VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
        """;

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, room.getVilla());
            stmt.setString(2, room.getName());
            stmt.setInt(3, room.getQuantity());
            stmt.setInt(4, room.getCapacity());
            stmt.setInt(5, room.getPrice());
            stmt.setString(6, room.getBedSize());
            stmt.setInt(7, room.getHasDesk());
            stmt.setInt(8, room.getHasAc());
            stmt.setInt(9, room.getHasTv());
            stmt.setInt(10, room.getHasWifi());
            stmt.setInt(11, room.getHasShower());
            stmt.setInt(12, room.getHasHotwater());
            stmt.setInt(13, room.getHasFridge());

            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false;
    }

    public boolean updateRoom(Rooms room) {
        String sql = """
        UPDATE room_types SET
        name = ?, quantity = ?, capacity = ?, price = ?, bed_size = ?,
        has_desk = ?, has_ac = ?, has_tv = ?, has_wifi = ?,
        has_shower = ?, has_hotwater = ?, has_fridge = ?
        WHERE id = ? AND villa = ?
    """;

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, room.getName());
            stmt.setInt(2, room.getQuantity());
            stmt.setInt(3, room.getCapacity());
            stmt.setInt(4, room.getPrice());
            stmt.setString(5, room.getBedSize());
            stmt.setInt(6, room.getHasDesk());
            stmt.setInt(7, room.getHasAc());
            stmt.setInt(8, room.getHasTv());
            stmt.setInt(9, room.getHasWifi());
            stmt.setInt(10, room.getHasShower());
            stmt.setInt(11, room.getHasHotwater());
            stmt.setInt(12, room.getHasFridge());
            stmt.setInt(13, room.getId());
            stmt.setInt(14, room.getVilla());

            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean deleteRoom(int roomId) {
        String sql = "DELETE FROM room_types WHERE id = ?";

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, roomId);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

}

