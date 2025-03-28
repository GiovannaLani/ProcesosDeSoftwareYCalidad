package com.spq.vinted.model;

import com.spq.vinted.dto.EntertainmentDTO;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;

@Entity
@Table(name = "entertainment")
public class Entertainment extends Item {
    
    public Entertainment() {
    }
    
    public Entertainment(String title, String description, float price, String image, User seller) {
        super(title, description, price, image, seller);
    }

    public EntertainmentDTO toDTO() {
        return new EntertainmentDTO(getId(), getTitle(), getDescription(), getPrice());
    }

}