package com.spq.vinted.model;

import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.spq.vinted.dto.HomeDTO;

class HomeTest {

    private Home home;
    private User seller;

    @BeforeEach
    void setUp() {
        seller = new User("seller@example.com", "password123", "sellerUser", "Seller", "User");
        home = new Home(
            "Test Home",
            "This is a test home item",
            300.0f,
            seller,
            HomeType.FURNITURE
        );
    }

    @Test
    void testConstructor() {
        assertEquals("Test Home", home.getTitle(), "El título debería coincidir");
        assertEquals("This is a test home item", home.getDescription(), "La descripción debería coincidir");
        assertEquals(300.0f, home.getPrice(), "El precio debería coincidir");
        assertEquals(HomeType.FURNITURE, home.getHomeType(), "El tipo de hogar debería coincidir");
        assertEquals(seller, home.getSeller(), "El vendedor debería coincidir");
        assertNotNull(home.getImages(), "La lista de imágenes no debería ser nula");
        assertTrue(home.getImages().isEmpty(), "La lista de imágenes debería estar vacía inicialmente");
    }

    @Test
    void testSettersAndGetters() {
        home.setTitle("New Title");
        home.setDescription("New Description");
        home.setPrice(500.0f);
        home.setHomeType(HomeType.DECORATION);

        List<String> images = new ArrayList<>();
        images.add("image1.jpg");
        images.add("image2.jpg");
        home.setImages(images);

        User newSeller = new User("newSeller@example.com", "password456", "newSellerUser", "NewSeller", "User");
        home.setSeller(newSeller);

        assertEquals("New Title", home.getTitle(), "El nuevo título debería coincidir");
        assertEquals("New Description", home.getDescription(), "La nueva descripción debería coincidir");
        assertEquals(500.0f, home.getPrice(), "El nuevo precio debería coincidir");
        assertEquals(HomeType.DECORATION, home.getHomeType(), "El nuevo tipo de hogar debería coincidir");
        assertEquals(2, home.getImages().size(), "La lista de imágenes debería contener 2 elementos");
        assertEquals("image1.jpg", home.getImages().get(0), "La primera imagen debería coincidir");
        assertEquals("image2.jpg", home.getImages().get(1), "La segunda imagen debería coincidir");
        assertEquals(newSeller, home.getSeller(), "El nuevo vendedor debería coincidir");
    }

    @Test
    void testToDTO() {
        List<String> images = new ArrayList<>();
        images.add("image1.jpg");
        images.add("image2.jpg");
        home.setImages(images);

        HomeDTO homeDTO = (HomeDTO) home.toDTO();

        assertEquals(home.getId(), homeDTO.getId(), "El ID debería coincidir");
        assertEquals(home.getTitle(), homeDTO.getTitle(), "El título debería coincidir");
        assertEquals(home.getDescription(), homeDTO.getDescription(), "La descripción debería coincidir");
        assertEquals(home.getPrice(), homeDTO.getPrice(), "El precio debería coincidir");
        assertEquals(home.getHomeType(), homeDTO.getHomeType(), "El tipo de hogar debería coincidir");
        assertEquals(home.getImages(), homeDTO.getImages(), "La lista de imágenes debería coincidir");
    }
}