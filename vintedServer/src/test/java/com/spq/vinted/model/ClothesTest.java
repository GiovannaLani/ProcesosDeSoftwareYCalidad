package com.spq.vinted.model;

import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.spq.vinted.dto.ClothesDTO;

class ClothesTest {

    private Clothes clothes;
    private User seller;

    @BeforeEach
    void setUp() {
        seller = new User("seller@example.com", "password123", "sellerUser", "Seller", "User");
        clothes = new Clothes(
            "Test Clothes",
            "This is a test clothes item",
            75.0f,
            ClothesSize.M,
            ClothesType.TSHIRT,
            Category.MAN,
            seller
        );
    }

    @Test
    void testConstructor() {
        assertEquals("Test Clothes", clothes.getTitle(), "El título debería coincidir");
        assertEquals("This is a test clothes item", clothes.getDescription(), "La descripción debería coincidir");
        assertEquals(75.0f, clothes.getPrice(), "El precio debería coincidir");
        assertEquals(ClothesSize.M, clothes.getSize(), "La talla debería coincidir");
        assertEquals(ClothesType.TSHIRT, clothes.getClothesType(), "El tipo de ropa debería coincidir");
        assertEquals(Category.MAN, clothes.getCategory(), "La categoría debería coincidir");
        assertEquals(seller, clothes.getSeller(), "El vendedor debería coincidir");
        assertNotNull(clothes.getImages(), "La lista de imágenes no debería ser nula");
        assertTrue(clothes.getImages().isEmpty(), "La lista de imágenes debería estar vacía inicialmente");
    }

    @Test
    void testSettersAndGetters() {
        clothes.setTitle("New Title");
        clothes.setDescription("New Description");
        clothes.setPrice(100.0f);
        clothes.setSize(ClothesSize.L);
        clothes.setClothesType(ClothesType.JACKET);
        clothes.setCategory(Category.WOMAN);

        List<String> images = new ArrayList<>();
        images.add("image1.jpg");
        images.add("image2.jpg");
        clothes.setImages(images);

        User newSeller = new User("newSeller@example.com", "password456", "newSellerUser", "NewSeller", "User");
        clothes.setSeller(newSeller);

        assertEquals("New Title", clothes.getTitle(), "El nuevo título debería coincidir");
        assertEquals("New Description", clothes.getDescription(), "La nueva descripción debería coincidir");
        assertEquals(100.0f, clothes.getPrice(), "El nuevo precio debería coincidir");
        assertEquals(ClothesSize.L, clothes.getSize(), "La nueva talla debería coincidir");
        assertEquals(ClothesType.JACKET, clothes.getClothesType(), "El nuevo tipo de ropa debería coincidir");
        assertEquals(Category.WOMAN, clothes.getCategory(), "La nueva categoría debería coincidir");
        assertEquals(2, clothes.getImages().size(), "La lista de imágenes debería contener 2 elementos");
        assertEquals("image1.jpg", clothes.getImages().get(0), "La primera imagen debería coincidir");
        assertEquals("image2.jpg", clothes.getImages().get(1), "La segunda imagen debería coincidir");
        assertEquals(newSeller, clothes.getSeller(), "El nuevo vendedor debería coincidir");
    }

    @Test
    void testToDTO() {
        List<String> images = new ArrayList<>();
        images.add("image1.jpg");
        images.add("image2.jpg");
        clothes.setImages(images);

        ClothesDTO clothesDTO = (ClothesDTO) clothes.toDTO();

        assertEquals(clothes.getId(), clothesDTO.getId(), "El ID debería coincidir");
        assertEquals(clothes.getTitle(), clothesDTO.getTitle(), "El título debería coincidir");
        assertEquals(clothes.getDescription(), clothesDTO.getDescription(), "La descripción debería coincidir");
        assertEquals(clothes.getPrice(), clothesDTO.getPrice(), "El precio debería coincidir");
        assertEquals(clothes.getSize(), clothesDTO.getSize(), "La talla debería coincidir");
        assertEquals(clothes.getClothesType(), clothesDTO.getClothesType(), "El tipo de ropa debería coincidir");
        assertEquals(clothes.getCategory(), clothesDTO.getCategory(), "La categoría debería coincidir");
        assertEquals(clothes.getImages(), clothesDTO.getImages(), "La lista de imágenes debería coincidir");
    }
}