package com.spq.client.data;

public enum HomeType {
    FURNITURE("Mueble"),
    DECORATION("Decoración");

    private final String displayName;

    HomeType(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}