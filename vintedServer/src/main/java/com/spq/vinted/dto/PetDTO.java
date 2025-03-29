package com.spq.vinted.dto;

import com.spq.vinted.model.Species;

public class PetDTO extends ItemDTO {

    private Species species;

    public PetDTO() {
    }

    public PetDTO(long id, String title, String description, float price, Species species) {
        super(id, title, description, price);
        this.species = species;
    }
    public Species getSpecies() {
        return species;
    }
    
    public void setSpecies(Species species) {
        this.species = species;
    }
}
