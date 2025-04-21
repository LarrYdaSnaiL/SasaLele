package org.example.sasalele_pos.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class DBConnection {
    private static final String DB_URL = "jdbc:postgresql://aws-0-ap-southeast-1.pooler.supabase.com:6543/postgres";
    private static final String DB_USER = "postgres.iymubalwbbtrwzcufdgq";
    private static final String DB_PASSWORD = "SasalelePOS123!";

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
    }

    // Inisialisasi tabel database (dijalankan sekali saat aplikasi pertama kali dijalankan)
    public static void initializeDatabase() {
        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement()) {

            // Tambahkan indeks untuk pencarian cepat
            stmt.execute("CREATE INDEX IF NOT EXISTS idx_users_username ON users(username)");
            stmt.execute("CREATE INDEX IF NOT EXISTS idx_products_id ON products(id)");
            stmt.execute("CREATE INDEX IF NOT EXISTS idx_transactions_id ON transactions(\"transaction_id\")");

        } catch (SQLException e) {
            System.err.println("[ERROR] Gagal inisialisasi database: " + e.getMessage());
        }
    }

    // Untuk testing koneksi
    public static boolean testConnection() {
        try (Connection conn = getConnection()) {
            return !conn.isClosed();
        } catch (SQLException e) {
            return false;
        }
    }
}