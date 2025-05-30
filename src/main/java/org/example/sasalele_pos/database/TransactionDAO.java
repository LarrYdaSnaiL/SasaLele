package org.example.sasalele_pos.database;

import org.example.sasalele_pos.model.CartItem;
import org.example.sasalele_pos.model.Product;
import org.example.sasalele_pos.transactions.PurchaseTransaction;
import org.example.sasalele_pos.transactions.RefundTransaction;
import org.example.sasalele_pos.transactions.Transaction;

import java.sql.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class TransactionDAO {
    // Tambahkan transaksi ke database (PURCHASE atau REFUND)
    public void addTransaction(Transaction transaction) {
        if (transaction instanceof PurchaseTransaction) {
            addPurchaseTransaction((PurchaseTransaction) transaction);
        } else if (transaction instanceof RefundTransaction) {
            addRefundTransaction((RefundTransaction) transaction);
        }
    }

    // Method untuk transaksi pembelian
    private void addPurchaseTransaction(PurchaseTransaction transaction) {
        String transactionSql = "INSERT INTO transactions (transaction_id, date, total_amount, username, type) VALUES (?, ?, ?, ?, 'PURCHASE')";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(transactionSql)) {

            // Simpan data transaksi
            pstmt.setString(1, transaction.getTransactionId());
            pstmt.setTimestamp(2, Timestamp.valueOf(transaction.getDate()));
            pstmt.setDouble(3, transaction.calculateTotal());
            pstmt.setString(4, transaction.getUsername());
            pstmt.executeUpdate();

            // Simpan item transaksi
            insertTransactionItems(transaction);

        } catch (SQLException e) {
            System.err.println("Gagal menyimpan transaksi pembelian: " + e.getMessage());
        }
    }

    // Method untuk transaksi refund
    private void addRefundTransaction(RefundTransaction transaction) {
        String sql = "INSERT INTO transactions (transaction_id, date, total_amount, username, type, original_transaction_id) VALUES (?, ?, ?, ?, ?, ?)";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, transaction.getTransactionId());
            pstmt.setTimestamp(2, Timestamp.valueOf(transaction.getDate()));
            pstmt.setDouble(3, -transaction.calculateTotal()); // Simpan sebagai negatif
            pstmt.setString(4, transaction.getUsername());
            pstmt.setString(5, "REFUND");
            pstmt.setString(6, transaction.getOriginalTransactionId());
            pstmt.executeUpdate();

        } catch (SQLException e) {
            System.err.println("Gagal menyimpan transaksi refund: " + e.getMessage());
        }
    }

    // Simpan item transaksi ke tabel transaction_items
    private void insertTransactionItems(PurchaseTransaction transaction) throws SQLException {
        String sql = "INSERT INTO transaction_items (transaction_id, product_id, quantity, price_at_transaction) VALUES (?, ?, ?, ?)";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            for (CartItem item : transaction.getItems()) {
                pstmt.setString(1, transaction.getTransactionId());
                pstmt.setString(2, item.getProduct().getId());
                pstmt.setInt(3, item.getQuantity());
                pstmt.setDouble(4, item.getProduct().getPrice());
                pstmt.addBatch();
            }
            pstmt.executeBatch();
        }
    }

    // Ambil transaksi berdasarkan ID
    public Transaction getTransactionById(String transactionId) {
        String sql = "SELECT * FROM transactions WHERE transaction_id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, transactionId);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                String type = rs.getString("type");
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSSSSS");  // Custom format to handle fractional seconds
                LocalDateTime date = LocalDateTime.parse(rs.getString("date"), formatter);
                String username = rs.getString("username");

                // Di method getTransactionById()
                if ("PURCHASE".equals(type)) {
                    List<CartItem> items = getTransactionItems(transactionId);
                    return new PurchaseTransaction(
                            transactionId,
                            date,
                            username,  // ✅ Tambahkan username
                            items
                    );
                } else if ("REFUND".equals(type)) {
                    String originalTransactionId = rs.getString("original_transaction_id");
                    double refundAmount = rs.getDouble("totalAmount"); // Ambil nilai refundAmount dari kolom totalAmount
                    return new RefundTransaction(
                            transactionId,
                            date,
                            username,
                            originalTransactionId,
                            refundAmount // ✅ Parameter ke-5 (refundAmount)
                    );
                }
            }
        } catch (SQLException e) {
            System.err.println("Gagal mengambil transaksi: " + e.getMessage());
        }
        return null;
    }

    // Ambil item transaksi dari tabel transaction_items
    private List<CartItem> getTransactionItems(String transactionId) {
        List<CartItem> items = new ArrayList<>();
        String sql = "SELECT product_id, quantity FROM transaction_items WHERE transaction_id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, transactionId);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                String productId = rs.getString("product_id");
                int quantity = rs.getInt("quantity");
                Product product = new ProductDAO().getProductById(productId);

                if (product != null) {
                    items.add(new CartItem(product, quantity));
                }
            }
        } catch (SQLException e) {
            System.err.println("Gagal mengambil item transaksi: " + e.getMessage());
        }
        return items;
    }

    // Ambil semua transaksi
    public List<Transaction> getAllTransactions() {
        List<Transaction> transactions = new ArrayList<>();
        String sql = "SELECT transaction_id FROM transactions";

        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                Transaction t = getTransactionById(rs.getString("transactionId"));
                if (t != null) transactions.add(t);
            }
        } catch (SQLException e) {
            System.err.println("Gagal mengambil semua transaksi: " + e.getMessage());
        }
        return transactions;
    }
}