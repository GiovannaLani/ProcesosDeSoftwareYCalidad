package com.spq.client.data;

public class Home extends Item {
    public HomeType type;
    public Home(String title, String description, float price, HomeType type) {
        super(title, description, price);
        this.type = type;
    }
    public HomeType getType() {
        return type;
    }
    public void setType(HomeType type) {
        this.type = type;
    }
}
