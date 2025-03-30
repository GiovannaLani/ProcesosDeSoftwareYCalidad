package com.spq.client.data;

public enum Category {
    WOMAN("Mujer"),
    MAN("Hombre"),
    GIRL("Niña"),
    BOY("Niño");

    private final String displayName;

    Category(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}