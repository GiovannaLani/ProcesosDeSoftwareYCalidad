package com.spq.vinted.dto;

import com.spq.vinted.model.Category;

public class ClothesDTO extends ItemDTO {
    
    private String size;
    private String brand;
    private Category category;

    public ClothesDTO() {

    }

    public ClothesDTO(long id, String title, String description, float price, String image, String size, String brand, Category category) {
        super(id, title, description, price, image); 
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