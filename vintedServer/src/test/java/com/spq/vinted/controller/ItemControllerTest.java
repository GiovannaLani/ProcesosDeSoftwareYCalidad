package com.spq.vinted.controller;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;


import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import com.spq.vinted.dto.*;
import com.spq.vinted.model.*;
import com.spq.vinted.service.ItemService;
import com.spq.vinted.service.UserService;

class ItemControllerTest {

    private ItemService itemService;
    private UserService userService;
    private ItemController itemController;
    private User user;

    @BeforeEach
    void setUp() {
        itemService = mock(ItemService.class);
        userService = mock(UserService.class);
        itemController = new ItemController(itemService, userService);
        user = new User();
        user.setId(1L);
    }

    @Test
    void AddItemToCartTest() {
        assertEquals(HttpStatus.OK, itemController.addItemToCart(1L, 2L).getStatusCode());
        doThrow(RuntimeException.class).when(itemService).addItemToCart(1L, 2L);
        assertEquals(HttpStatus.BAD_REQUEST, itemController.addItemToCart(1L, 2L).getStatusCode());
    }

    @Test
    void GetCartTest() {
        List<Item> items = List.of(new Clothes("camiseta", "camiseta a rayas", 10, ClothesSize.M, ClothesType.TSHIRT, Category.WOMAN, user));
        when(itemService.getCartItems(1L)).thenReturn(items);
        ResponseEntity<List<ItemDTO>> response = itemController.getCart(1L);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());

        when(itemService.getCartItems(1L)).thenThrow(RuntimeException.class);
        assertEquals(HttpStatus.BAD_REQUEST, itemController.getCart(1L).getStatusCode());
    }

    @Test
    void DeleteItemFromCartTest() {
        assertEquals(HttpStatus.OK, itemController.deleteItemFromCart(1L, 2L).getStatusCode());
        doThrow(RuntimeException.class).when(itemService).removeItemFromCart(1L, 2L);
        assertEquals(HttpStatus.BAD_REQUEST, itemController.deleteItemFromCart(1L, 2L).getStatusCode());
    }

    @Test
    void UploadItemDataTest() {
        ClothesDTO clothesDTO = new ClothesDTO();
        clothesDTO.setTitle("title");
        clothesDTO.setDescription("desc");
        clothesDTO.setPrice(20);
        clothesDTO.setSize(ClothesSize.M);
        clothesDTO.setClothesType(ClothesType.TSHIRT);
        clothesDTO.setCategory(Category.WOMAN);

        when(userService.getUserByToken(1L)).thenReturn(user);
        when(itemService.saveItem(any(Clothes.class))).thenReturn(new Clothes("camiseta", "camiseta lisa", 40, ClothesSize.M, ClothesType.TSHIRT, Category.WOMAN, user));
        assertEquals(HttpStatus.OK, itemController.uploadItemData(1L, clothesDTO).getStatusCode());

        when(userService.getUserByToken(1L)).thenReturn(null);
        assertEquals(HttpStatus.FORBIDDEN, itemController.uploadItemData(1L, clothesDTO).getStatusCode());
    }

    @Test
    void UpdateItemImageTest() throws IOException {
        List<MultipartFile> images = List.of(new MockMultipartFile("file", "filename.jpg", "image/jpeg", "test".getBytes()));
        assertEquals(HttpStatus.NO_CONTENT, itemController.updateItemImage(1L, images).getStatusCode());

        doThrow(new IOException()).when(itemService).uploadItemImages(anyLong(), anyList());
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, itemController.updateItemImage(1L, images).getStatusCode());

        doThrow(new RuntimeException("Item not found")).when(itemService).uploadItemImages(anyLong(), anyList());
        assertEquals(HttpStatus.NOT_FOUND, itemController.updateItemImage(1L, images).getStatusCode());
    }

    @Test
    void GetItemsTest() {
        when(itemService.getItems(null)).thenReturn(new ArrayList<>());
        assertEquals(HttpStatus.OK, itemController.getItems(null).getStatusCode());
    }

    @Test
    void GetItemByIdTest() {
        when(itemService.getItemById(1L)).thenReturn(new Clothes("camiseta", "camiseta azul", 20, ClothesSize.M, ClothesType.TSHIRT, Category.WOMAN, user));
        assertEquals(HttpStatus.OK, itemController.getItemById(1L).getStatusCode());

        when(itemService.getItemById(2L)).thenThrow(RuntimeException.class);
        assertEquals(HttpStatus.BAD_REQUEST, itemController.getItemById(2L).getStatusCode());
    }

    @Test
    void GetClothesTest() {
        when(itemService.getClothes()).thenReturn(new ArrayList<>());
        assertEquals(HttpStatus.OK, itemController.getClothes(null).getStatusCode());
    }

    @Test
    void GetClothesByCategoryTest() {
        when(itemService.getClothesByCategory(Category.WOMAN)).thenReturn(new ArrayList<>());
        assertEquals(HttpStatus.OK, itemController.getClothesByCategory(Category.WOMAN, null).getStatusCode());
    }

    @Test
    void GetElectronicsTest() {
        when(itemService.getElectronics()).thenReturn(new ArrayList<>());
        assertEquals(HttpStatus.OK, itemController.getElectronics(null).getStatusCode());
    }

    @Test
    void GetHomeItemsTest() {
        when(itemService.getHomeItems()).thenReturn(new ArrayList<>());
        assertEquals(HttpStatus.OK, itemController.getHomeItems(null).getStatusCode());
    }

    @Test
    void GetItemsForPetTest() {
        when(itemService.getItemsForPet()).thenReturn(new ArrayList<>());
        assertEquals(HttpStatus.OK, itemController.getItemsForPet(null).getStatusCode());
    }

    @Test
    void GetItemsforEntertainmentTest() {
        when(itemService.getItemsforEntertainment()).thenReturn(new ArrayList<>());
        assertEquals(HttpStatus.OK, itemController.getItemsforEntertainment(null).getStatusCode());
    }

    @Test
    void ShowImagenTest() throws Exception {
        ResponseEntity<Resource> response = itemController.showImagen("nonexistent.jpg");
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }
    @Test
    void GetItemOwnerTest() {
        when(itemService.getItemOwner(1L)).thenReturn(user);
        assertEquals(HttpStatus.OK, itemController.getItemOwner(1L).getStatusCode());

        doThrow(RuntimeException.class).when(itemService).getItemOwner(2L);
        assertEquals(HttpStatus.NOT_FOUND, itemController.getItemOwner(2L).getStatusCode());
    }

    @Test
    void GetUserItemsTest() {
        when(itemService.getUserItems(1L)).thenReturn(new ArrayList<>());
        assertEquals(HttpStatus.OK, itemController.getUserItems(1L).getStatusCode());
    }

    @Test
    void GetSellerTest() {
        Item item = mock(Item.class);
        when(itemService.getItemById(1L)).thenReturn(item);
        when(item.getSeller()).thenReturn(user);
        assertEquals(HttpStatus.OK, itemController.getSeller(1L).getStatusCode());
    }

    @Test
    void DeleteItemTest() {
        assertEquals(HttpStatus.NO_CONTENT, itemController.deleteItem(1L, 1L).getStatusCode());

        doThrow(new RuntimeException("Item not found")).when(itemService).deleteItem(1L, 1L);
        assertEquals(HttpStatus.NOT_FOUND, itemController.deleteItem(1L, 1L).getStatusCode());

        doThrow(new RuntimeException("Not authorized")).when(itemService).deleteItem(1L, 2L);
        assertEquals(HttpStatus.FORBIDDEN, itemController.deleteItem(1L, 2L).getStatusCode());
    }

    @Test
    void SearchItemsTest() {
        when(itemService.searchItems(null, "query")).thenReturn(new ArrayList<>());
        assertEquals(HttpStatus.OK, itemController.searchItems(null, "query").getStatusCode());
    }
}
