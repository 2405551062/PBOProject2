package Tugas2.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DB {
    private static Connection conn = null;

    public static Connection getConnection() throws SQLException {
        if (conn == null || conn.isClosed()) {
            String url = "jdbc:sqlite:vbook.db";
            conn = DriverManager.getConnection(url);
        }
        return conn;
    }
}
