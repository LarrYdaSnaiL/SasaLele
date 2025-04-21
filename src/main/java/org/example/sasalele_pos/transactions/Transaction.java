package org.example.sasalele_pos.transactions;

import java.time.LocalDateTime;

public abstract class Transaction {
    private String transactionId;
    private LocalDateTime date;

    public Transaction(String transactionId, LocalDateTime date) {
        this.transactionId = transactionId;
        this.date = date;
    }

    // Getter
    public String getTransactionId() { return transactionId; }
    public LocalDateTime getDate() { return date; }

    // Method abstrak
    public abstract void processTransaction();
}