package com.spq.vinted.dto;

import com.fasterxml.jackson.annotation.JsonTypeInfo;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonSubTypes;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "type")
@JsonSubTypes({
    @JsonSubTypes.Type(value = ClothesDTO.class, name = "clothes"),
    @JsonSubTypes.Type(value = ElectronicsDTO.class, name = "electronics"),
    @JsonSubTypes.Type(value = PetDTO.class, name = "pet"),
    @JsonSubTypes.Type(value = EntertainmentDTO.class, name = "entertainment"),
    @JsonSubTypes.Type(value = HomeDTO.class, name = "home")
})
public abstract class ItemDTO {

    private long id;
    private String title;
    private String description;
    private float price;
    private long sellerId;
    private List<String> images = new ArrayList<>();
    
    public ItemDTO() {
    }

    public ItemDTO(long id, String title, String description, float price, List<String> images) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.price = price;
        this.images = images;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public float getPrice() {
        return price;
    }

    public void setPrice(float price) {
        this.price = price;
    }

    public List<String> getImages(){
        return images;
    }
    public void setImages(List<String> images){
        this.images = images;
    }

    public long getSellerId() {
        return sellerId;
    }

    public void setSellerId(long sellerId) {
        this.sellerId = sellerId;
    }
}


