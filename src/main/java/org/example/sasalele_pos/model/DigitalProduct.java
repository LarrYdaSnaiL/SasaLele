package org.example.sasalele_pos.model;

/**
 * Kelas turunan Product untuk merepresentasikan produk digital
 * yang memiliki URL dan vendor.
 */
public class DigitalProduct extends Product {
    private String url;
    private String vendorName;

    /**
     * Constructor untuk produk digital.
     * @param id ID produk (unik)
     * @param name Nama produk
     * @param price Harga satuan
     * @param url URL produk digital (harus valid)
     * @param vendorName Nama vendor/penyedia
     * @throws IllegalArgumentException jika parameter tidak valid
     */
    public DigitalProduct(String id, String name, double price, String url, String vendorName) {
        super(id, name, price);
        setUrl(url);
        setVendorName(vendorName);
    }

    // Getter & Setter dengan validasi
    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        if (url == null || url.isBlank()) {
            throw new IllegalArgumentException("URL tidak boleh kosong!");
        }
        this.url = url;
    }

    public String getVendorName() {
        return vendorName;
    }

    public void setVendorName(String vendorName) {
        if (vendorName == null || vendorName.isBlank()) {
            throw new IllegalArgumentException("Nama vendor tidak boleh kosong!");
        }
        this.vendorName = vendorName;
    }

    /**
     * Mengembalikan tipe produk untuk database.
     * @return "DIGITAL"
     */
    @Override
    public String getProductType() {
        return "DIGITAL";
    }

    /**
     * Representasi string untuk debugging.
     * @return Format: [Digital] [ID] Nama - RpHarga | Vendor: VendorName | URL: URL
     */
    @Override
    public String toString() {
        return String.format(
                "[Digital] %s | Vendor: %s | URL: %s",
                super.toString(),
                vendorName,
                url
        );
    }
}