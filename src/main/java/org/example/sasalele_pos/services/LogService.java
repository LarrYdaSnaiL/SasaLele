package org.example.sasalele_pos.services;

import org.example.sasalele_pos.database.LogDAO;
import org.example.sasalele_pos.model.Log;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Layanan untuk manajemen log sistem.
 */
public class LogService {
    private final LogDAO logDAO;

    public LogService() {
        this.logDAO = new LogDAO();
    }

    /**
     * Mencatat aktivitas ke dalam sistem log.
     * @param type Jenis log (AUTH, PRODUCT_UPDATE, TRANSACTION, dll)
     * @param description Deskripsi aktivitas
     */
    public void logAction(String type, String description) {
        Log log = new Log(
                0, // ID akan di-generate otomatis oleh database
                type,
                description,
                LocalDateTime.now()
        );
        logDAO.addLog(log);
    }

    /**
     * Mengambil semua log dari database.
     */
    public List<Log> getAllLogs() {
        return logDAO.getAllLogs();
    }

    /**
     * Mengambil log berdasarkan jenis aktivitas.
     * @param type Jenis log yang ingin difilter (AUTH, PRODUCT_UPDATE, dll)
     */
    public List<Log> getLogsByType(String type) {
        return logDAO.getAllLogs().stream()
                .filter(log -> log.getType().equalsIgnoreCase(type))
                .collect(Collectors.toList());
    }

    /**
     * Mengambil log berdasarkan ID.
     */
    public Log getLogById(int logId) {
        return logDAO.getLogById(logId);
    }

    /**
     * Menghapus log tertentu.
     * @return true jika berhasil dihapus
     */
    public boolean deleteLog(int logId) {
        return logDAO.deleteLog(logId);
    }

    /**
     * Menghapus semua log (untuk keperluan maintenance).
     */
    public void purgeAllLogs() {
        logDAO.getAllLogs().forEach(log -> logDAO.deleteLog(log.getLogId()));
    }
}