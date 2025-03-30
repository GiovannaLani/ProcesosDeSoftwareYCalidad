package com.spq.client.data;

public enum Species {
    DOG("Perro"),
    CAT("Gato");

    private final String displayName;

    Species(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}