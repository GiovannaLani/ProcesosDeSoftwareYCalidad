package com.spq.client.data;

public enum EntertainmentType {
    BOOK("Libro"),
    GAME("Juego");

    private final String displayName;

    EntertainmentType(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}