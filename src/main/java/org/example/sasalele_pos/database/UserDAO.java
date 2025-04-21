package org.example.sasalele_pos.database;

import org.example.sasalele_pos.model.User;
import java.sql.*;

public class UserDAO {
    // Method untuk menambah user
    public void addUser(User user) {
        String sql = "INSERT INTO users(username, password, role) VALUES(?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, user.getUsername());
            pstmt.setString(2, user.getPassword());
            pstmt.setString(3, user.getRole());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Method untuk mengambil user berdasarkan username
    public User getUser(String username) {
        String sql = "SELECT * FROM users WHERE username = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, username);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                String password = rs.getString("password");
                String role = rs.getString("role");
                return new User(username, password, role);
            } else {
                return null;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    // Method untuk mengupdate data user
    public boolean updateUser(User user) {
        String sql = "UPDATE users SET password = ?, role = ? WHERE username = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, user.getPassword());
            pstmt.setString(2, user.getRole());
            pstmt.setString(3, user.getUsername());
            int affectedRows = pstmt.executeUpdate();
            return affectedRows > 0; // Mengembalikan true jika ada baris yang diupdate
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // Method untuk menghapus user
    public boolean deleteUser(String username) {
        String sql = "DELETE FROM users WHERE username = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, username);
            int affectedRows = pstmt.executeUpdate();
            return affectedRows > 0; // Mengembalikan true jika ada baris yang dihapus
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // Method untuk mengecek user
    public boolean checkUser(String username) {
        String sql = "SELECT * FROM users WHERE username = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, username);
            ResultSet rs = pstmt.executeQuery();

            return rs.next(); // Mengembalikan true jika user ditemukan
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}