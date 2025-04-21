package org.example.sasalele_pos.database;

import org.example.sasalele_pos.model.CartItem;
import org.example.sasalele_pos.model.Product;
import org.example.sasalele_pos.transactions.PurchaseTransaction;
import org.example.sasalele_pos.transactions.RefundTransaction;
import org.example.sasalele_pos.transactions.Transaction;
import java.sql.*;
import java.time.LocalDateTime;
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
        String transactionSql = "INSERT INTO transactions (transactionId, date, totalAmount, username, type) VALUES (?, ?, ?, ?, 'PURCHASE')";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(transactionSql)) {

            // Simpan data transaksi
            pstmt.setString(1, transaction.getTransactionId());
            pstmt.setString(2, transaction.getDate().toString());
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
        String sql = "INSERT INTO transactions (transactionId, date, totalAmount, username, type, originalTransactionId) VALUES (?, ?, ?, ?, 'REFUND', ?)";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, transaction.getTransactionId());
            pstmt.setString(2, transaction.getDate().toString());
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
        String sql = "INSERT INTO transaction_items (transactionId, productId, quantity, priceAtTransaction) VALUES (?, ?, ?, ?)";

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
        String sql = "SELECT * FROM transactions WHERE transactionId = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, transactionId);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                String type = rs.getString("type");
                LocalDateTime date = LocalDateTime.parse(rs.getString("date"));
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
                    String originalTransactionId = rs.getString("originalTransactionId");
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
        String sql = "SELECT productId, quantity FROM transaction_items WHERE transactionId = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, transactionId);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                String productId = rs.getString("productId");
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
        String sql = "SELECT transactionId FROM transactions";

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