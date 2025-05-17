package com.spq.client.data;

public record Ad (
    long id,
    String title,
    String description,
    String imageUrl
) {
    public Ad(String title, String description) {
        this(0L, title, description, null);
    }
}