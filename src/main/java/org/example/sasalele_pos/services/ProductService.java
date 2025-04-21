package org.example.sasalele_pos.services;

import org.example.sasalele_pos.database.ProductDAO;
import org.example.sasalele_pos.exceptions.InvalidProductException;
import org.example.sasalele_pos.model.*;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * Layanan untuk mengelola logika bisnis produk.
 */
public class ProductService {
    private final ProductDAO productDAO;

    public ProductService() {
        this.productDAO = new ProductDAO();
    }

    // ---------------------- CORE METHODS ----------------------

    /**
     * Menambahkan produk baru dengan validasi ketat.
     */
    public void addProduct(Product product) throws InvalidProductException {
        validateProduct(product);
        productDAO.addProduct(product);
    }

    /**
     * Memperbarui data produk yang sudah ada.
     */
    public void updateProduct(Product product) throws InvalidProductException {
        validateProduct(product);

        // Pastikan produk ada di database
        Product existingProduct = productDAO.getProductById(product.getId());
        if (existingProduct == null) {
            throw new InvalidProductException("Produk tidak ditemukan!");
        }

        productDAO.updateProduct(product);
    }

    /**
     * Menghapus produk berdasarkan ID.
     */
    public boolean deleteProduct(String productId) {
        return productDAO.deleteProduct(productId);
    }

    // ---------------------- UTILITY METHODS ----------------------

    /**
     * Validasi semua jenis produk.
     */
    private void validateProduct(Product product) throws InvalidProductException {
        // Validasi dasar untuk semua produk
        if (product.getId() == null || product.getId().isBlank()) {
            throw new InvalidProductException("ID produk tidak valid!");
        }

        if (product.getName() == null || product.getName().isBlank()) {
            throw new InvalidProductException("Nama produk tidak valid!");
        }

        if (product.getPrice() < 0) {
            throw new InvalidProductException("Harga tidak boleh negatif!");
        }

        // Validasi khusus berdasarkan jenis produk
        if (product instanceof PerishableProduct) {
            validatePerishableProduct((PerishableProduct) product);
        }
        else if (product instanceof DigitalProduct) {
            validateDigitalProduct((DigitalProduct) product);
        }
        else if (product instanceof BundleProduct) {
            validateBundleProduct((BundleProduct) product);
        }
    }

    /**
     * Validasi produk perishable (tanggal kadaluarsa harus valid).
     */
    private void validatePerishableProduct(PerishableProduct product)
            throws InvalidProductException {

        LocalDate expiryDate = product.getExpiryDate();
        if (expiryDate == null || expiryDate.isBefore(LocalDate.now())) {
            throw new InvalidProductException(
                    "Tanggal kadaluarsa tidak valid atau sudah lewat!"
            );
        }
    }

    /**
     * Validasi produk digital (URL dan vendor harus valid).
     */
    private void validateDigitalProduct(DigitalProduct product)
            throws InvalidProductException {

        if (product.getUrl() == null || !product.getUrl().startsWith("http")) {
            throw new InvalidProductException("URL produk digital tidak valid!");
        }

        if (product.getVendorName() == null || product.getVendorName().isBlank()) {
            throw new InvalidProductException("Nama vendor tidak valid!");
        }
    }

    /**
     * Validasi produk bundle (minimal 2 item, diskon valid).
     */
    private void validateBundleProduct(BundleProduct product)
            throws InvalidProductException {

        List<Product> bundleItems = product.getProducts();
        if (bundleItems == null || bundleItems.size() < 2) {
            throw new InvalidProductException(
                    "Bundle harus memiliki minimal 2 produk!"
            );
        }

        if (product.getDiscountRate() < 0 || product.getDiscountRate() > 1) {
            throw new InvalidProductException(
                    "Diskon harus antara 0% hingga 100%!"
            );
        }
    }

    // ---------------------- GETTER METHODS ----------------------

    /**
     * Mengambil semua produk dari database.
     */
    public List<Product> getAllProducts() {
        return productDAO.getAllProducts();
    }

    /**
     * Mencari produk berdasarkan ID.
     */
    public Optional<Product> getProductById(String id) {
        Product product = productDAO.getProductById(id);
        return Optional.ofNullable(product);
    }

    /**
     * Mencari produk berdasarkan nama (case-insensitive).
     */
    public List<Product> searchProductsByName(String keyword) {
        return productDAO.getAllProducts().stream()
                .filter(p -> p.getName().toLowerCase().contains(keyword.toLowerCase()))
                .toList();
    }
}