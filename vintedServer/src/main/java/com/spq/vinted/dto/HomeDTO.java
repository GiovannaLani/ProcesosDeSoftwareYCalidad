package com.spq.vinted.dto;

import java.util.List;

import com.spq.vinted.model.HomeType;

public class HomeDTO extends ItemDTO {
    public HomeType homeType;
    public HomeDTO() {
    }

    public HomeDTO(long id, String title, String description, float price, HomeType type, List<String> images) {
        super(id, title, description, price,images);
        this.homeType = type;
    }
    public HomeType getHomeType() {
        return homeType;
    }
    public void setHomeType(HomeType type) {
        this.homeType = type;
    }
}
