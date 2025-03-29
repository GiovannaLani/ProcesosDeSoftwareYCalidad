package com.spq.vinted.dto;

import java.util.List;

import com.spq.vinted.model.ElectronicsType;

public class ElectronicsDTO extends ItemDTO {
    public ElectronicsType electronisType;
    
     public ElectronicsDTO() {
    }

    
    public ElectronicsDTO(long id, String title, String description, float price, ElectronicsType type,List<String> images) {
        super(id, title, description, price,images);
        this.electronisType = type;
    }

    public ElectronicsType getElectronisType() {
        return electronisType;
    }
    public void setElectronisType(ElectronicsType type) {
        this.electronisType = type;
    }
}