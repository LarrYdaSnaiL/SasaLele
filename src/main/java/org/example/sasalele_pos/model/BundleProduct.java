package org.example.sasalele_pos.model;

import java.util.List;

/**
 * Kelas turunan Product untuk merepresentasikan produk bundle yang terdiri dari beberapa produk lainnya.
 */
public class BundleProduct extends Product {
    private List<Product> products;
    private double discountRate;

    /**
     * Constructor untuk produk bundle.
     * @param id ID produk (unik)
     * @param name Nama produk
     * @param products Daftar produk dalam bundle (tidak boleh kosong)
     * @param discountRate Diskon dalam bentuk desimal (0.0 - 1.0)
     * @throws IllegalArgumentException jika parameter tidak valid
     */
    public BundleProduct(String id, String name, List<Product> products, double discountRate) {
        super(id, name, 0); // Harga sementara 0, akan diupdate setelah menghitung total
        setProducts(products);
        setDiscountRate(discountRate);
        calculateTotalPrice(); // Hitung harga total dari produk dalam bundle
    }

    // Getter & Setter dengan validasi
    public List<Product> getProducts() {
        return products;
    }

    public void setProducts(List<Product> products) {
        if (products == null || products.isEmpty()) {
            throw new IllegalArgumentException("Daftar produk tidak boleh kosong!");
        }
        this.products = products;
        calculateTotalPrice(); // Update harga setiap kali daftar produk diubah
    }

    public double getDiscountRate() {
        return discountRate;
    }

    public void setDiscountRate(double discountRate) {
        if (discountRate < 0 || discountRate > 1) {
            throw new IllegalArgumentException("Diskon harus antara 0.0 dan 1.0!");
        }
        this.discountRate = discountRate;
    }

    /**
     * Menghitung total harga dari semua produk dalam bundle dan mengupdate harga di superclass.
     */
    private void calculateTotalPrice() {
        double total = products.stream()
                .mapToDouble(Product::getPrice)
                .sum();
        super.setPrice(total); // Update harga di superclass
    }

    /**
     * Menghitung harga setelah diskon.
     * @return Harga setelah dikurangi diskon
     */
    public double getDiscountedPrice() {
        return super.getPrice() * (1 - discountRate);
    }

    /**
     * Mengembalikan tipe produk untuk database.
     * @return "BUNDLE"
     */
    @Override
    public String getProductType() {
        return "BUNDLE";
    }

    /**
     * Representasi string untuk debugging.
     * @return Format: [Bundle] [ID] Nama - RpHarga | Diskon: X% | Total Item: Y
     */
    @Override
    public String toString() {
        return String.format(
                "[Bundle] %s | Diskon: %.0f%% | Total Item: %d",
                super.toString(),
                discountRate * 100,
                products.size()
        );
    }
}