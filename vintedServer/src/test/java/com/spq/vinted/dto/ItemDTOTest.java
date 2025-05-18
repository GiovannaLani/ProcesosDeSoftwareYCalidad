package com.spq.vinted.dto;

import org.junit.jupiter.api.Test;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

public class ItemDTOTest {

    // Clase concreta para probar la clase abstracta ItemDTO
    private static class TestItemDTO extends ItemDTO {
        public TestItemDTO() {
            super();
        }

        public TestItemDTO(long id, String title, String description, float price, List<String> images) {
            super(id, title, description, price, images,false);
        }
    }

    @Test
    public void testNoArgsConstructor() {
        TestItemDTO itemDTO = new TestItemDTO();
        
        assertEquals(0L, itemDTO.getId());
        assertNull(itemDTO.getTitle());
        assertNull(itemDTO.getDescription());
        assertEquals(0.0f, itemDTO.getPrice());
        assertEquals(0L, itemDTO.getSellerId());
        assertNotNull(itemDTO.getImages());
        assertTrue(itemDTO.getImages().isEmpty());
    }

    @Test
    public void testAllArgsConstructor() {
        long id = 1L;
        String title = "Test Item";
        String description = "Test Description";
        float price = 99.99f;
        List<String> images = Arrays.asList("image1.jpg", "image2.jpg");

        TestItemDTO itemDTO = new TestItemDTO(id, title, description, price, images);

        assertEquals(id, itemDTO.getId());
        assertEquals(title, itemDTO.getTitle());
        assertEquals(description, itemDTO.getDescription());
        assertEquals(price, itemDTO.getPrice());
        assertEquals(images, itemDTO.getImages());
        assertEquals(0L, itemDTO.getSellerId()); // No se establece en el constructor
    }

    @Test
    public void testSettersAndGetters() {
        TestItemDTO itemDTO = new TestItemDTO();

        long id = 2L;
        String title = "Updated Item";
        String description = "Updated Description";
        float price = 149.99f;
        long sellerId = 100L;
        List<String> images = Arrays.asList("updated1.jpg", "updated2.jpg");

        itemDTO.setId(id);
        itemDTO.setTitle(title);
        itemDTO.setDescription(description);
        itemDTO.setPrice(price);
        itemDTO.setSellerId(sellerId);
        itemDTO.setImages(images);

        assertEquals(id, itemDTO.getId());
        assertEquals(title, itemDTO.getTitle());
        assertEquals(description, itemDTO.getDescription());
        assertEquals(price, itemDTO.getPrice());
        assertEquals(sellerId, itemDTO.getSellerId());
        assertEquals(images, itemDTO.getImages());
    }

    @Test
    public void testImagesListInitialization() {
        TestItemDTO itemDTO = new TestItemDTO();
        List<String> images = itemDTO.getImages();
        
        assertNotNull(images);
        assertTrue(images instanceof ArrayList);
        assertTrue(images.isEmpty());
    }

    @Test
    public void testImagesListModification() {
        TestItemDTO itemDTO = new TestItemDTO();
        List<String> images = itemDTO.getImages();
        
        images.add("newImage.jpg");
        assertEquals(1, itemDTO.getImages().size());
        assertEquals("newImage.jpg", itemDTO.getImages().get(0));
    }

    @Test
    public void testSetImagesWithNull() {
        TestItemDTO itemDTO = new TestItemDTO();
        itemDTO.setImages(null);
        
        assertNull(itemDTO.getImages());
    }
}