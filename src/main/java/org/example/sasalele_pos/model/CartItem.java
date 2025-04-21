package org.example.sasalele_pos.model;

/**
 * Kelas untuk merepresentasikan item dalam keranjang belanja.
 * Menyimpan informasi produk dan kuantitasnya.
 */
public class CartItem {
    private Product product;
    private int quantity;

    /**
     * Constructor untuk membuat CartItem.
     * @param product Produk yang ditambahkan ke keranjang (tidak boleh null)
     * @param quantity Kuantitas produk (minimal 1)
     * @throws IllegalArgumentException jika parameter tidak valid
     */
    public CartItem(Product product, int quantity) {
        setProduct(product);
        setQuantity(quantity);
    }

    // Getter & Setter dengan validasi
    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        if (product == null) {
            throw new IllegalArgumentException("Produk tidak boleh null!");
        }
        this.product = product;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        if (quantity < 1) {
            throw new IllegalArgumentException("Kuantitas minimal 1!");
        }
        this.quantity = quantity;
    }

    /**
     * Menghitung subtotal untuk item ini (harga produk * kuantitas)
     * @return Subtotal dalam bentuk double
     */
    public double calculateSubtotal() {
        return product.getPrice() * quantity;
    }

    /**
     * Representasi string untuk debugging/logging.
     * Format: [Nama Produk] x[Kuantitas] - Rp[Subtotal]
     */
    @Override
    public String toString() {
        return String.format(
                "%s x%d - Rp%,.2f",
                product.getName(),
                quantity,
                calculateSubtotal()
        );
    }

    /**
     * Membandingkan kesamaan CartItem berdasarkan ID produk dan kuantitas.
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CartItem cartItem = (CartItem) o;
        return quantity == cartItem.quantity &&
                product.getId().equals(cartItem.product.getId());
    }

    /**
     * Menghasilkan hash code berdasarkan ID produk dan kuantitas.
     */
    @Override
    public int hashCode() {
        return 31 * product.getId().hashCode() + quantity;
    }
}