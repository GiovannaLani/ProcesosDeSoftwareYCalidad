package com.spq.vinted.controller;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
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
    void addItemToCartTest() {
        assertEquals(HttpStatus.OK, itemController.addItemToCart(1L, 2L).getStatusCode());
        doThrow(RuntimeException.class).when(itemService).addItemToCart(1L, 2L);
        assertEquals(HttpStatus.BAD_REQUEST, itemController.addItemToCart(1L, 2L).getStatusCode());
    }

    @Test
    void deleteItemFromCartTest() {
        assertEquals(HttpStatus.OK, itemController.deleteItemFromCart(1L, 2L).getStatusCode());
        doThrow(RuntimeException.class).when(itemService).removeItemFromCart(1L, 2L);
        assertEquals(HttpStatus.BAD_REQUEST, itemController.deleteItemFromCart(1L, 2L).getStatusCode());
    }

    @Test
    void uploadItemDataTest() {
        when(userService.getUserByToken(1L)).thenReturn(user);

        ClothesDTO clothesDTO = new ClothesDTO(0, "Camisa", "Bonita", 20, ClothesSize.M, ClothesType.TSHIRT, Category.WOMAN, null);
        when(itemService.saveItem(any(Clothes.class))).thenReturn(new Clothes());
        assertEquals(HttpStatus.OK, itemController.uploadItemData(1L, clothesDTO).getStatusCode());

        ElectronicsDTO electronicsDTO = new ElectronicsDTO(0, "Laptop", "Potente", 1000, ElectronicsType.DEVICE, null);
        when(itemService.saveItem(any(Electronics.class))).thenReturn(new Electronics());
        assertEquals(HttpStatus.OK, itemController.uploadItemData(1L, electronicsDTO).getStatusCode());

        PetDTO petDTO = new PetDTO(0, "Collar", "Para perro", 10, Species.DOG, null);
        when(itemService.saveItem(any(Pet.class))).thenReturn(new Pet());
        assertEquals(HttpStatus.OK, itemController.uploadItemData(1L, petDTO).getStatusCode());

        HomeDTO homeDTO = new HomeDTO(0, "Sofa", "Comodo", 500, HomeType.FURNITURE, null);
        when(itemService.saveItem(any(Home.class))).thenReturn(new Home());
        assertEquals(HttpStatus.OK, itemController.uploadItemData(1L, homeDTO).getStatusCode());

        EntertainmentDTO entertainmentDTO = new EntertainmentDTO(0, "Juego", "Divertido", 30, EntertainmentType.GAME, null);
        when(itemService.saveItem(any(Entertainment.class))).thenReturn(new Entertainment());
        assertEquals(HttpStatus.OK, itemController.uploadItemData(1L, entertainmentDTO).getStatusCode());

        when(itemService.saveItem(any(Item.class))).thenReturn(null);
        assertEquals(HttpStatus.BAD_REQUEST, itemController.uploadItemData(1L, clothesDTO).getStatusCode());

        when(userService.getUserByToken(1L)).thenReturn(null);
        assertEquals(HttpStatus.FORBIDDEN, itemController.uploadItemData(1L, clothesDTO).getStatusCode());
    }

    @Test
    void updateItemImageTest() throws IOException {
        List<MultipartFile> images = List.of(new MockMultipartFile("file", "filename.jpg", "image/jpeg", "test".getBytes()));
        assertEquals(HttpStatus.NO_CONTENT, itemController.updateItemImage(1L, images).getStatusCode());

        doThrow(new IOException()).when(itemService).uploadItemImages(anyLong(), anyList());
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, itemController.updateItemImage(1L, images).getStatusCode());

        doThrow(new RuntimeException("Item not found")).when(itemService).uploadItemImages(anyLong(), anyList());
        assertEquals(HttpStatus.NOT_FOUND, itemController.updateItemImage(1L, images).getStatusCode());

        doThrow(new RuntimeException("Unexpected error")).when(itemService).uploadItemImages(anyLong(), anyList());
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, itemController.updateItemImage(1L, images).getStatusCode());
    }

    @Test
    void getCartTest() {
        when(itemService.getCartItems(1L)).thenReturn(new ArrayList<>());
        assertEquals(HttpStatus.OK, itemController.getCart(1L).getStatusCode());

        when(itemService.getCartItems(1L)).thenThrow(RuntimeException.class);
        assertEquals(HttpStatus.BAD_REQUEST, itemController.getCart(1L).getStatusCode());
    }

    @Test
    void getItemsTest() {
        when(itemService.getItems(null)).thenReturn(new ArrayList<>());
        assertEquals(HttpStatus.OK, itemController.getItems(null).getStatusCode());

        when(itemService.getItems(1L)).thenReturn(new ArrayList<>());
        assertEquals(HttpStatus.OK, itemController.getItems(1L).getStatusCode());

        when(itemService.getItems(2L)).thenThrow(RuntimeException.class);
        assertEquals(HttpStatus.BAD_REQUEST, itemController.getItems(2L).getStatusCode());
    }

    @Test
    void getItemByIdTest() {
        when(itemService.getItemById(1L)).thenReturn(new Clothes());
        assertEquals(HttpStatus.OK, itemController.getItemById(1L).getStatusCode());

        when(itemService.getItemById(2L)).thenThrow(RuntimeException.class);
        assertEquals(HttpStatus.BAD_REQUEST, itemController.getItemById(2L).getStatusCode());
    }

    @Test
    void getClothesTest() {
        when(itemService.getClothes()).thenReturn(new ArrayList<>());
        assertEquals(HttpStatus.OK, itemController.getClothes(null).getStatusCode());
    }

    @Test
    void getElectronicsTest() {
        when(itemService.getElectronics()).thenReturn(new ArrayList<>());
        assertEquals(HttpStatus.OK, itemController.getElectronics(null).getStatusCode());
    }

    @Test
    void getHomeItemsTest() {
        when(itemService.getHomeItems()).thenReturn(new ArrayList<>());
        assertEquals(HttpStatus.OK, itemController.getHomeItems(null).getStatusCode());
    }

    @Test
    void getItemsForPetTest() {
        when(itemService.getItemsForPet()).thenReturn(new ArrayList<>());
        assertEquals(HttpStatus.OK, itemController.getItemsForPet(null).getStatusCode());
    }

    @Test
    void getItemsforEntertainmentTest() {
        when(itemService.getItemsforEntertainment()).thenReturn(new ArrayList<>());
        assertEquals(HttpStatus.OK, itemController.getItemsforEntertainment(null).getStatusCode());
    }

    @Test
    void showImagenTest() throws Exception {
        Path path = Paths.get("uploads/items/test.jpg");
        Files.createDirectories(path.getParent());
        Files.writeString(path, "fake image content");

        ResponseEntity<Resource> response = itemController.showImagen("test.jpg");
        assertEquals(HttpStatus.OK, response.getStatusCode());

        Files.deleteIfExists(path);

        ResponseEntity<Resource> responseNotFound = itemController.showImagen("nonexistent.jpg");
        assertEquals(HttpStatus.NOT_FOUND, responseNotFound.getStatusCode());
    }

    @Test
    void getItemOwnerTest() {
        when(itemService.getItemOwner(1L)).thenReturn(user);
        assertEquals(HttpStatus.OK, itemController.getItemOwner(1L).getStatusCode());

        doThrow(RuntimeException.class).when(itemService).getItemOwner(2L);
        assertEquals(HttpStatus.NOT_FOUND, itemController.getItemOwner(2L).getStatusCode());
    }

    @Test
    void getUserItemsTest() {
        when(itemService.getUserItems(1L)).thenReturn(new ArrayList<>());
        assertEquals(HttpStatus.OK, itemController.getUserItems(1L).getStatusCode());
    }

    @Test
    void getSellerTest() {
        Item item = mock(Item.class);
        when(itemService.getItemById(1L)).thenReturn(item);
        when(item.getSeller()).thenReturn(user);
        assertEquals(HttpStatus.OK, itemController.getSeller(1L).getStatusCode());
    }

    @Test
    void deleteItemTest() {
        assertEquals(HttpStatus.NO_CONTENT, itemController.deleteItem(1L, 1L).getStatusCode());

        doThrow(new RuntimeException("Item not found")).when(itemService).deleteItem(1L, 1L);
        assertEquals(HttpStatus.NOT_FOUND, itemController.deleteItem(1L, 1L).getStatusCode());

        doThrow(new RuntimeException("Not authorized")).when(itemService).deleteItem(1L, 2L);
        assertEquals(HttpStatus.FORBIDDEN, itemController.deleteItem(1L, 2L).getStatusCode());

        doThrow(new RuntimeException("Unexpected"));
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, itemController.deleteItem(1L, 3L).getStatusCode());
    }

    @Test
    void searchItemsTest() {
        when(itemService.searchItems(null, "query")).thenReturn(new ArrayList<>());
        assertEquals(HttpStatus.OK, itemController.searchItems(null, "query").getStatusCode());

        when(itemService.searchItems(null, "badquery")).thenThrow(RuntimeException.class);
        assertEquals(HttpStatus.BAD_REQUEST, itemController.searchItems(null, "badquery").getStatusCode());
    }
}
