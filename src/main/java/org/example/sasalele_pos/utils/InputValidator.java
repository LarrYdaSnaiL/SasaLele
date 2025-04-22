package org.example.sasalele_pos.utils;

import java.time.LocalDate;

/**
 * Kelas utilitas untuk validasi input sesuai aturan bisnis.
 */
public class InputValidator {

    // ---------------------- VALIDASI UMUM ----------------------

    /**
     * Memvalidasi apakah string tidak kosong/null.
     */
    public static boolean isNotEmpty(String input) {
        return input != null && !input.trim().isEmpty();
    }

    // ---------------------- VALIDASI USER ----------------------

    /**
     * Memvalidasi format username (alfanumerik + underscore, 5-20 karakter).
     */
    public static boolean isValidUsername(String username) {
        return username != null && username.matches("^[a-zA-Z0-9_]{5,20}$");
    }

    /**
     * Memvalidasi kekuatan password (minimal 8 karakter, mengandung huruf dan angka).
     */
    public static boolean isValidPassword(String password) {
        return password != null &&
                password.length() >= 8 &&
                password.matches("^(?=.*[A-Za-z])(?=.*\\d).+$");
    }

    // ---------------------- VALIDASI PRODUK ----------------------

    /**
     * Memvalidasi harga produk (tidak boleh negatif).
     */
    public static boolean isValidPrice(double price) {
        return price >= 0;
    }

    /**
     * Memvalidasi tanggal kadaluarsa (tidak boleh sebelum hari ini).
     */
    public static boolean isValidExpiryDate(LocalDate expiryDate) {
        return expiryDate != null &&
                !expiryDate.isBefore(LocalDate.now());
    }

    /**
     * Memvalidasi URL produk digital (harus dimulai dengan http/https).
     */
    public static boolean isValidUrl(String url) {
        return url != null &&
                url.matches("^(https?://).+");
    }

    // ---------------------- VALIDASI TRANSAKSI ----------------------

    /**
     * Memvalidasi jumlah pembayaran (harus cukup untuk menutup total belanja).
     */
    public static boolean isPaymentSufficient(double payment, double total) {
        return payment >= total;
    }

    /**
     * Memvalidasi kuantitas item (minimal 1).
     */
    public static boolean isValidQuantity(int quantity) {
        return quantity > 0;
    }

    public static boolean isValidPaymentString(String input) {
        return input.matches("^\\d+(\\.\\d{1,2})?$");
    }
}