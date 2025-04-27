package com.spq.vinted.model;

import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class ItemTest {

    private Clothes item;
    private User seller;

    @BeforeEach
    void setUp() {
        seller = new User("seller@example.com", "password123", "sellerUser", "Seller", "User");
        item = new Clothes(
            "Test Item",
            "This is a test item",
            50.0f,
            ClothesSize.M,
            ClothesType.TSHIRT,
            Category.MAN,
            seller
        );
    }

    @Test
    void testConstructor() {
        assertEquals("Test Item", item.getTitle(), "El título debería coincidir");
        assertEquals("This is a test item", item.getDescription(), "La descripción debería coincidir");
        assertEquals(50.0f, item.getPrice(), "El precio debería coincidir");
        assertEquals(ClothesSize.M, item.getSize(), "La talla debería coincidir");
        assertEquals(ClothesType.TSHIRT, item.getClothesType(), "El tipo de ropa debería coincidir");
        assertEquals(Category.MAN, item.getCategory(), "La categoría debería coincidir");
        assertEquals(seller, item.getSeller(), "El vendedor debería coincidir");
        assertNotNull(item.getImages(), "La lista de imágenes no debería ser nula");
        assertTrue(item.getImages().isEmpty(), "La lista de imágenes debería estar vacía inicialmente");
    }

    @Test
    void testSettersAndGetters() {
        item.setTitle("New Title");
        item.setDescription("New Description");
        item.setPrice(100.0f);
        item.setSize(ClothesSize.L);
        item.setClothesType(ClothesType.PANTS);
        item.setCategory(Category.WOMAN);

        List<String> images = new ArrayList<>();
        images.add("image1.jpg");
        images.add("image2.jpg");
        item.setImages(images);

        User newSeller = new User("newSeller@example.com", "password456", "newSellerUser", "NewSeller", "User");
        item.setSeller(newSeller);

        assertEquals("New Title", item.getTitle(), "El nuevo título debería coincidir");
        assertEquals("New Description", item.getDescription(), "La nueva descripción debería coincidir");
        assertEquals(100.0f, item.getPrice(), "El nuevo precio debería coincidir");
        assertEquals(ClothesSize.L, item.getSize(), "La nueva talla debería coincidir");
        assertEquals(ClothesType.PANTS, item.getClothesType(), "El nuevo tipo de ropa debería coincidir");
        assertEquals(Category.WOMAN, item.getCategory(), "La nueva categoría debería coincidir");
        assertEquals(2, item.getImages().size(), "La lista de imágenes debería contener 2 elementos");
        assertEquals("image1.jpg", item.getImages().get(0), "La primera imagen debería coincidir");
        assertEquals("image2.jpg", item.getImages().get(1), "La segunda imagen debería coincidir");
        assertEquals(newSeller, item.getSeller(), "El nuevo vendedor debería coincidir");
    }

    @Test
    void testUsersWithItemInCart() {
        User user1 = new User("user1@example.com", "password1", "user1", "User1", "Test");
        User user2 = new User("user2@example.com", "password2", "user2", "User2", "Test");

        List<User> users = new ArrayList<>();
        users.add(user1);
        users.add(user2);

        item.setUsersWithItemInCart(users);

        assertEquals(2, item.getUsersWithItemInCart().size(), "Debería haber 2 usuarios con el ítem en el carrito");
        assertEquals(user1, item.getUsersWithItemInCart().get(0), "El primer usuario debería coincidir");
        assertEquals(user2, item.getUsersWithItemInCart().get(1), "El segundo usuario debería coincidir");
    }

    @Test
    void testEquals() {
        Clothes anotherItem = new Clothes(
            "Another Item",
            "Another description",
            30.0f,
            ClothesSize.S,
            ClothesType.JACKET,
            Category.MAN,
            seller
        );
        anotherItem.setId(item.getId());

        assertEquals(item, anotherItem, "Los ítems deberían ser iguales si tienen el mismo ID");

        anotherItem.setId(999L);
        assertNotEquals(item, anotherItem, "Los ítems no deberían ser iguales si tienen IDs diferentes");
    }
}