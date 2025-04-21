package org.example.sasalele_pos.transactions;

import org.example.sasalele_pos.interfaces.Payable;
import java.time.LocalDateTime;

public class RefundTransaction extends Transaction implements Payable {
    private final String username;       // ✅ Tambahkan final
    private final String originalTransactionId; // ✅ Tambahkan final
    private final double refundAmount;   // ✅ Deklarasi field refundAmount

    public RefundTransaction(
            String transactionId,
            LocalDateTime date,
            String username,
            String originalTransactionId,
            double refundAmount // ✅ Tambahkan parameter refundAmount
    ) {
        super(transactionId, date);
        this.username = username;
        this.originalTransactionId = originalTransactionId;
        this.refundAmount = refundAmount; // ✅ Inisialisasi refundAmount
    }

    // Getter
    public String getUsername() {
        return username;
    }

    public String getOriginalTransactionId() {
        return originalTransactionId;
    }

    @Override
    public double calculateTotal() {
        return refundAmount; // ✅ Kembalikan refundAmount
    }

    @Override
    public void processTransaction() {
        System.out.printf("Refund untuk transaksi %s telah diproses. Jumlah: Rp%,.2f%n",
                originalTransactionId, refundAmount);
    }

    @Override
    public String serializeTransaction() {
        return String.format(
                "REFUND|%s|%s|%s|%.2f",
                getTransactionId(),
                getDate().toString(),
                originalTransactionId,
                refundAmount
        );
    }
}