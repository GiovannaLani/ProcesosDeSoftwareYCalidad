package com.spq.client.data;

import com.fasterxml.jackson.annotation.JsonTypeName;

@JsonTypeName("home")
public class Home extends Item {
    public HomeType homeType;
    public Home(String title, String description, float price, HomeType type, boolean isSold) {
        super(title, description, price, isSold);
        this.homeType = type;
    }
    public HomeType getHomeType() {
        return homeType;
    }
    public void setHomeType(HomeType type) {
        this.homeType = type;
    }
}
