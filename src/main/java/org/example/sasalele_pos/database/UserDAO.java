package org.example.sasalele_pos.database;

import org.example.sasalele_pos.model.User;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

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
    public boolean updateUser(String oldUsername, String newUsername, String newRole) {
        String sql = "UPDATE users SET username = ?, role = ? WHERE username = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            // Set parameter untuk update
            pstmt.setString(1, newUsername);  // Set new username
            pstmt.setString(2, newRole);      // Set new role
            pstmt.setString(3, oldUsername); // Set old username sebagai kunci untuk update

            int affectedRows = pstmt.executeUpdate();
            return affectedRows > 0; // Mengembalikan true jika ada baris yang diupdate
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }


    // Method untuk menghapus user
    // Method untuk menghapus user berdasarkan username
    public boolean deleteUser(String username) {
        String sql = "DELETE FROM users WHERE username = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, username);
            int affectedRows = pstmt.executeUpdate();

            return affectedRows > 0; // True kalau berhasil hapus
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // Method untuk mengambil semua user
    public List<User> getAllUsers() {
        List<User> userList = new ArrayList<>();
        String sql = "SELECT * FROM users";

        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                String username = rs.getString("username");
                String password = rs.getString("password");
                String role = rs.getString("role");

                User user = new User(username, password, role);
                userList.add(user);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return userList;
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