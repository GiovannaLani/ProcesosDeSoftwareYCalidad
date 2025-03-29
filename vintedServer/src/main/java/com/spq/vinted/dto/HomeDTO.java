package com.spq.vinted.dto;

import java.util.List;

import com.spq.vinted.model.HomeType;

public class HomeDTO extends ItemDTO {
    public HomeType type;
    public HomeDTO() {
    }

    public HomeDTO(long id, String title, String description, float price, HomeType type, List<String> images) {
        super(id, title, description, price,images);
        this.type = type;
    }
    public HomeType getType() {
        return type;
    }
    public void setType(HomeType type) {
        this.type = type;
    }
}
