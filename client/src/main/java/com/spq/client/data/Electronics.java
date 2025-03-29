package com.spq.client.data;

import com.fasterxml.jackson.annotation.JsonTypeName;

@JsonTypeName("electronics")
public class Electronics extends Item {
    
    ElectronicsType electronicsType;
    public Electronics(String title, String description, float price, ElectronicsType type) {
        super(title, description, price);
        this.electronicsType = type;
    }
    public ElectronicsType getElectronicsType() {
        return electronicsType;
    }
    public void setElectronicsType(ElectronicsType type) {
        this.electronicsType = type;
    }
}