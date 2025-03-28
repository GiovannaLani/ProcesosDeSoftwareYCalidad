package com.spq.vinted.model;

import com.spq.vinted.dto.HomeDTO;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;

@Entity
@Table(name = "home")
public class Home extends Item {

    public Home() {
    }

    public Home(String title, String description, float price, String image, User seller) {
        super(title, description, price, image, seller);
    }
    
    public HomeDTO toDTO() {
        return new HomeDTO(getId(), getTitle(), getDescription(), getPrice());
    }
}
