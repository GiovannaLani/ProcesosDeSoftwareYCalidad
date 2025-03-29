package com.spq.client.data;

public class Pet extends Item {
    public Species species;
    public Pet(String title, String description, float price, Species species) {
        super(title, description, price);
        this.species = species;
    }
    public Species getSpecies() {
        return species;
    }
    public void setSpecies(Species species) {
        this.species = species;
    }
}
