package com.spq.vinted.model;

import com.spq.vinted.dto.ClothesDTO;
import com.spq.vinted.dto.ItemDTO;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

@Entity
@Table(name = "clothes")
public class Clothes extends Item {
    
    @Column(nullable = false)
    private ClothesSize size;
    @Column(nullable = false)
    private ClothesType clothesType;
    @Column(nullable = false)
    private Category category;

    public Clothes() {
    }

    public Clothes(String title, String description, float price, ClothesSize size, ClothesType type, Category category, User seller) {
        super(title, description, price, seller);
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

    @Override
    public ItemDTO toDTO() {
        return new ClothesDTO(getId(), getTitle(), getDescription(), getPrice(), getSize(), getClothesType(), getCategory(), getImages());
    }
}