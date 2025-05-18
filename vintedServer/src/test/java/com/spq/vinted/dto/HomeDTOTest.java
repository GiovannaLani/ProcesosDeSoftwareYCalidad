package com.spq.vinted.dto;

import com.spq.vinted.model.HomeType;
import org.junit.jupiter.api.Test;
import java.util.Arrays;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

public class HomeDTOTest {

    @Test
    public void testNoArgsConstructor() {
        HomeDTO homeDTO = new HomeDTO();
        
        assertNull(homeDTO.getHomeType());
        // Heredados de ItemDTO
        assertEquals(0L, homeDTO.getId());
        assertNull(homeDTO.getTitle());
        assertNull(homeDTO.getDescription());
        assertEquals(0.0f, homeDTO.getPrice());
        assertNotNull(homeDTO.getImages());
        assertTrue(homeDTO.getImages().isEmpty());
    }

    @Test
    public void testAllArgsConstructor() {
        long id = 1L;
        String title = "Sofa";
        String description = "Comfortable 3-seater sofa";
        float price = 299.99f;
        HomeType type = HomeType.FURNITURE;
        List<String> images = Arrays.asList("sofa1.jpg", "sofa2.jpg");

        HomeDTO homeDTO = new HomeDTO(id, title, description, price, type, images, false);

        assertEquals(id, homeDTO.getId());
        assertEquals(title, homeDTO.getTitle());
        assertEquals(description, homeDTO.getDescription());
        assertEquals(price, homeDTO.getPrice());
        assertEquals(type, homeDTO.getHomeType());
        assertEquals(images, homeDTO.getImages());
    }

    @Test
    public void testSettersAndGetters() {
        HomeDTO homeDTO = new HomeDTO();
        HomeType type = HomeType.DECORATION;

        homeDTO.setHomeType(type);
        assertEquals(type, homeDTO.getHomeType());
    }

    @Test
    public void testInheritedItemDTOMethods() {
        HomeDTO homeDTO = new HomeDTO();

        long id = 2L;
        String title = "Lamp";
        String description = "Modern table lamp";
        float price = 39.99f;
        long sellerId = 300L;
        List<String> images = Arrays.asList("lamp1.jpg", "lamp2.jpg");

        homeDTO.setId(id);
        homeDTO.setTitle(title);
        homeDTO.setDescription(description);
        homeDTO.setPrice(price);
        homeDTO.setSellerId(sellerId);
        homeDTO.setImages(images);

        assertEquals(id, homeDTO.getId());
        assertEquals(title, homeDTO.getTitle());
        assertEquals(description, homeDTO.getDescription());
        assertEquals(price, homeDTO.getPrice());
        assertEquals(sellerId, homeDTO.getSellerId());
        assertEquals(images, homeDTO.getImages());
    }
}