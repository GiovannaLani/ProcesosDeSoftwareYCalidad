package com.spq.vinted.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

@Entity
@Table(name = "clothes")
public class Clothes extends Item {
    private String size;
    private String brand;

    @Column(nullable = false)
    private Category category;

    public Clothes() {
    }

    public Clothes(long id, String title, String description, float price, String image, String size, String brand, Category category, User seller) {
        super(id, title, description, price, image, seller);
        this.size = size;
        this.brand = brand;
        this.category = category;
    }

    public String getSize() {
        return size;
    }

    public void setSize(String size) {
        this.size = size;
    }

    public String getBrand() {
        return brand;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }
}