package com.spq.vinted.model;

import com.spq.vinted.dto.ElectronicsDTO;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;

@Entity
@Table(name = "electronics")
public class Electronics extends Item {

    public Electronics() {
    }

    public Electronics(long id, String title, String description, float price, String image, User seller) {
        super(id, title, description, price, image, seller);
    }
    
    public ElectronicsDTO toDTO() {
        return new ElectronicsDTO(getId(), getTitle(), getDescription(), getPrice(), getImage());
    }
}
