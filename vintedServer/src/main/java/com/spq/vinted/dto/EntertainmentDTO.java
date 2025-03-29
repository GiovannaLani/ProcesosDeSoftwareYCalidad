package com.spq.vinted.dto;

import java.util.List;

import com.spq.vinted.model.EntertainmentType;

public class EntertainmentDTO extends ItemDTO {
    public EntertainmentType entreainmentType;
    public EntertainmentDTO() {
    }

    public EntertainmentDTO(long id, String title, String description, float price, EntertainmentType type,List<String> images) {
        super(id, title, description, price,images);
        this.entreainmentType = type;
    }
    public EntertainmentType getEntreainmentType() {
        return entreainmentType;
    }
    public void setEntreainmentType(EntertainmentType type) {
        this.entreainmentType = type;
    }
    
}
