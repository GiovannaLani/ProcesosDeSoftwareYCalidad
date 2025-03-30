package com.spq.vinted.model;

import com.spq.vinted.dto.EntertainmentDTO;
import com.spq.vinted.dto.ItemDTO;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;

@Entity
@Table(name = "entertainment")
public class Entertainment extends Item {
    public EntertainmentType entertainmentType;

    public Entertainment() {
    }
    
    public Entertainment(String title, String description, float price, User seller, EntertainmentType type) {
        super(title, description, price, seller);
        this.entertainmentType = type;
    }

    public EntertainmentType getEntertainmentType() {
        return entertainmentType;
    }
    public void setEntertainmentType(EntertainmentType type) {
        this.entertainmentType = type;
    }

    @Override
    public ItemDTO toDTO() {
        return new EntertainmentDTO(getId(), getTitle(), getDescription(), getPrice(), getEntertainmentType(), getImages());
    }

}