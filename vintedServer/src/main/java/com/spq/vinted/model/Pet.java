package com.spq.vinted.model;

import com.spq.vinted.dto.PetDTO;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

@Entity
@Table(name = "pets")
public class Pet extends Item{
    @Column(nullable = false)
    private String species;

    public Pet() {
    }

    public Pet(long id, String title, String description, float price, String image, String species, User seller) {
        super(id, title, description, price, image, seller);
        this.species = species;
    }

    public String getSpecies() {
        return species;
    }
    
    public void setSpecies(String species) {
        this.species = species;
    }

    public PetDTO toDTO() {
        return new PetDTO(getId(), getTitle(), getDescription(), getPrice(), getImage(), species);
    }
    
}
