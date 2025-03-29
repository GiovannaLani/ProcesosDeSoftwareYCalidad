package com.spq.vinted.dto;

import java.util.List;

import com.spq.vinted.model.ElectronicsType;

public class ElectronicsDTO extends ItemDTO {
    public ElectronicsType electronicsType;
    
     public ElectronicsDTO() {
    }

    
    public ElectronicsDTO(long id, String title, String description, float price, ElectronicsType type,List<String> images) {
        super(id, title, description, price,images);
        this.electronicsType = type;
    }

    public ElectronicsType getElectronicsType() {
        return electronicsType;
    }
    public void setElectronicsType(ElectronicsType type) {
        this.electronicsType = type;
    }
}