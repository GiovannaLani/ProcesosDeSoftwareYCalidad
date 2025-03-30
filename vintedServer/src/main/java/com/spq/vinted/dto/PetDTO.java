package com.spq.vinted.dto;

import java.util.List;

import com.spq.vinted.model.Species;

public class PetDTO extends ItemDTO {

    private Species species;

    public PetDTO() {
    }

    public PetDTO(long id, String title, String description, float price, Species species,List<String> images) {
        super(id, title, description, price, images);
        this.species = species;
    }
    public Species getSpecies() {
        return species;
    }
    
    public void setSpecies(Species species) {
        this.species = species;
    }
}
