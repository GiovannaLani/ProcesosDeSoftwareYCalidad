package com.spq.vinted.model;

import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.spq.vinted.dto.PetDTO;

class PetTest {

    private Pet pet;
    private User seller;

    @BeforeEach
    void setUp() {
        seller = new User("seller@example.com", "password123", "sellerUser", "Seller", "User");
        pet = new Pet(
            "Test Pet",
            "This is a test pet",
            100.0f,
            Species.DOG,
            seller
        );
    }

    @Test
    void testConstructor() {
        assertEquals("Test Pet", pet.getTitle(), "El título debería coincidir");
        assertEquals("This is a test pet", pet.getDescription(), "La descripción debería coincidir");
        assertEquals(100.0f, pet.getPrice(), "El precio debería coincidir");
        assertEquals(Species.DOG, pet.getSpecies(), "La especie debería coincidir");
        assertEquals(seller, pet.getSeller(), "El vendedor debería coincidir");
        assertNotNull(pet.getImages(), "La lista de imágenes no debería ser nula");
        assertTrue(pet.getImages().isEmpty(), "La lista de imágenes debería estar vacía inicialmente");
    }

    @Test
    void testSettersAndGetters() {
        pet.setTitle("New Title");
        pet.setDescription("New Description");
        pet.setPrice(200.0f);
        pet.setSpecies(Species.CAT);

        List<String> images = new ArrayList<>();
        images.add("image1.jpg");
        images.add("image2.jpg");
        pet.setImages(images);

        User newSeller = new User("newSeller@example.com", "password456", "newSellerUser", "NewSeller", "User");
        pet.setSeller(newSeller);

        assertEquals("New Title", pet.getTitle(), "El nuevo título debería coincidir");
        assertEquals("New Description", pet.getDescription(), "La nueva descripción debería coincidir");
        assertEquals(200.0f, pet.getPrice(), "El nuevo precio debería coincidir");
        assertEquals(Species.CAT, pet.getSpecies(), "La nueva especie debería coincidir");
        assertEquals(2, pet.getImages().size(), "La lista de imágenes debería contener 2 elementos");
        assertEquals("image1.jpg", pet.getImages().get(0), "La primera imagen debería coincidir");
        assertEquals("image2.jpg", pet.getImages().get(1), "La segunda imagen debería coincidir");
        assertEquals(newSeller, pet.getSeller(), "El nuevo vendedor debería coincidir");
    }

    @Test
    void testToDTO() {
        List<String> images = new ArrayList<>();
        images.add("image1.jpg");
        images.add("image2.jpg");
        pet.setImages(images);

        PetDTO petDTO = (PetDTO) pet.toDTO();

        assertEquals(pet.getId(), petDTO.getId(), "El ID debería coincidir");
        assertEquals(pet.getTitle(), petDTO.getTitle(), "El título debería coincidir");
        assertEquals(pet.getDescription(), petDTO.getDescription(), "La descripción debería coincidir");
        assertEquals(pet.getPrice(), petDTO.getPrice(), "El precio debería coincidir");
        assertEquals(pet.getSpecies(), petDTO.getSpecies(), "La especie debería coincidir");
        assertEquals(pet.getImages(), petDTO.getImages(), "La lista de imágenes debería coincidir");
    }
}