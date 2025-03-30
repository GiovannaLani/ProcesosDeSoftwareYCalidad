package com.spq.client.data;

public enum ClothesType {
    TSHIRT("Camiseta"),
    PANTS("Pantalones"),
    JACKET("Chaqueta"),
    SKIRT("Falda"),
    DRESS("Vestido"),
    SWEATER("Suéter"),
    SHORTS("Pantalón corto");

    private final String displayName;

    ClothesType(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
