package com.spq.vinted.dto;

import java.util.List;

import com.spq.vinted.model.EntertainmentType;

public class EntertainmentDTO extends ItemDTO {
    public EntertainmentType type;
    public EntertainmentDTO() {
    }

    public EntertainmentDTO(long id, String title, String description, float price, EntertainmentType type,List<String> images) {
        super(id, title, description, price,images);
        this.type = type;
    }
    public EntertainmentType getType() {
        return type;
    }
    public void setType(EntertainmentType type) {
        this.type = type;
    }
    
}
