package org.example.sasalele_pos.model;

import java.time.LocalDateTime;

public class Log {
    private final int logId;
    private final String type;
    private final String description;
    private final LocalDateTime timestamp;

    // Constructor untuk pembuatan baru (tanpa logId)
    public Log(String type, String description, LocalDateTime timestamp) {
        this(0, type, description, timestamp);
    }

    // Constructor untuk data dari database
    public Log(int logId, String type, String description, LocalDateTime timestamp) {
        this.logId = logId;
        this.type = type;
        this.description = description;
        this.timestamp = timestamp;
    }

    // Getter methods
    public int getLogId() { return logId; }
    public String getType() { return type; }
    public String getDescription() { return description; }
    public LocalDateTime getTimestamp() { return timestamp; }
}