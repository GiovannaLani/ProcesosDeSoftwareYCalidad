package com.spq.vinted.model;

import com.spq.vinted.dto.ElectronicsDTO;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;

@Entity
@Table(name = "electronics")
public class Electronics extends Item {
    public ElectronicsType type;
    public Electronics() {
    }

    public Electronics(String title, String description, float price, User seller, ElectronicsType type) {
        super(title, description, price, seller);
        this.type = type;
    }
    
    public ElectronicsType getType() {
        return type;
    }
    public void setType(ElectronicsType type) {
        this.type = type;
    }

    public ElectronicsDTO toDTO() {
        return new ElectronicsDTO(getId(), getTitle(), getDescription(), getPrice(), getType());
    }
}
