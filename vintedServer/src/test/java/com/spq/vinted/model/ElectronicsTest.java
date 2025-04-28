package com.spq.vinted.model;

import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.spq.vinted.dto.ElectronicsDTO;

class ElectronicsTest {

    private Electronics electronics;
    private User seller;

    @BeforeEach
    void setUp() {
        seller = new User("seller@example.com", "password123", "sellerUser", "Seller", "User");
        electronics = new Electronics(
            "Test Electronics",
            "This is a test electronics item",
            500.0f,
            seller,
            ElectronicsType.DEVICE
        );
    }

    @Test
    void testConstructor() {
        assertEquals("Test Electronics", electronics.getTitle(), "El título debería coincidir");
        assertEquals("This is a test electronics item", electronics.getDescription(), "La descripción debería coincidir");
        assertEquals(500.0f, electronics.getPrice(), "El precio debería coincidir");
        assertEquals(ElectronicsType.DEVICE, electronics.getElectronicsType(), "El tipo de electrónica debería coincidir");
        assertEquals(seller, electronics.getSeller(), "El vendedor debería coincidir");
        assertNotNull(electronics.getImages(), "La lista de imágenes no debería ser nula");
        assertTrue(electronics.getImages().isEmpty(), "La lista de imágenes debería estar vacía inicialmente");
    }

    @Test
    void testSettersAndGetters() {
        electronics.setTitle("New Title");
        electronics.setDescription("New Description");
        electronics.setPrice(1000.0f);
        electronics.setElectronicsType(ElectronicsType.VIDEOGAME);

        List<String> images = new ArrayList<>();
        images.add("image1.jpg");
        images.add("image2.jpg");
        electronics.setImages(images);

        User newSeller = new User("newSeller@example.com", "password456", "newSellerUser", "NewSeller", "User");
        electronics.setSeller(newSeller);

        assertEquals("New Title", electronics.getTitle(), "El nuevo título debería coincidir");
        assertEquals("New Description", electronics.getDescription(), "La nueva descripción debería coincidir");
        assertEquals(1000.0f, electronics.getPrice(), "El nuevo precio debería coincidir");
        assertEquals(ElectronicsType.VIDEOGAME, electronics.getElectronicsType(), "El nuevo tipo de electrónica debería coincidir");
        assertEquals(2, electronics.getImages().size(), "La lista de imágenes debería contener 2 elementos");
        assertEquals("image1.jpg", electronics.getImages().get(0), "La primera imagen debería coincidir");
        assertEquals("image2.jpg", electronics.getImages().get(1), "La segunda imagen debería coincidir");
        assertEquals(newSeller, electronics.getSeller(), "El nuevo vendedor debería coincidir");
    }

    @Test
    void testToDTO() {
        List<String> images = new ArrayList<>();
        images.add("image1.jpg");
        images.add("image2.jpg");
        electronics.setImages(images);

        ElectronicsDTO electronicsDTO = (ElectronicsDTO) electronics.toDTO();

        assertEquals(electronics.getId(), electronicsDTO.getId(), "El ID debería coincidir");
        assertEquals(electronics.getTitle(), electronicsDTO.getTitle(), "El título debería coincidir");
        assertEquals(electronics.getDescription(), electronicsDTO.getDescription(), "La descripción debería coincidir");
        assertEquals(electronics.getPrice(), electronicsDTO.getPrice(), "El precio debería coincidir");
        assertEquals(electronics.getElectronicsType(), electronicsDTO.getElectronicsType(), "El tipo de electrónica debería coincidir");
        assertEquals(electronics.getImages(), electronicsDTO.getImages(), "La lista de imágenes debería coincidir");
    }
}