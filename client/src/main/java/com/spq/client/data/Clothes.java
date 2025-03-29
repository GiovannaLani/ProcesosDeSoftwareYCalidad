package com.spq.client.data;

public class Clothes extends Item {
    private ClothesSize size;
    private Category category;
    private ClothesType type;

    public Clothes(String title, String description, float price, ClothesSize size, Category category, ClothesType type) {
        super(title, description, price); 
        this.size = size;
        this.type = type;
        this.category = category;
    }

    public ClothesSize getSize() {
        return size;
    }

    public void setSize(ClothesSize size) {
        this.size = size;
    }

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }
    public ClothesType getType() {
        return type;
    }
    public void setType(ClothesType type) {
        this.type = type;
    }
}