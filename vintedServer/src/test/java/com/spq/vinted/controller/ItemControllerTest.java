package com.spq.vinted.controller;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.hamcrest.Matchers.hasSize;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.spq.vinted.dto.*;
import com.spq.vinted.model.*;
import com.spq.vinted.service.ItemService;
import com.spq.vinted.service.UserService;

@ExtendWith(MockitoExtension.class)
public class ItemControllerTest {

    private MockMvc mockMvc;

    @Mock
    private ItemService itemService;

    @Mock
    private UserService userService;

    @InjectMocks
    private ItemController itemController;

    private ObjectMapper objectMapper;
    private User testUser;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(itemController).build();
        objectMapper = new ObjectMapper();
        
        testUser = new User();
        testUser.setId(1L);
        testUser.setUsername("testuser");
        testUser.setEmail("test@example.com");
    }

    // Shopping Cart Tests
    @Test
    void testAddItemToCart_Success() throws Exception {
        long token = 12345L;
        long itemId = 1L;

        mockMvc.perform(post("/items/shoppingCart/add")
                .param("token", String.valueOf(token))
                .param("itemId", String.valueOf(itemId)))
                .andExpect(status().isOk());

        verify(itemService).addItemToCart(token, itemId);
    }

    @Test
    void testAddItemToCart_Failure() throws Exception {
        long token = 12345L;
        long itemId = 1L;
        doThrow(new RuntimeException("Item not found")).when(itemService).addItemToCart(token, itemId);

        mockMvc.perform(post("/items/shoppingCart/add")
                .param("token", String.valueOf(token))
                .param("itemId", String.valueOf(itemId)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testGetCart_WithItems() throws Exception {
        long token = 12345L;
        List<Item> cartItems = new ArrayList<>();
        
        Clothes clothes = new Clothes();
        clothes.setId(1L);
        clothes.setTitle("T-Shirt");
        clothes.setPrice(19.99f);
        cartItems.add(clothes);

        when(itemService.getCartItems(token)).thenReturn(cartItems);

        mockMvc.perform(get("/items/shoppingCart")
                .param("token", String.valueOf(token)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].title").value("T-Shirt"))
                .andExpect(jsonPath("$[0].price").value(19.99));
    }

    @Test
    void testGetCart_Empty() throws Exception {
        long token = 12345L;
        when(itemService.getCartItems(token)).thenReturn(new ArrayList<>());

        mockMvc.perform(get("/items/shoppingCart")
                .param("token", String.valueOf(token)))
                .andExpect(status().isOk())
                .andExpect(content().string(""));
    }

    @Test
    void testGetCart_Error() throws Exception {
        long token = 12345L;
        when(itemService.getCartItems(token)).thenThrow(new RuntimeException("User not found"));

        mockMvc.perform(get("/items/shoppingCart")
                .param("token", String.valueOf(token)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testDeleteItemFromCart_Success() throws Exception {
        long token = 12345L;
        long itemId = 1L;

        mockMvc.perform(post("/items/shoppingCart/remove")
                .param("token", String.valueOf(token))
                .param("itemId", String.valueOf(itemId)))
                .andExpect(status().isOk());

        verify(itemService).removeItemFromCart(token, itemId);
    }

    @Test
    void testDeleteItemFromCart_Error() throws Exception {
        long token = 12345L;
        long itemId = 1L;
        doThrow(new RuntimeException("Item not found")).when(itemService).removeItemFromCart(token, itemId);

        mockMvc.perform(post("/items/shoppingCart/remove")
                .param("token", String.valueOf(token))
                .param("itemId", String.valueOf(itemId)))
                .andExpect(status().isBadRequest());
    }

    // Wishlist Tests
    @Test
    void testAddItemToWishlist_Success() throws Exception {
        long token = 12345L;
        long itemId = 1L;

        mockMvc.perform(post("/items/wishlist/add")
                .param("token", String.valueOf(token))
                .param("itemId", String.valueOf(itemId)))
                .andExpect(status().isOk());

        verify(itemService).addItemToWishlist(token, itemId);
    }

    @Test
    void testAddItemToWishlist_Failure() throws Exception {
        long token = 12345L;
        long itemId = 1L;
        doThrow(new RuntimeException("Item not found")).when(itemService).addItemToWishlist(token, itemId);

        mockMvc.perform(post("/items/wishlist/add")
                .param("token", String.valueOf(token))
                .param("itemId", String.valueOf(itemId)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testGetWishlist_WithItems() throws Exception {
        long token = 12345L;
        List<Item> wishlistItems = new ArrayList<>();
        
        Electronics electronics = new Electronics();
        electronics.setId(1L);
        electronics.setTitle("Smartphone");
        electronics.setPrice(599.99f);
        wishlistItems.add(electronics);

        when(itemService.getWishlistItems(token)).thenReturn(wishlistItems);

        mockMvc.perform(get("/items/wishlist")
                .param("token", String.valueOf(token)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].title").value("Smartphone"))
                .andExpect(jsonPath("$[0].price").value(599.99));
    }

    @Test
    void testGetWishlist_Empty() throws Exception {
        long token = 12345L;
        when(itemService.getWishlistItems(token)).thenReturn(new ArrayList<>());

        mockMvc.perform(get("/items/wishlist")
                .param("token", String.valueOf(token)))
                .andExpect(status().isOk())
                .andExpect(content().string(""));
    }

    @Test
    void testGetWishlist_Error() throws Exception {
        long token = 12345L;
        when(itemService.getWishlistItems(token)).thenThrow(new RuntimeException("User not found"));

        mockMvc.perform(get("/items/wishlist")
                .param("token", String.valueOf(token)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testRemoveItemFromWishlist_Success() throws Exception {
        long token = 12345L;
        long itemId = 1L;

        mockMvc.perform(post("/items/wishlist/remove")
                .param("token", String.valueOf(token))
                .param("itemId", String.valueOf(itemId)))
                .andExpect(status().isOk());

        verify(itemService).removeItemFromWishlist(token, itemId);
    }

    @Test
    void testRemoveItemFromWishlist_Error() throws Exception {
        long token = 12345L;
        long itemId = 1L;
        doThrow(new RuntimeException("Item not found")).when(itemService).removeItemFromWishlist(token, itemId);

        mockMvc.perform(post("/items/wishlist/remove")
                .param("token", String.valueOf(token))
                .param("itemId", String.valueOf(itemId)))
                .andExpect(status().isBadRequest());
    }

    // Item Data Tests
    @Test
    void testUploadItemData_ClothesSuccess() throws Exception {
        long token = 12345L;
        ClothesDTO clothesDTO = new ClothesDTO();
        clothesDTO.setTitle("T-Shirt");
        clothesDTO.setDescription("Nice T-Shirt");
        clothesDTO.setPrice(19.99f);
        clothesDTO.setSize(ClothesSize.M);
        clothesDTO.setClothesType(ClothesType.TSHIRT);
        clothesDTO.setCategory(Category.MAN);

        when(userService.getUserByToken(token)).thenReturn(testUser);
        
        Clothes savedItem = new Clothes();
        savedItem.setId(100L);
        when(itemService.saveItem(any(Clothes.class))).thenReturn(savedItem);

        mockMvc.perform(post("/items/itemData")
                .param("token", String.valueOf(token))
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(clothesDTO)))
                .andExpect(status().isOk())
                .andExpect(content().string("100"));
    }

    @Test
    void testUploadItemData_ElectronicsSuccess() throws Exception {
        long token = 12345L;
        ElectronicsDTO electronicsDTO = new ElectronicsDTO();
        electronicsDTO.setTitle("Laptop");
        electronicsDTO.setDescription("Gaming Laptop");
        electronicsDTO.setPrice(999.99f);
        electronicsDTO.setElectronicsType(ElectronicsType.DEVICE);

        when(userService.getUserByToken(token)).thenReturn(testUser);
        
        Electronics savedItem = new Electronics();
        savedItem.setId(100L);
        when(itemService.saveItem(any(Electronics.class))).thenReturn(savedItem);

        mockMvc.perform(post("/items/itemData")
                .param("token", String.valueOf(token))
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(electronicsDTO)))
                .andExpect(status().isOk())
                .andExpect(content().string("100"));
    }

    @Test
    void testUploadItemData_PetSuccess() throws Exception {
        long token = 12345L;
        PetDTO petDTO = new PetDTO();
        petDTO.setTitle("Dog Toy");
        petDTO.setDescription("Chew Toy");
        petDTO.setPrice(9.99f);
        petDTO.setSpecies(Species.DOG);

        when(userService.getUserByToken(token)).thenReturn(testUser);
        
        Pet savedItem = new Pet();
        savedItem.setId(100L);
        when(itemService.saveItem(any(Pet.class))).thenReturn(savedItem);

        mockMvc.perform(post("/items/itemData")
                .param("token", String.valueOf(token))
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(petDTO)))
                .andExpect(status().isOk())
                .andExpect(content().string("100"));
    }

    @Test
    void testUploadItemData_HomeSuccess() throws Exception {
        long token = 12345L;
        HomeDTO homeDTO = new HomeDTO();
        homeDTO.setTitle("Lamp");
        homeDTO.setDescription("Table Lamp");
        homeDTO.setPrice(29.99f);
        homeDTO.setHomeType(HomeType.FURNITURE);

        when(userService.getUserByToken(token)).thenReturn(testUser);
        
        Home savedItem = new Home();
        savedItem.setId(100L);
        when(itemService.saveItem(any(Home.class))).thenReturn(savedItem);

        mockMvc.perform(post("/items/itemData")
                .param("token", String.valueOf(token))
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(homeDTO)))
                .andExpect(status().isOk())
                .andExpect(content().string("100"));
    }

    @Test
    void testUploadItemData_EntertainmentSuccess() throws Exception {
        long token = 12345L;
        EntertainmentDTO entertainmentDTO = new EntertainmentDTO();
        entertainmentDTO.setTitle("Board Game");
        entertainmentDTO.setDescription("Strategy Game");
        entertainmentDTO.setPrice(49.99f);
        entertainmentDTO.setEntertainmentType(EntertainmentType.GAME);

        when(userService.getUserByToken(token)).thenReturn(testUser);
        
        Entertainment savedItem = new Entertainment();
        savedItem.setId(100L);
        when(itemService.saveItem(any(Entertainment.class))).thenReturn(savedItem);

        mockMvc.perform(post("/items/itemData")
                .param("token", String.valueOf(token))
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(entertainmentDTO)))
                .andExpect(status().isOk())
                .andExpect(content().string("100"));
    }

    @Test
    void testUploadItemData_UserNotFound() throws Exception {
        long token = 12345L;
        ClothesDTO clothesDTO = new ClothesDTO();
        when(userService.getUserByToken(token)).thenReturn(null);

        mockMvc.perform(post("/items/itemData")
                .param("token", String.valueOf(token))
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(clothesDTO)))
                .andExpect(status().isForbidden());
    }

    @Test
    void testUploadItemData_SaveItemReturnsNull() throws Exception {
        long token = 12345L;
        ClothesDTO clothesDTO = new ClothesDTO();
        clothesDTO.setTitle("T-Shirt");
        clothesDTO.setDescription("Nice T-Shirt");
        clothesDTO.setPrice(19.99f);
        clothesDTO.setSize(ClothesSize.M);
        clothesDTO.setClothesType(ClothesType.TSHIRT);
        clothesDTO.setCategory(Category.MAN);

        when(userService.getUserByToken(token)).thenReturn(testUser);
        when(itemService.saveItem(any(Clothes.class))).thenReturn(null);

        mockMvc.perform(post("/items/itemData")
                .param("token", String.valueOf(token))
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(clothesDTO)))
                .andExpect(status().isBadRequest());
    }

    // Image Tests
    @Test
    void testUpdateItemImage_Success() throws Exception {
        long itemId = 1L;
        MockMultipartFile file1 = new MockMultipartFile(
                "images", "image1.jpg", "image/jpeg", "test image content".getBytes());
        MockMultipartFile file2 = new MockMultipartFile(
                "images", "image2.jpg", "image/jpeg", "test image content 2".getBytes());

        mockMvc.perform(multipart("/items/itemImage")
                .file(file1)
                .file(file2)
                .param("itemId", String.valueOf(itemId))
                .with(request -> {
                    request.setMethod("PUT");
                    return request;
                }))
                .andExpect(status().isNoContent());

        verify(itemService).uploadItemImages(eq(itemId), anyList());
    }

    @Test
    void testUpdateItemImage_ItemNotFound() throws Exception {
        long itemId = 1L;
        MockMultipartFile file = new MockMultipartFile(
                "images", "image.jpg", "image/jpeg", "test image content".getBytes());
        doThrow(new RuntimeException("Item not found")).when(itemService).uploadItemImages(eq(itemId), anyList());

        mockMvc.perform(multipart("/items/itemImage")
                .file(file)
                .param("itemId", String.valueOf(itemId))
                .with(request -> {
                    request.setMethod("PUT");
                    return request;
                }))
                .andExpect(status().isNotFound());
    }

    @Test
    void testUpdateItemImage_IOException() throws Exception {
        long itemId = 1L;
        MockMultipartFile file = new MockMultipartFile(
                "images", "image.jpg", "image/jpeg", "test image content".getBytes());
        
        doThrow(new IOException("IO error")).when(itemService).uploadItemImages(eq(itemId), anyList());

        mockMvc.perform(multipart("/items/itemImage")
                .file(file)
                .param("itemId", String.valueOf(itemId))
                .with(request -> {
                    request.setMethod("PUT");
                    return request;
                }))
                .andExpect(status().isInternalServerError());
    }

    // Get Items Tests
    @Test
    void testGetItems_Success() throws Exception {
        List<Item> items = new ArrayList<>();
        Clothes clothes = new Clothes();
        clothes.setId(1L);
        clothes.setTitle("T-Shirt");
        items.add(clothes);

        when(itemService.getItems(null)).thenReturn(items);

        mockMvc.perform(get("/items/items"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].title").value("T-Shirt"));
    }

    @Test
    void testGetItems_WithToken() throws Exception {
        long token = 12345L;
        List<Item> items = new ArrayList<>();
        Clothes clothes = new Clothes();
        clothes.setId(1L);
        clothes.setTitle("T-Shirt");
        items.add(clothes);

        when(itemService.getItems(token)).thenReturn(items);

        mockMvc.perform(get("/items/items")
                .param("token", String.valueOf(token)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].title").value("T-Shirt"));
    }

    @Test
    void testGetItemById_Success() throws Exception {
        long itemId = 1L;
        Clothes clothes = new Clothes();
        clothes.setId(itemId);
        clothes.setTitle("T-Shirt");

        when(itemService.getItemById(itemId)).thenReturn(clothes);

        mockMvc.perform(get("/items/item/{id}", itemId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("T-Shirt"));
    }

    @Test
    void testGetItemById_Error() throws Exception {
        long itemId = 1L;
        when(itemService.getItemById(itemId)).thenThrow(new RuntimeException("Item not found"));

        mockMvc.perform(get("/items/item/{id}", itemId))
                .andExpect(status().isBadRequest());
    }

    // Category specific tests
    @Test
    void testGetClothes_WithToken() throws Exception {
        long token = 12345L;
        List<Clothes> clothes = new ArrayList<>();
        
        // Own item (should be filtered out)
        Clothes ownItem = new Clothes();
        ownItem.setId(1L);
        ownItem.setTitle("My T-Shirt");
        ownItem.setSeller(testUser);
        
        // Other user's item
        User otherUser = new User();
        otherUser.setId(2L);
        Clothes otherItem = new Clothes();
        otherItem.setId(2L);
        otherItem.setTitle("Other T-Shirt");
        otherItem.setSeller(otherUser);
        
        clothes.add(ownItem);
        clothes.add(otherItem);

        when(itemService.getClothes()).thenReturn(clothes);
        when(userService.getUserByToken(token)).thenReturn(testUser);

        mockMvc.perform(get("/items/clothes")
                .param("token", String.valueOf(token)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].title").value("Other T-Shirt"));
    }

    @Test
    void testGetClothes_WithoutToken() throws Exception {
        List<Clothes> clothes = new ArrayList<>();
        Clothes item = new Clothes();
        item.setId(1L);
        item.setTitle("T-Shirt");
        clothes.add(item);

        when(itemService.getClothes()).thenReturn(clothes);

        mockMvc.perform(get("/items/clothes"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].title").value("T-Shirt"));
    }

    @Test
    void testGetClothesByCategory_Success() throws Exception {
        Category category = Category.MAN;
        List<Clothes> clothes = new ArrayList<>();
        Clothes item = new Clothes();
        item.setId(1L);
        item.setTitle("Men's T-Shirt");
        item.setCategory(category);
        clothes.add(item);

        when(itemService.getClothesByCategory(category)).thenReturn(clothes);

        mockMvc.perform(get("/items/clothes/{category}", category))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].title").value("Men's T-Shirt"));
    }

    @Test
    void testGetElectronics_WithToken() throws Exception {
        long token = 12345L;
        List<Electronics> electronics = new ArrayList<>();
        
        Electronics ownItem = new Electronics();
        ownItem.setId(1L);
        ownItem.setTitle("My Phone");
        ownItem.setSeller(testUser);
        
        User otherUser = new User();
        otherUser.setId(2L);
        Electronics otherItem = new Electronics();
        otherItem.setId(2L);
        otherItem.setTitle("Other Phone");
        otherItem.setSeller(otherUser);
        
        electronics.add(ownItem);
        electronics.add(otherItem);

        when(itemService.getElectronics()).thenReturn(electronics);
        when(userService.getUserByToken(token)).thenReturn(testUser);

        mockMvc.perform(get("/items/electronics")
                .param("token", String.valueOf(token)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].title").value("Other Phone"));
    }

    @Test
    void testGetHomeItems_WithToken() throws Exception {
        long token = 12345L;
        List<Home> homeItems = new ArrayList<>();
        
        Home ownItem = new Home();
        ownItem.setId(1L);
        ownItem.setTitle("My Sofa");
        ownItem.setSeller(testUser);
        
        User otherUser = new User();
        otherUser.setId(2L);
        Home otherItem = new Home();
        otherItem.setId(2L);
        otherItem.setTitle("Other Sofa");
        otherItem.setSeller(otherUser);
        
        homeItems.add(ownItem);
        homeItems.add(otherItem);

        when(itemService.getHomeItems()).thenReturn(homeItems);
        when(userService.getUserByToken(token)).thenReturn(testUser);

        mockMvc.perform(get("/items/home")
                .param("token", String.valueOf(token)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].title").value("Other Sofa"));
    }

    @Test
    void testGetItemsForPet_WithToken() throws Exception {
        long token = 12345L;
        List<Pet> petItems = new ArrayList<>();
        
        Pet ownItem = new Pet();
        ownItem.setId(1L);
        ownItem.setTitle("My Pet Food");
        ownItem.setSeller(testUser);
        
        User otherUser = new User();
        otherUser.setId(2L);
        Pet otherItem = new Pet();
        otherItem.setId(2L);
        otherItem.setTitle("Other Pet Food");
        otherItem.setSeller(otherUser);
        
        petItems.add(ownItem);
        petItems.add(otherItem);

        when(itemService.getItemsForPet()).thenReturn(petItems);
        when(userService.getUserByToken(token)).thenReturn(testUser);

        mockMvc.perform(get("/items/pet")
                .param("token", String.valueOf(token)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].title").value("Other Pet Food"));
    }

    @Test
    void testGetItemsForEntertainment_WithToken() throws Exception {
        long token = 12345L;
        List<Entertainment> entertainmentItems = new ArrayList<>();
        
        Entertainment ownItem = new Entertainment();
        ownItem.setId(1L);
        ownItem.setTitle("My Game");
        ownItem.setSeller(testUser);
        
        User otherUser = new User();
        otherUser.setId(2L);
        Entertainment otherItem = new Entertainment();
        otherItem.setId(2L);
        otherItem.setTitle("Other Game");
        otherItem.setSeller(otherUser);
        
        entertainmentItems.add(ownItem);
        entertainmentItems.add(otherItem);

        when(itemService.getItemsforEntertainment()).thenReturn(entertainmentItems);
        when(userService.getUserByToken(token)).thenReturn(testUser);

        mockMvc.perform(get("/items/entertainment")
                .param("token", String.valueOf(token)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].title").value("Other Game"));
    }

    // Owner and Seller Tests
    @Test
    void testGetItemOwner_Success() throws Exception {
        long itemId = 1L;
        when(itemService.getItemOwner(itemId)).thenReturn(testUser);

        mockMvc.perform(get("/items/{itemId}/owner", itemId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("testuser"));
    }

    @Test
    void testGetItemOwner_NotFound() throws Exception {
        long itemId = 1L;
        when(itemService.getItemOwner(itemId)).thenThrow(new RuntimeException("Item not found"));

        mockMvc.perform(get("/items/{itemId}/owner", itemId))
                .andExpect(status().isNotFound());
    }

    @Test
    void testGetUserItems_Success() throws Exception {
        long userId = 1L;
        List<Item> items = new ArrayList<>();
        Clothes clothes = new Clothes();
        clothes.setId(1L);
        clothes.setTitle("T-Shirt");
        items.add(clothes);

        when(itemService.getUserItems(userId)).thenReturn(items);

        mockMvc.perform(get("/items/userItems/{userId}", userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].title").value("T-Shirt"));
    }

    @Test
    void testGetSeller_Success() throws Exception {
        long itemId = 1L;
        Clothes item = new Clothes();
        item.setId(itemId);
        item.setSeller(testUser);

        when(itemService.getItemById(itemId)).thenReturn(item);

        mockMvc.perform(get("/items/seller/{itemId}", itemId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("testuser"));
    }

    @Test
    void testGetSeller_Error() throws Exception {
        long itemId = 1L;
        when(itemService.getItemById(itemId)).thenThrow(new RuntimeException("Item not found"));

        mockMvc.perform(get("/items/seller/{itemId}", itemId))
                .andExpect(status().isBadRequest());
    }

    // Delete Tests
    @Test
    void testDeleteItem_Success() throws Exception {
        long token = 12345L;
        long itemId = 1L;

        mockMvc.perform(delete("/items/delete/{itemId}", itemId)
                .param("token", String.valueOf(token)))
                .andExpect(status().isNoContent());

        verify(itemService).deleteItem(token, itemId);
    }

    @Test
    void testDeleteItem_NotFound() throws Exception {
        long token = 12345L;
        long itemId = 1L;
        doThrow(new RuntimeException("Item not found")).when(itemService).deleteItem(token, itemId);

        mockMvc.perform(delete("/items/delete/{itemId}", itemId)
                .param("token", String.valueOf(token)))
                .andExpect(status().isNotFound());
    }

    @Test
    void testDeleteItem_NotAuthorized() throws Exception {
        long token = 12345L;
        long itemId = 1L;
        doThrow(new RuntimeException("Not authorized")).when(itemService).deleteItem(token, itemId);

        mockMvc.perform(delete("/items/delete/{itemId}", itemId)
                .param("token", String.valueOf(token)))
                .andExpect(status().isForbidden());
    }

    @Test
    void testDeleteItem_InternalError() throws Exception {
        long token = 12345L;
        long itemId = 1L;
        doThrow(new RuntimeException("Internal error")).when(itemService).deleteItem(token, itemId);

        mockMvc.perform(delete("/items/delete/{itemId}", itemId)
                .param("token", String.valueOf(token)))
                .andExpect(status().isInternalServerError());
    }

    // Search Tests
    @Test
    void testSearchItems_Success() throws Exception {
        String query = "test";
        List<Item> items = new ArrayList<>();
        Clothes clothes = new Clothes();
        clothes.setId(1L);
        clothes.setTitle("Test T-Shirt");
        items.add(clothes);

        when(itemService.searchItems(null, query)).thenReturn(items);

        mockMvc.perform(get("/items/search")
                .param("search_text", query))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].title").value("Test T-Shirt"));
    }

    @Test
    void testSearchItems_WithToken() throws Exception {
        long token = 12345L;
        String query = "test";
        List<Item> items = new ArrayList<>();
        Clothes clothes = new Clothes();
        clothes.setId(1L);
        clothes.setTitle("Test T-Shirt");
        items.add(clothes);

        when(itemService.searchItems(token, query)).thenReturn(items);

        mockMvc.perform(get("/items/search")
                .param("token", String.valueOf(token))
                .param("search_text", query))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].title").value("Test T-Shirt"));
    }

    @Test
    void testSearchItems_Error() throws Exception {
        String query = "test";
        when(itemService.searchItems(null, query)).thenThrow(new RuntimeException("Search error"));

        mockMvc.perform(get("/items/search")
                .param("search_text", query))
                .andExpect(status().isBadRequest());
    }

    // Show Image Test
    @Test
    void testShowImagen_NotFound() throws Exception {
        String nombreImagen = "nonexistent.jpg";
        
        mockMvc.perform(get("/items/images/{nombreImagen}", nombreImagen))
                .andExpect(status().isNotFound());
    }

    // Error Cases Tests
    @Test
    void testGetClothes_Error() throws Exception {
        when(itemService.getClothes()).thenThrow(new RuntimeException("Error getting clothes"));

        mockMvc.perform(get("/items/clothes"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testGetClothesByCategory_Error() throws Exception {
        Category category = Category.MAN;
        when(itemService.getClothesByCategory(category)).thenThrow(new RuntimeException("Error getting clothes by category"));

        mockMvc.perform(get("/items/clothes/{category}", category))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testGetElectronics_Error() throws Exception {
        when(itemService.getElectronics()).thenThrow(new RuntimeException("Error getting electronics"));

        mockMvc.perform(get("/items/electronics"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testGetHomeItems_Error() throws Exception {
        when(itemService.getHomeItems()).thenThrow(new RuntimeException("Error getting home items"));

        mockMvc.perform(get("/items/home"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testGetItemsForPet_Error() throws Exception {
        when(itemService.getItemsForPet()).thenThrow(new RuntimeException("Error getting pet items"));

        mockMvc.perform(get("/items/pet"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testGetItemsForEntertainment_Error() throws Exception {
        when(itemService.getItemsforEntertainment()).thenThrow(new RuntimeException("Error getting entertainment items"));

        mockMvc.perform(get("/items/entertainment"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testGetUserItems_Error() throws Exception {
        long userId = 1L;
        when(itemService.getUserItems(userId)).thenThrow(new RuntimeException("Error getting user items"));

        mockMvc.perform(get("/items/userItems/{userId}", userId))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testGetItems_Error() throws Exception {
        when(itemService.getItems(null)).thenThrow(new RuntimeException("Error getting items"));

        mockMvc.perform(get("/items/items"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testUpdateItemImage_UnknownError() throws Exception {
        long itemId = 1L;
        MockMultipartFile file = new MockMultipartFile(
                "images", "image.jpg", "image/jpeg", "test image content".getBytes());
        
        doThrow(new RuntimeException("Unknown error")).when(itemService).uploadItemImages(eq(itemId), anyList());

        mockMvc.perform(multipart("/items/itemImage")
                .file(file)
                .param("itemId", String.valueOf(itemId))
                .with(request -> {
                    request.setMethod("PUT");
                    return request;
                }))
                .andExpect(status().isInternalServerError());
    }
}