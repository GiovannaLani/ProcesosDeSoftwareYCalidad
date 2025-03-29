package com.spq.vinted.model;

import com.spq.vinted.dto.ItemDTO;
import com.spq.vinted.dto.PetDTO;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

@Entity
@Table(name = "pets")
public class Pet extends Item{
    @Column(nullable = false)
    private Species species;

    public Pet() {
    }

    public Pet(String title, String description, float price, Species species, User seller) {
        super(title, description, price, seller);
        this.species = species;
    }

    public Species getSpecies() {
        return species;
    }
    
    public void setSpecies(Species species) {
        this.species = species;
    }

    @Override
    public ItemDTO toDTO() {
        return new PetDTO(getId(), getTitle(), getDescription(), getPrice(), species, getImages());
    }
    
}
