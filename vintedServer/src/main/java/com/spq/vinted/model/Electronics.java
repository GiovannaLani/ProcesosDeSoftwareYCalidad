package com.spq.vinted.model;

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
    
}
