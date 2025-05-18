package com.spq.vinted.dto;

import com.spq.vinted.model.Species;
import org.junit.jupiter.api.Test;
import java.util.Arrays;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

public class PetDTOTest {

    @Test
    public void testNoArgsConstructor() {
        PetDTO petDTO = new PetDTO();
        
        assertNull(petDTO.getSpecies());
        // Heredados de ItemDTO
        assertEquals(0L, petDTO.getId());
        assertNull(petDTO.getTitle());
        assertNull(petDTO.getDescription());
        assertEquals(0.0f, petDTO.getPrice());
        assertNotNull(petDTO.getImages());
        assertTrue(petDTO.getImages().isEmpty());
    }

    @Test
    public void testAllArgsConstructor() {
        long id = 1L;
        String title = "Cute Dog";
        String description = "Friendly golden retriever";
        float price = 200.0f;
        Species species = Species.DOG;
        List<String> images = Arrays.asList("dog1.jpg", "dog2.jpg");

        PetDTO petDTO = new PetDTO(id, title, description, price, species, images, false);

        assertEquals(id, petDTO.getId());
        assertEquals(title, petDTO.getTitle());
        assertEquals(description, petDTO.getDescription());
        assertEquals(price, petDTO.getPrice());
        assertEquals(species, petDTO.getSpecies());
        assertEquals(images, petDTO.getImages());
    }

    @Test
    public void testSettersAndGetters() {
        PetDTO petDTO = new PetDTO();
        Species species = Species.CAT;

        petDTO.setSpecies(species);
        assertEquals(species, petDTO.getSpecies());
    }

    @Test
    public void testInheritedItemDTOMethods() {
        PetDTO petDTO = new PetDTO();

        long id = 2L;
        String title = "Parrot";
        String description = "Green talking parrot";
        float price = 150.0f;
        long sellerId = 500L;
        List<String> images = Arrays.asList("parrot1.jpg", "parrot2.jpg");

        petDTO.setId(id);
        petDTO.setTitle(title);
        petDTO.setDescription(description);
        petDTO.setPrice(price);
        petDTO.setSellerId(sellerId);
        petDTO.setImages(images);

        assertEquals(id, petDTO.getId());
        assertEquals(title, petDTO.getTitle());
        assertEquals(description, petDTO.getDescription());
        assertEquals(price, petDTO.getPrice());
        assertEquals(sellerId, petDTO.getSellerId());
        assertEquals(images, petDTO.getImages());
    }
}