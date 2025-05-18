package com.spq.vinted.dto;

import com.spq.vinted.model.EntertainmentType;
import org.junit.jupiter.api.Test;
import java.util.Arrays;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

public class EntertainmentDTOTest {

    @Test
    public void testNoArgsConstructor() {
        EntertainmentDTO entertainmentDTO = new EntertainmentDTO();
        
        assertNull(entertainmentDTO.getEntertainmentType());
        // Heredados de ItemDTO
        assertEquals(0L, entertainmentDTO.getId());
        assertNull(entertainmentDTO.getTitle());
        assertNull(entertainmentDTO.getDescription());
        assertEquals(0.0f, entertainmentDTO.getPrice());
        assertNotNull(entertainmentDTO.getImages());
        assertTrue(entertainmentDTO.getImages().isEmpty());
    }

    @Test
    public void testAllArgsConstructor() {
        long id = 1L;
        String title = "Video Game";
        String description = "Action adventure game";
        float price = 59.99f;
        EntertainmentType type = EntertainmentType.GAME;
        List<String> images = Arrays.asList("game1.jpg", "game2.jpg");

        EntertainmentDTO entertainmentDTO = new EntertainmentDTO(id, title, description, price, type, images, false);

        assertEquals(id, entertainmentDTO.getId());
        assertEquals(title, entertainmentDTO.getTitle());
        assertEquals(description, entertainmentDTO.getDescription());
        assertEquals(price, entertainmentDTO.getPrice());
        assertEquals(type, entertainmentDTO.getEntertainmentType());
        assertEquals(images, entertainmentDTO.getImages());
    }

    @Test
    public void testSettersAndGetters() {
        EntertainmentDTO entertainmentDTO = new EntertainmentDTO();
        EntertainmentType type = EntertainmentType.BOOK;

        entertainmentDTO.setEntertainmentType(type);
        assertEquals(type, entertainmentDTO.getEntertainmentType());
    }

    @Test
    public void testInheritedItemDTOMethods() {
        EntertainmentDTO entertainmentDTO = new EntertainmentDTO();

        long id = 2L;
        String title = "Book";
        String description = "Science fiction novel";
        float price = 19.99f;
        long sellerId = 200L;
        List<String> images = Arrays.asList("book1.jpg", "book2.jpg");

        entertainmentDTO.setId(id);
        entertainmentDTO.setTitle(title);
        entertainmentDTO.setDescription(description);
        entertainmentDTO.setPrice(price);
        entertainmentDTO.setSellerId(sellerId);
        entertainmentDTO.setImages(images);

        assertEquals(id, entertainmentDTO.getId());
        assertEquals(title, entertainmentDTO.getTitle());
        assertEquals(description, entertainmentDTO.getDescription());
        assertEquals(price, entertainmentDTO.getPrice());
        assertEquals(sellerId, entertainmentDTO.getSellerId());
        assertEquals(images, entertainmentDTO.getImages());
    }
}