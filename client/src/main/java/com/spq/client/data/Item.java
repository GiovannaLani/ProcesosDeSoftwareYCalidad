package com.spq.client.data;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonSubTypes;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "type")
@JsonSubTypes({
    @JsonSubTypes.Type(value = Clothes.class, name = "clothes"),
    @JsonSubTypes.Type(value = Electronics.class, name = "electronics"),
    @JsonSubTypes.Type(value = Pet.class, name = "pet"),
    @JsonSubTypes.Type(value = Entertainment.class, name = "entertainment"),
    @JsonSubTypes.Type(value = Home.class, name = "home")
})
public abstract class Item {
    private String title;
    private String description;
    private float price;
    Item(String title, String description, float price) {
        this.title = title;
        this.description = description;
        this.price = price;
    }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public float getPrice() { return price; }
    public void setPrice(float price) { this.price = price; }
}

