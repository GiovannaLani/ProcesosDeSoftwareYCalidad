package com.spq.vinted.dto;

public class PetDTO extends ItemDTO {

    private String species;

    public PetDTO() {
    }

    public PetDTO(long id, String title, String description, float price, String image, String species) {
        super(id, title, description, price, image);
        this.species = species;
    }
    public String getSpecies() {
        return species;
    }
    
    public void setSpecies(String species) {
        this.species = species;
    }
}
