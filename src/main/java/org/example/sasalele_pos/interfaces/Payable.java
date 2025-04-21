package org.example.sasalele_pos.interfaces;

public interface Payable {
    /**
     * Menghitung total transaksi (termasuk pajak, diskon, dll)
     */
    double calculateTotal();

    /**
     * Memproses transaksi (misal: mengurangi stok, mencatat log)
     */
    void processTransaction();

    /**
     * Mengonversi transaksi ke format string untuk disimpan di database
     */
    String serializeTransaction();
}