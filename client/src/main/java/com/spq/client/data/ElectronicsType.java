package com.spq.client.data;

public enum ElectronicsType {
    DEVICE("Dispositivo"),
    VIDEOGAME("Videojuego");

    private final String displayName;

    ElectronicsType(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}