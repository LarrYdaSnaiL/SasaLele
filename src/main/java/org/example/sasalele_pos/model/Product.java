package org.example.sasalele_pos.model;

import java.util.Date;

/**
 * Kelas abstrak yang merepresentasikan produk dasar dalam sistem POS.
 */
public abstract class Product {
    private String id;
    private String name;
    private double price;

    /**
     * Constructor untuk membuat objek Product.
     * @param id ID unik produk (tidak boleh null atau kosong)
     * @param name Nama produk (tidak boleh null atau kosong)
     * @param price Harga produk (harus >= 0)
     * @throws IllegalArgumentException jika parameter tidak valid
     */
    public Product(String id, String name, double price) {
        setId(id);
        setName(name);
        setPrice(price);
    }

    // Getter & Setter dengan validasi
    public String getId() {
        return id;
    }

    public void setId(String id) {
        if (id == null || id.trim().isEmpty()) {
            throw new IllegalArgumentException("ID produk tidak boleh kosong!");
        }
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Nama produk tidak boleh kosong!");
        }
        this.name = name;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        if (price < 0) {
            throw new IllegalArgumentException("Harga produk tidak boleh negatif!");
        }
        this.price = price;
    }

    /**
     * Method abstrak untuk mendapatkan tipe produk.
     * @return Tipe produk (contoh: "PERISHABLE", "DIGITAL")
     */
    public abstract String getProductType();

    /**
     * Representasi string untuk keperluan debugging.
     * @return String dalam format: [ID] Nama - RpHarga
     */
    @Override
    public String toString() {
        return String.format("[%s] %s - Rp%,.2f", id, name, price);
    }

    /**
     * Membandingkan kesamaan produk berdasarkan ID.
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Product product = (Product) o;
        return id.equals(product.id);
    }

    /**
     * Menghasilkan hash code berdasarkan ID.
     */
    @Override
    public int hashCode() {
        return id.hashCode();
    }
}