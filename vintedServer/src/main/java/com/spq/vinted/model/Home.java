package com.spq.vinted.model;

import com.spq.vinted.dto.HomeDTO;
import com.spq.vinted.dto.ItemDTO;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;

@Entity
@Table(name = "home")
public class Home extends Item {

    public HomeType homeType;

    public Home() {
    }

    public Home(String title, String description, float price, User seller, HomeType type) {
        super(title, description, price, seller);
        this.homeType = type;
    }
    
    public HomeType getHomeType() {
        return homeType;
    }
    public void setHomeType(HomeType type) {
        this.homeType = type;
    }

    @Override
    public ItemDTO toDTO() {
        return new HomeDTO(getId(), getTitle(), getDescription(), getPrice(), getHomeType(), getImages());
    }
}
