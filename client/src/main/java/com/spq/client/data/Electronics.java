package com.spq.client.data;

public class Electronics extends Item {
    
    ElectronicsType type;
    public Electronics(String title, String description, float price, ElectronicsType type) {
        super(title, description, price);
        this.type = type;
    }
    public ElectronicsType getType() {
        return type;
    }
    public void setType(ElectronicsType type) {
        this.type = type;
    }
}