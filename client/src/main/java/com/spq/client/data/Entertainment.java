package com.spq.client.data;

public class Entertainment extends Item {
    public EntertainmentType entertainmentType;
    public Entertainment(String title, String description, float price, EntertainmentType entertainmentType) {
        super(title, description, price);
        this.entertainmentType = entertainmentType;
    }
    public EntertainmentType getEntertainmentType() {
        return entertainmentType;
    }
    public void setEntertainmentType(EntertainmentType entertainmentType) {
        this.entertainmentType = entertainmentType;
    }
}
