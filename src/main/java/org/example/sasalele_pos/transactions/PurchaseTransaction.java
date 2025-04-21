package org.example.sasalele_pos.transactions;

import org.example.sasalele_pos.interfaces.Payable;
import org.example.sasalele_pos.model.CartItem;
import java.time.LocalDateTime;
import java.util.List;

public class PurchaseTransaction extends Transaction implements Payable {
    private final String username; // ✅ Tambahkan final
    private final List<CartItem> items; // ✅ Tambahkan final

    public PurchaseTransaction(
            String transactionId,
            LocalDateTime date,
            String username,
            List<CartItem> items
    ) {
        super(transactionId, date);
        this.username = username;
        this.items = items;
    }

    // Getter
    public String getUsername() {
        return username;
    }

    public List<CartItem> getItems() {
        return items;
    }

    @Override
    public double calculateTotal() {
        return items.stream()
                .mapToDouble(CartItem::calculateSubtotal)
                .sum();
    }

    @Override
    public void processTransaction() {
        System.out.printf("Transaksi dengan ID %s telah diproses. Total: Rp%,.2f%n",
                getTransactionId(), calculateTotal());
    }

    @Override
    public String serializeTransaction() {
        return String.format(
                "PURCHASE|%s|%s|%d|%.2f",
                getTransactionId(),
                getDate().toString(),
                items.size(),
                calculateTotal()
        );
    }
}