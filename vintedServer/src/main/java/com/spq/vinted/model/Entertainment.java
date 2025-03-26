package com.spq.vinted.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;

@Entity
@Table(name = "entertainment")
public class Entertainment extends Item {
    
    public Entertainment() {
    }
    
    public Entertainment(long id, String title, String description, float price, String image, User seller) {
        super(id, title, description, price, image, seller);
    }



}