package com.spq.vinted.dto;

import com.spq.vinted.model.ElectronicsType;
import org.junit.jupiter.api.Test;
import java.util.Arrays;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

public class ElectronicsDTOTest {

    @Test
    public void testNoArgsConstructor() {
        ElectronicsDTO electronicsDTO = new ElectronicsDTO();
        
        assertNull(electronicsDTO.getElectronicsType());
        // Heredados de ItemDTO
        assertEquals(0L, electronicsDTO.getId());
        assertNull(electronicsDTO.getTitle());
        assertNull(electronicsDTO.getDescription());
        assertEquals(0.0f, electronicsDTO.getPrice());
        assertNotNull(electronicsDTO.getImages());
        assertTrue(electronicsDTO.getImages().isEmpty());
    }

    @Test
    public void testAllArgsConstructor() {
        long id = 1L;
        String title = "Smartphone";
        String description = "Latest model smartphone";
        float price = 499.99f;
        ElectronicsType type = ElectronicsType.DEVICE;
        List<String> images = Arrays.asList("phone1.jpg", "phone2.jpg");

        ElectronicsDTO electronicsDTO = new ElectronicsDTO(id, title, description, price, type, images,false);

        assertEquals(id, electronicsDTO.getId());
        assertEquals(title, electronicsDTO.getTitle());
        assertEquals(description, electronicsDTO.getDescription());
        assertEquals(price, electronicsDTO.getPrice());
        assertEquals(type, electronicsDTO.getElectronicsType());
        assertEquals(images, electronicsDTO.getImages());
    }

    @Test
    public void testSettersAndGetters() {
        ElectronicsDTO electronicsDTO = new ElectronicsDTO();
        ElectronicsType type = ElectronicsType.DEVICE;

        electronicsDTO.setElectronicsType(type);
        assertEquals(type, electronicsDTO.getElectronicsType());
    }

    @Test
    public void testInheritedItemDTOMethods() {
        ElectronicsDTO electronicsDTO = new ElectronicsDTO();

        long id = 2L;
        String title = "Laptop";
        String description = "Gaming laptop";
        float price = 999.99f;
        long sellerId = 100L;
        List<String> images = Arrays.asList("laptop1.jpg", "laptop2.jpg");

        electronicsDTO.setId(id);
        electronicsDTO.setTitle(title);
        electronicsDTO.setDescription(description);
        electronicsDTO.setPrice(price);
        electronicsDTO.setSellerId(sellerId);
        electronicsDTO.setImages(images);

        assertEquals(id, electronicsDTO.getId());
        assertEquals(title, electronicsDTO.getTitle());
        assertEquals(description, electronicsDTO.getDescription());
        assertEquals(price, electronicsDTO.getPrice());
        assertEquals(sellerId, electronicsDTO.getSellerId());
        assertEquals(images, electronicsDTO.getImages());
    }
}