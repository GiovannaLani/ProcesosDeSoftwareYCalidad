package com.spq.client.data;

import com.fasterxml.jackson.annotation.JsonTypeName;

@JsonTypeName("pet")
public class Pet extends Item {
    public Species species;
    public Pet(String title, String description, float price, Species species, boolean isSold) {
        super(title, description, price, isSold);
        this.species = species;
    }
    public Species getSpecies() {
        return species;
    }
    public void setSpecies(Species species) {
        this.species = species;
    }
}
