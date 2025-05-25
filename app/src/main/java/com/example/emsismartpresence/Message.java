package com.example.emsismartpresence;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Message {
    public static final int TYPE_USER = 0;
    public static final int TYPE_ASSISTANT = 1;

    private String text;
    private int type;
    private LocalDateTime timestamp;

    public Message(String text, int type, LocalDateTime timestamp) {
        this.text = text;
        this.type = type;
        this.timestamp = timestamp;
    }

    public String getText() {
        return text;
    }

    public int getType() {
        return type;
    }

    public String getFormattedTimestamp() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
        return timestamp.format(formatter);
    }
}