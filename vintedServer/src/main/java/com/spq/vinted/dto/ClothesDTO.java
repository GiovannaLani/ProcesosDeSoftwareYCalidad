package com.spq.vinted.dto;

import java.util.List;

import com.spq.vinted.model.Category;
import com.spq.vinted.model.ClothesSize;
import com.spq.vinted.model.ClothesType;

public class ClothesDTO extends ItemDTO {
    
    private ClothesSize size;
    private ClothesType clothesType;
    private Category category;

    public ClothesDTO() {

    }

    public ClothesDTO(long id, String title, String description, float price, ClothesSize size, ClothesType type, Category category,List<String> images) {
        super(id, title, description, price,images); 
        this.size = size;
        this.clothesType = type;
        this.category = category;
    }

    public ClothesSize getSize() {
        return size;
    }

    public void setSize(ClothesSize size) {
        this.size = size;
    }

    public ClothesType getClothesType() {
        return clothesType;
    }
    
    public void setClothesType(ClothesType type) {
        this.clothesType = type;
    }

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }
}