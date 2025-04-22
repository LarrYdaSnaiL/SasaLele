package org.example.sasalele_pos.model;

/**
 * Kelas turunan Product untuk merepresentasikan produk yang tidak memiliki tanggal kadaluarsa.
 */
public class NonPerishableProduct extends Product {

    /**
     * Constructor untuk produk non-perishable.
     * @param id ID produk (unik)
     * @param name Nama produk
     * @param price Harga satuan
     * @throws IllegalArgumentException jika parameter tidak valid
     */
    public NonPerishableProduct(String id, String name, double price) {
        super(id, name, price);
    }

    /**
     * Mengembalikan tipe produk untuk database.
     * @return "NON_PERISHABLE"
     */
    @Override
    public String getProductType() {
        return "NON_PERISHABLE";
    }

    /**
     * Representasi string untuk debugging.
     * @return Format: [Non-Perishable] [ID] Nama - RpHarga
     */
    @Override
    public String toString() {
        return String.format("[Non-Perishable] %s", super.toString());
    }
}