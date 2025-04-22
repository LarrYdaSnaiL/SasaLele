package org.example.sasalele_pos.model;

import java.time.LocalDateTime;

public abstract class Log {
    private String id;
    private String type;
    private String description;
    private LocalDateTime timestamp;

    public Log(String id, String type, String description, LocalDateTime timestamp) {
        this.id = id;
        this.type = type;
        this.description = description;
        this.timestamp = timestamp;
    }

    public String getId() {
        return id;
    }

    public String getType() {
        return type;
    }

    public String getDescription() {
        return description;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }
}