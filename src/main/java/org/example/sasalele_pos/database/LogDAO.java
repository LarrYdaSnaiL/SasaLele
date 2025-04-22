package org.example.sasalele_pos.database;

import org.example.sasalele_pos.model.Log;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class LogDAO {
    // Tambahkan log ke database
    public void addLog(Log log) {
        String sql = "INSERT INTO logs (type, description, timestamp) VALUES (?, ?, ?)";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, log.getType());
            pstmt.setString(2, log.getDescription());
            pstmt.setTimestamp(3, Timestamp.valueOf(log.getTimestamp()));
            pstmt.executeUpdate();

        } catch (SQLException e) {
            System.err.println("Gagal menambahkan log: " + e.getMessage());
        }
    }

    // Ambil log berdasarkan ID
    public Log getLogById(int logId) {
        String sql = "SELECT * FROM logs WHERE log_id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, logId);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                String type = rs.getString("type");
                String description = rs.getString("description");
                LocalDateTime timestamp = LocalDateTime.parse(rs.getString("timestamp"));

                return new Log(logId, type, description, timestamp);
            }
        } catch (SQLException e) {
            System.err.println("Gagal mengambil log: " + e.getMessage());
        }
        return null;
    }

    // Ambil semua log
    public static List<Log> getAllLogs() {
        List<Log> logs = new ArrayList<>();
        String sql = "SELECT * FROM logs";

        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                int logId = rs.getInt("log_id");
                String type = rs.getString("type");
                String description = rs.getString("description");
                LocalDateTime timestamp = LocalDateTime.parse(rs.getString("timestamp"));

                logs.add(new Log(logId, type, description, timestamp));
            }
        } catch (SQLException e) {
            System.err.println("Gagal mengambil semua log: " + e.getMessage());
        }
        return logs;
    }

    // Hapus log berdasarkan ID
    public boolean deleteLog(int logId) {
        String sql = "DELETE FROM logs WHERE log_id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, logId);
            int affectedRows = pstmt.executeUpdate();
            return affectedRows > 0;

        } catch (SQLException e) {
            System.err.println("Gagal menghapus log: " + e.getMessage());
            return false;
        }
    }
}