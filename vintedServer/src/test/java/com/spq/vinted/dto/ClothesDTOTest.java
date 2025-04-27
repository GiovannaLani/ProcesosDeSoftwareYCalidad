package com.spq.vinted.dto;

import com.spq.vinted.model.Category;
import com.spq.vinted.model.ClothesSize;
import com.spq.vinted.model.ClothesType;
import org.junit.jupiter.api.Test;
import java.util.Arrays;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

public class ClothesDTOTest {

    @Test
    public void testNoArgsConstructor() {
        ClothesDTO clothesDTO = new ClothesDTO();
        
        assertNull(clothesDTO.getSize());
        assertNull(clothesDTO.getClothesType());
        assertNull(clothesDTO.getCategory());
        // Heredados de ItemDTO
        assertEquals(0L, clothesDTO.getId());
        assertNull(clothesDTO.getTitle());
        assertNull(clothesDTO.getDescription());
        assertEquals(0.0f, clothesDTO.getPrice());
        assertNotNull(clothesDTO.getImages());
        assertTrue(clothesDTO.getImages().isEmpty());
    }

    @Test
    public void testAllArgsConstructor() {
        long id = 1L;
        String title = "T-Shirt";
        String description = "Cotton T-Shirt";
        float price = 19.99f;
        ClothesSize size = ClothesSize.M;
        ClothesType type = ClothesType.TSHIRT;
        Category category = Category.MAN;
        List<String> images = Arrays.asList("image1.jpg", "image2.jpg");

        ClothesDTO clothesDTO = new ClothesDTO(id, title, description, price, size, type, category, images);

        assertEquals(id, clothesDTO.getId());
        assertEquals(title, clothesDTO.getTitle());
        assertEquals(description, clothesDTO.getDescription());
        assertEquals(price, clothesDTO.getPrice());
        assertEquals(size, clothesDTO.getSize());
        assertEquals(type, clothesDTO.getClothesType());
        assertEquals(category, clothesDTO.getCategory());
        assertEquals(images, clothesDTO.getImages());
    }

    @Test
    public void testSettersAndGetters() {
        ClothesDTO clothesDTO = new ClothesDTO();

        ClothesSize size = ClothesSize.L;
        ClothesType type = ClothesType.PANTS;
        Category category = Category.WOMAN;

        clothesDTO.setSize(size);
        clothesDTO.setClothesType(type);
        clothesDTO.setCategory(category);

        assertEquals(size, clothesDTO.getSize());
        assertEquals(type, clothesDTO.getClothesType());
        assertEquals(category, clothesDTO.getCategory());
    }

    @Test
    public void testInheritedItemDTOMethods() {
        ClothesDTO clothesDTO = new ClothesDTO();

        long id = 2L;
        String title = "Jeans";
        String description = "Blue Jeans";
        float price = 29.99f;
        List<String> images = Arrays.asList("jeans1.jpg", "jeans2.jpg");

        clothesDTO.setId(id);
        clothesDTO.setTitle(title);
        clothesDTO.setDescription(description);
        clothesDTO.setPrice(price);
        clothesDTO.setImages(images);

        assertEquals(id, clothesDTO.getId());
        assertEquals(title, clothesDTO.getTitle());
        assertEquals(description, clothesDTO.getDescription());
        assertEquals(price, clothesDTO.getPrice());
        assertEquals(images, clothesDTO.getImages());
    }
}