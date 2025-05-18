package com.spq.vinted.model;

import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.spq.vinted.dto.UserDTO;

class UserTest {

    private User user;

    @BeforeEach
    void setUp() {
        user = new User("test@example.com", "password123", "testuser", "Test", "User");
    }

    @Test
    void testConstructor() {
        assertEquals("test@example.com", user.getEmail(), "El email debería coincidir");
        assertEquals("password123", user.getPassword(), "La contraseña debería coincidir");
        assertEquals("testuser", user.getUsername(), "El nombre de usuario debería coincidir");
        assertEquals("Test", user.getName(), "El nombre debería coincidir");
        assertEquals("User", user.getSurname(), "El apellido debería coincidir");
        assertNotNull(user.getCartItems(), "La lista de ítems del carrito no debería ser nula");
        assertTrue(user.getCartItems().isEmpty(), "La lista de ítems del carrito debería estar vacía inicialmente");
    }

    @Test
    void testSettersAndGetters() {
        user.setEmail("newemail@example.com");
        user.setPassword("newpassword");
        user.setUsername("newuser");
        user.setName("New");
        user.setSurname("Name");
        user.setDescription("This is a test description");
        user.setProfileImage("profile.jpg");

        assertEquals("newemail@example.com", user.getEmail(), "El email debería coincidir");
        assertEquals("newpassword", user.getPassword(), "La contraseña debería coincidir");
        assertEquals("newuser", user.getUsername(), "El nombre de usuario debería coincidir");
        assertEquals("New", user.getName(), "El nombre debería coincidir");
        assertEquals("Name", user.getSurname(), "El apellido debería coincidir");
        assertEquals("This is a test description", user.getDescription(), "La descripción debería coincidir");
        assertEquals("profile.jpg", user.getProfileImage(), "La imagen de perfil debería coincidir");
    }

    @Test
    void testAddItemToCart() {
        Item item = new Clothes();
        item.setTitle("Test Item");

        user.addItemToCart(item);

        assertEquals(1, user.getCartItems().size(), "El carrito debería contener 1 ítem");
        assertEquals("Test Item", user.getCartItems().get(0).getTitle(), "El título del ítem debería coincidir");
    }

    @Test
    void testSetCartItems() {
        Item item1 = new Clothes();
        item1.setTitle("Item 1");

        Item item2 = new Home();
        item2.setTitle("Item 2");

        List<Item> items = new ArrayList<>();
        items.add(item1);
        items.add(item2);

        user.setCartItems(items);

        assertEquals(2, user.getCartItems().size(), "El carrito debería contener 2 ítems");
        assertEquals("Item 1", user.getCartItems().get(0).getTitle(), "El título del primer ítem debería coincidir");
        assertEquals("Item 2", user.getCartItems().get(1).getTitle(), "El título del segundo ítem debería coincidir");
    }

    @Test
    void testToDTO() {
        user.setDescription("Test description");
        user.setProfileImage("profile.jpg");

        UserDTO userDTO = user.toDTO();

        assertEquals(user.getId(), userDTO.id(), "El ID debería coincidir");
        assertEquals(user.getUsername(), userDTO.username(), "El nombre de usuario debería coincidir");
        assertEquals(user.getName(), userDTO.name(), "El nombre debería coincidir");
        assertEquals(user.getSurname(), userDTO.surname(), "El apellido debería coincidir");
        assertEquals(user.getDescription(), userDTO.description(), "La descripción debería coincidir");
        assertEquals(user.getProfileImage(), userDTO.profileImage(), "La imagen de perfil debería coincidir");
    }

    @Test
    void testFollowersManagement() {
        User user1 = new User();
        user1.setUsername("user1");
        User user2 = new User();
        user2.setUsername("user2");
        List<User> followersList = new ArrayList<>();
        followersList.add(user2);
        
        user1.setFollowers(followersList);
        
        assertEquals(1, user1.getFollowers().size());
        assertEquals("user2", user1.getFollowers().get(0).getUsername());
        
        User newUser = new User("new@test.com", "pass", "newuser", "New", "User");
        assertNotNull(newUser.getFollowers());
        assertTrue(newUser.getFollowers().isEmpty());
    }


    @Test
    void testFollowUser() {
        User user1 = new User();
        user1.setUsername("user1");
        User user2 = new User();
        user2.setUsername("user2");
        user1.follow(user2);
        
        assertTrue(user1.getFollowing().contains(user2));
        assertTrue(user2.getFollowers().contains(user1));
        
        user1.follow(user2);
        assertEquals(1, user1.getFollowing().size());
    }

    @Test
    void testUnfollowUser() {
        User user1 = new User();
        user1.setUsername("user1");
        User user2 = new User();
        user2.setUsername("user2");
        user1.follow(user2);
        
        user1.unfollow(user2);
        
        assertFalse(user1.getFollowing().contains(user2));
        assertFalse(user2.getFollowers().contains(user1));
        
        user1.unfollow(user2);  
        assertEquals(0, user1.getFollowing().size());
    }

    @Test
    void testWishlistManagement() {
        User user1 = new User();
        user1.setUsername("user1");
        Item testItem = new Clothes();
        testItem.setTitle("Camiseta Vintage");
        user1.addItemToWishlist(testItem);
        
        assertEquals(1, user1.getWishlistItems().size());
        assertEquals("Camiseta Vintage", user1.getWishlistItems().get(0).getTitle());
        
        User newUser = new User("new@test.com", "pass", "newuser", "New", "User");
        assertNotNull(newUser.getWishlistItems());
        assertTrue(newUser.getWishlistItems().isEmpty());
    }
    

}
