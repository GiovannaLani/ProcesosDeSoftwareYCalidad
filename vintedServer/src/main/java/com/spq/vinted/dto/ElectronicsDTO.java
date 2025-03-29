package com.spq.vinted.dto;

import java.util.List;

import com.spq.vinted.model.ElectronicsType;

public class ElectronicsDTO extends ItemDTO {
    public ElectronicsType type;
    
     public ElectronicsDTO() {
    }

    
    public ElectronicsDTO(long id, String title, String description, float price, ElectronicsType type,List<String> images) {
        super(id, title, description, price,images);
        this.type = type;
    }

    public ElectronicsType getType() {
        return type;
    }
    public void setType(ElectronicsType type) {
        this.type = type;
    }
}