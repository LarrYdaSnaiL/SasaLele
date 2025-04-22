package org.example.sasalele_pos.services;

import org.example.sasalele_pos.transactions.Transaction;
import org.example.sasalele_pos.transactions.PurchaseTransaction;
import org.example.sasalele_pos.transactions.RefundTransaction;
import org.example.sasalele_pos.database.ProductDAO;
import org.example.sasalele_pos.database.TransactionDAO;
import org.example.sasalele_pos.exceptions.InvalidTransactionException;
import org.example.sasalele_pos.model.*;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Layanan untuk mengelola logika bisnis transaksi dan refund.
 */
public class TransactionService {
    private final TransactionDAO transactionDAO;
    private final ProductDAO productDAO;
    private final LogService logService;

    public TransactionService() {
        this.transactionDAO = new TransactionDAO();
        this.productDAO = new ProductDAO();
        this.logService = new LogService();
    }

    // ========================= CORE TRANSACTION METHODS =========================

    /**
     * Memproses transaksi penjualan baru.
     * @param items Item yang dibeli
     * @param paymentAmount Uang yang dibayarkan customer
     * @param username Username kasir yang login
     * @return Kembalian yang harus diberikan
     */
    public double processSale(List<CartItem> items, double paymentAmount, String username)
            throws InvalidTransactionException {

        // Validasi dasar
        validateTransactionItems(items);
        validatePayment(paymentAmount);

        // Hitung total transaksi
        PurchaseTransaction transaction = new PurchaseTransaction(
                generateTransactionId(),
                LocalDateTime.now(),
                username,
                items
        );
        double total = transaction.calculateTotal();

        // Validasi pembayaran
        if (paymentAmount < total) {
            throw new InvalidTransactionException("Pembayaran kurang Rp" + (total - paymentAmount));
        }

        try {
            // Proses transaksi
            processTransaction(transaction);
            updateStock(items, false); // Kurangi stok

            // Catat log
            logService.logAction(
                    "TRANSACTION",
                    "Transaksi " + transaction.getTransactionId() + " berhasil diproses. Total: Rp" + total
            );

            return paymentAmount - total;

        } catch (Exception e) {
            logService.logAction("ERROR", "Gagal memproses transaksi: " + e.getMessage());
            throw new InvalidTransactionException(e.getMessage());
        }
    }

    /**
     * Memproses refund untuk transaksi yang sudah ada.
     * @param originalTransactionId ID transaksi asli
     * @param username Username kasir yang memproses
     */
    public void processRefund(String originalTransactionId, String username)
            throws InvalidTransactionException {

        // Validasi transaksi asli
        PurchaseTransaction originalTx = (PurchaseTransaction) transactionDAO.getTransactionById(originalTransactionId);
        if (originalTx == null) {
            throw new InvalidTransactionException("Transaksi asli tidak ditemukan!");
        }

        try {
            // Buat transaksi refund
            RefundTransaction refund = new RefundTransaction(
                    generateTransactionId(),
                    LocalDateTime.now(),
                    username,
                    originalTransactionId,
                    originalTx.calculateTotal() // Refund penuh
            );

            // Proses refund
            processTransaction(refund);
            updateStock(originalTx.getItems(), true); // Kembalikan stok

            // Catat log
            logService.logAction(
                    "REFUND",
                    "Refund " + refund.getTransactionId() + " untuk transaksi " + originalTransactionId
            );

        } catch (Exception e) {
            logService.logAction("ERROR", "Gagal memproses refund: " + e.getMessage());
            throw new InvalidTransactionException(e.getMessage());
        }
    }

    // ========================= UTILITY METHODS =========================

    private void processTransaction(Transaction transaction) {
        transactionDAO.addTransaction(transaction);
        transaction.processTransaction(); // Panggil method dari objek konkret
    }

    private void updateStock(List<CartItem> items, boolean isRestock) {
        for (CartItem item : items) {
            Product product = productDAO.getProductById(item.getProduct().getId());
            if (product instanceof NonPerishableProduct || product instanceof PerishableProduct) {
                // Hanya update stok untuk produk fisik
                // (Implementasi manajemen stok bisa ditambahkan di ProductDAO)
            }
        }
    }

    private void validateTransactionItems(List<CartItem> items) throws InvalidTransactionException {
        if (items == null || items.isEmpty()) {
            throw new InvalidTransactionException("Keranjang belanja tidak boleh kosong!");
        }
        for (CartItem item : items) {
            if (item.getQuantity() < 1) {
                throw new InvalidTransactionException("Kuantitas item tidak valid!");
            }
        }
    }

    private void validatePayment(double paymentAmount) throws InvalidTransactionException {
        if (paymentAmount <= 0) {
            throw new InvalidTransactionException("Jumlah pembayaran tidak valid!");
        }
    }

    private String generateTransactionId() {
        return "TX-" + System.currentTimeMillis();
    }

    // ========================= GETTER METHODS =========================

    public List<Transaction> getTransactionHistory() {
        return transactionDAO.getAllTransactions();
    }

    public Transaction getTransactionDetails(String transactionId) {
        return transactionDAO.getTransactionById(transactionId);
    }
}