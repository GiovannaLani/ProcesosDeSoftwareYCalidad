package com.spq.vinted.dto;

import java.util.List;

import com.spq.vinted.model.EntertainmentType;

public class EntertainmentDTO extends ItemDTO {
    public EntertainmentType entertainmentType;
    public EntertainmentDTO() {
    }

    public EntertainmentDTO(long id, String title, String description, float price, EntertainmentType type,List<String> images) {
        super(id, title, description, price,images);
        this.entertainmentType = type;
    }
    public EntertainmentType getEntertainmentType() {
        return entertainmentType;
    }
    public void setEntertainmentType(EntertainmentType type) {
        this.entertainmentType = type;
    }
    
}
