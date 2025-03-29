package com.spq.vinted.model;

import com.spq.vinted.dto.HomeDTO;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;

@Entity
@Table(name = "home")
public class Home extends Item {

    public HomeType type;

    public Home() {
    }

    public Home(String title, String description, float price, User seller, HomeType type) {
        super(title, description, price, seller);
        this.type = type;
    }
    
    public HomeType getType() {
        return type;
    }
    public void setType(HomeType type) {
        this.type = type;
    }
    public HomeDTO toDTO() {
        return new HomeDTO(getId(), getTitle(), getDescription(), getPrice(), getType());
    }
}
