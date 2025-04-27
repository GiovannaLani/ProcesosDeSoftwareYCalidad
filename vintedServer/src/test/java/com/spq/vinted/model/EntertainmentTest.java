package com.spq.vinted.model;

import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.spq.vinted.dto.EntertainmentDTO;

class EntertainmentTest {

    private Entertainment entertainment;
    private User seller;

    @BeforeEach
    void setUp() {
        seller = new User("seller@example.com", "password123", "sellerUser", "Seller", "User");
        entertainment = new Entertainment(
            "Test Entertainment",
            "This is a test entertainment item",
            150.0f,
            seller,
            EntertainmentType.BOOK
        );
    }

    @Test
    void testConstructor() {
        assertEquals("Test Entertainment", entertainment.getTitle(), "El título debería coincidir");
        assertEquals("This is a test entertainment item", entertainment.getDescription(), "La descripción debería coincidir");
        assertEquals(150.0f, entertainment.getPrice(), "El precio debería coincidir");
        assertEquals(EntertainmentType.BOOK, entertainment.getEntertainmentType(), "El tipo de entretenimiento debería coincidir");
        assertEquals(seller, entertainment.getSeller(), "El vendedor debería coincidir");
        assertNotNull(entertainment.getImages(), "La lista de imágenes no debería ser nula");
        assertTrue(entertainment.getImages().isEmpty(), "La lista de imágenes debería estar vacía inicialmente");
    }

    @Test
    void testSettersAndGetters() {
        entertainment.setTitle("New Title");
        entertainment.setDescription("New Description");
        entertainment.setPrice(200.0f);
        entertainment.setEntertainmentType(EntertainmentType.GAME);

        List<String> images = new ArrayList<>();
        images.add("image1.jpg");
        images.add("image2.jpg");
        entertainment.setImages(images);

        User newSeller = new User("newSeller@example.com", "password456", "newSellerUser", "NewSeller", "User");
        entertainment.setSeller(newSeller);

        assertEquals("New Title", entertainment.getTitle(), "El nuevo título debería coincidir");
        assertEquals("New Description", entertainment.getDescription(), "La nueva descripción debería coincidir");
        assertEquals(200.0f, entertainment.getPrice(), "El nuevo precio debería coincidir");
        assertEquals(EntertainmentType.GAME, entertainment.getEntertainmentType(), "El nuevo tipo de entretenimiento debería coincidir");
        assertEquals(2, entertainment.getImages().size(), "La lista de imágenes debería contener 2 elementos");
        assertEquals("image1.jpg", entertainment.getImages().get(0), "La primera imagen debería coincidir");
        assertEquals("image2.jpg", entertainment.getImages().get(1), "La segunda imagen debería coincidir");
        assertEquals(newSeller, entertainment.getSeller(), "El nuevo vendedor debería coincidir");
    }

    @Test
    void testToDTO() {
        List<String> images = new ArrayList<>();
        images.add("image1.jpg");
        images.add("image2.jpg");
        entertainment.setImages(images);

        EntertainmentDTO entertainmentDTO = (EntertainmentDTO) entertainment.toDTO();

        assertEquals(entertainment.getId(), entertainmentDTO.getId(), "El ID debería coincidir");
        assertEquals(entertainment.getTitle(), entertainmentDTO.getTitle(), "El título debería coincidir");
        assertEquals(entertainment.getDescription(), entertainmentDTO.getDescription(), "La descripción debería coincidir");
        assertEquals(entertainment.getPrice(), entertainmentDTO.getPrice(), "El precio debería coincidir");
        assertEquals(entertainment.getEntertainmentType(), entertainmentDTO.getEntertainmentType(), "El tipo de entretenimiento debería coincidir");
        assertEquals(entertainment.getImages(), entertainmentDTO.getImages(), "La lista de imágenes debería coincidir");
    }
}