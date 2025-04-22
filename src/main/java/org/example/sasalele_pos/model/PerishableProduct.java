package org.example.sasalele_pos.model;

import java.time.LocalDate;

/**
 * Kelas turunan Product untuk merepresentasikan produk yang memiliki tanggal kadaluarsa.
 */
public class PerishableProduct extends Product {
    private LocalDate expiryDate;

    /**
     * Constructor untuk produk perishable.
     * @param id ID produk (unik)
     * @param name Nama produk
     * @param price Harga satuan
     * @param expiryDate Tanggal kadaluarsa (tidak boleh lebih awal dari hari ini)
     * @throws IllegalArgumentException jika tanggal kadaluarsa invalid
     */
    public PerishableProduct(String id, String name, double price, LocalDate expiryDate) {
        super(id, name, price);
        setExpiryDate(expiryDate); // Gunakan setter untuk validasi
    }

    // Getter & Setter dengan validasi
    public LocalDate getExpiryDate() {
        return expiryDate;
    }

    /**
     * Mengupdate tanggal kadaluarsa produk.
     * @param expiryDate Tanggal baru (harus lebih besar dari tanggal hari ini)
     * @throws IllegalArgumentException jika tanggal tidak valid
     */
    public void setExpiryDate(LocalDate expiryDate) {
        if (expiryDate.isBefore(LocalDate.now())) {
            throw new IllegalArgumentException("Tanggal kadaluarsa tidak boleh lebih awal dari hari ini!");
        }
        this.expiryDate = expiryDate;
    }

    /**
     * Memeriksa apakah produk sudah kadaluarsa.
     * @return true jika produk sudah kadaluarsa
     */
    public boolean isExpired() {
        return LocalDate.now().isAfter(expiryDate);
    }

    /**
     * Menghitung sisa hari hingga kadaluarsa.
     * @return Jumlah hari tersisa (negatif jika sudah kadaluarsa)
     */
    public int daysUntilExpiry() {
        return LocalDate.now().until(expiryDate).getDays();
    }

    /**
     * Konversi ke format String untuk penyimpanan database.
     * @return Tanggal dalam format String (yyyy-MM-dd)
     */
    public String getExpiryDateForDB() {
        return expiryDate.toString();
    }

    @Override
    public String toString() {
        return String.format(
                "[Perishable] %s | Kadaluarsa: %s | Status: %s",
                super.toString(),
                expiryDate,
                isExpired() ? "EXPIRED" : "VALID"
        );
    }

    /**
     * Implementasi untuk tipe produk (digunakan dalam database)
     */
    public String getProductType() {
        return "PERISHABLE";
    }
}