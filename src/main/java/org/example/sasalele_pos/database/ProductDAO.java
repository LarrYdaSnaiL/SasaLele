package org.example.sasalele_pos.database;

import org.example.sasalele_pos.model.*;
import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class ProductDAO {
    // Tambahkan produk ke database
    public void addProduct(Product product) {
        String sql = "INSERT INTO products(id, name, price, type, expiry_date, url, vendor_name) VALUES(?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            // Data umum untuk semua produk
            pstmt.setString(1, product.getId());
            pstmt.setString(2, product.getName());
            pstmt.setDouble(3, product.getPrice());
            pstmt.setString(4, product.getProductType());

            // Data khusus berdasarkan jenis produk
            if (product instanceof PerishableProduct) {
                PerishableProduct pp = (PerishableProduct) product;
                pstmt.setDate(5, Date.valueOf(pp.getExpiryDate()));
                pstmt.setNull(6, Types.VARCHAR); // url
                pstmt.setNull(7, Types.VARCHAR); // vendorName
            }
            else if (product instanceof DigitalProduct) {
                DigitalProduct dp = (DigitalProduct) product;
                pstmt.setNull(5, Types.DATE); // expiryDate
                pstmt.setString(6, dp.getUrl());
                pstmt.setString(7, dp.getVendorName());
            }
            else { // NonPerishableProduct atau BundleProduct
                pstmt.setNull(5, Types.DATE); // expiryDate
                pstmt.setNull(6, Types.VARCHAR); // url
                pstmt.setNull(7, Types.VARCHAR); // vendorName
            }

            pstmt.executeUpdate();

        } catch (SQLException e) {
            System.err.println("Gagal menambahkan produk: " + e.getMessage());
        }
    }

    // Ambil produk berdasarkan ID
    public static Product getProductById(String id) {
        String sql = "SELECT * FROM products WHERE id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, id);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                String type = rs.getString("type");
                String name = rs.getString("name");
                double price = rs.getDouble("price");

                switch (type) {
                    case "PERISHABLE":
                        LocalDate expiryDate = LocalDate.parse(rs.getString("expiry_date"));
                        return new PerishableProduct(id, name, price, expiryDate);

                    case "DIGITAL":
                        String url = rs.getString("url");
                        String vendorName = rs.getString("vendor_name");
                        return new DigitalProduct(id, name, price, url, vendorName);

                    case "BUNDLE":
                        // Ambil item bundle dari tabel terpisah (bundle_items)
                        List<Product> bundleItems = getBundleItems(id);
                        return new BundleProduct(id, name, bundleItems, 0); // Diskon diambil dari logika lain

                    default: // NON_PERISHABLE
                        return new NonPerishableProduct(id, name, price);
                }
            }
        } catch (SQLException e) {
            System.err.println("Gagal mengambil produk: " + e.getMessage());
        }
        return null;
    }

    // Update produk
    public boolean updateProduct(Product product) {
        String sql = "UPDATE products SET name = ?, price = ?, expiry_date = ?, url = ?, vendor_name = ? WHERE id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, product.getName());
            pstmt.setDouble(2, product.getPrice());

            if (product instanceof PerishableProduct) {
                pstmt.setString(3, ((PerishableProduct) product).getExpiryDate().toString());
                pstmt.setNull(4, Types.VARCHAR);
                pstmt.setNull(5, Types.VARCHAR);
            }
            else if (product instanceof DigitalProduct) {
                pstmt.setNull(3, Types.VARCHAR);
                pstmt.setString(4, ((DigitalProduct) product).getUrl());
                pstmt.setString(5, ((DigitalProduct) product).getVendorName());
            }
            else {
                pstmt.setNull(3, Types.VARCHAR);
                pstmt.setNull(4, Types.VARCHAR);
                pstmt.setNull(5, Types.VARCHAR);
            }

            pstmt.setString(6, product.getId());
            int affectedRows = pstmt.executeUpdate();
            return affectedRows > 0;

        } catch (SQLException e) {
            System.err.println("Gagal update produk: " + e.getMessage());
            return false;
        }
    }

    // Hapus produk
    public boolean deleteProduct(String id) {
        String sql = "DELETE FROM products WHERE id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, id);
            int affectedRows = pstmt.executeUpdate();
            return affectedRows > 0;

        } catch (SQLException e) {
            System.err.println("Gagal hapus produk: " + e.getMessage());
            return false;
        }
    }

    // Ambil semua produk (untuk pencarian)
    public static List<Product> getAllProducts() {
        List<Product> products = new ArrayList<>();
        String sql = "SELECT * FROM products";

        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                Product p = getProductById(rs.getString("id"));
                if (p != null) products.add(p);
            }
        } catch (SQLException e) {
            System.err.println("Gagal mengambil semua produk: " + e.getMessage());
        }
        return products;
    }

    // Method khusus untuk BundleProduct (Ambil item dari tabel bundle_items)
    private static List<Product> getBundleItems(String bundleId) {
        List<Product> items = new ArrayList<>();
        String sql = "SELECT productId FROM bundle_items WHERE bundleId = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, bundleId);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                Product p = getProductById(rs.getString("productId"));
                if (p != null) items.add(p);
            }
        } catch (SQLException e) {
            System.err.println("Gagal mengambil item bundle: " + e.getMessage());
        }
        return items;
    }
}