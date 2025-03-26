package com.spq.vinted.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;

@Entity
@Table(name = "home")
public class Home extends Item {

    public Home() {
    }

    public Home(long id, String title, String description, float price, String image, User seller) {
        super(id, title, description, price, image, seller);
    }
    
}
