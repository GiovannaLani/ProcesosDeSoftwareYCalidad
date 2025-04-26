package com.spq.vinted.service;
import com.spq.vinted.dto.ClothesDTO;
import com.spq.vinted.dto.HomeDTO;
import com.spq.vinted.dto.ItemDTO;
import com.spq.vinted.dto.PetDTO;
import com.spq.vinted.model.Clothes;
import com.spq.vinted.model.Home;
import com.spq.vinted.model.Item;
import com.spq.vinted.model.Pet;
import com.spq.vinted.repository.ItemRepository;
import com.spq.vinted.repository.UserRepository;
import com.spq.vinted.service.ItemService;
import com.spq.vinted.service.UserService;
import com.spq.vinted.controller.ItemController;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class ItemServiceTest {

    @Mock
    private ItemRepository itemRepository;

    @Mock
    private UserService userService;

    @InjectMocks
    private ItemService itemService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }
    
    @Test
    void testGetItems(){
        Item item1 = new Clothes();
        item1.setTitle("Item 1");
        item1.setDescription("Description 1");
        item1.setPrice(10.0f);

        Item item2 = new Home();
        item2.setTitle("Item 2");
        item2.setDescription("Description 2");
        item2.setPrice(20.0f);

        List<Item> items = Arrays.asList(item1, item2);

        when(itemRepository.findAll()).thenReturn(items);

        List<Item> result = itemService.getItems(null);

        assertEquals(2, result.size());
        assertEquals("Item 1", result.get(0).getTitle());
        assertEquals("Item 2", result.get(1).getTitle());


    }


    // @Test
    // void testGetItemById() throws Exception {
    //     // Mock de datos
    //     ItemDTO item = new ItemDTO();
    //     item.setTitle("Item 1");
    //     item.setDescription("Description 1");
    //     item.setPrice(10.0f);

    //     // Mock del servicio
    //     Mockito.when(itemService.getItemById(1L)).thenReturn(new Item());

    //     // Realiza la solicitud GET
    //     mockMvc.perform(get("/items/item/1")
    //                     .contentType(MediaType.APPLICATION_JSON))
    //             .andExpect(status().isOk())
    //             .andExpect(jsonPath("$.title").value("Item 1"))
    //             .andExpect(jsonPath("$.description").value("Description 1"))
    //             .andExpect(jsonPath("$.price").value(10.0));
    // }

    // @Test
    // void testSearchItems() throws Exception {
    //     // Mock de datos
    //     ItemDTO item1 = new ItemDTO();
    //     item1.setTitle("Item 1");
    //     item1.setDescription("Description 1");
    //     item1.setPrice(10.0f);

    //     ItemDTO item2 = new ItemDTO();
    //     item2.setTitle("Item 2");
    //     item2.setDescription("Description 2");
    //     item2.setPrice(20.0f);

    //     List<ItemDTO> mockItems = Arrays.asList(item1, item2);

    //     // Mock del servicio
    //     Mockito.when(itemService.searchItems(any(Long.class), eq("query")))
    //             .thenReturn(Arrays.asList(new Item(), new Item()));

    //     // Realiza la solicitud GET
    //     mockMvc.perform(get("/items/search")
    //                     .param("search_text", "query")
    //                     .param("token", "12345")
    //                     .contentType(MediaType.APPLICATION_JSON))
    //             .andExpect(status().isOk())
    //             .andExpect(jsonPath("$[0].title").value("Item 1"))
    //             .andExpect(jsonPath("$[1].title").value("Item 2"));
    // }

    // @Test
    // void testAddItemToCart() throws Exception {
    //     // Mock del servicio
    //     Mockito.doNothing().when(itemService).addItemToCart(12345L, 1L);

    //     // Realiza la solicitud POST
    //     mockMvc.perform(post("/items/shoppingCart/add")
    //                     .param("token", "12345")
    //                     .param("itemId", "1")
    //                     .contentType(MediaType.APPLICATION_JSON))
    //             .andExpect(status().isOk());
    // }

    // @Test
    // void testDeleteItemFromCart() throws Exception {
    //     // Mock del servicio
    //     Mockito.doNothing().when(itemService).removeItemFromCart(12345L, 1L);

    //     // Realiza la solicitud POST
    //     mockMvc.perform(post("/items/shoppingCart/remove")
    //                     .param("token", "12345")
    //                     .param("itemId", "1")
    //                     .contentType(MediaType.APPLICATION_JSON))
    //             .andExpect(status().isOk());
    // }

    // @Test
    // void testGetCart() throws Exception {
    //     // Mock de datos
    //     ItemDTO item1 = new ItemDTO();
    //     item1.setTitle("Item 1");
    //     item1.setDescription("Description 1");
    //     item1.setPrice(10.0f);

    //     List<ItemDTO> mockCartItems = Arrays.asList(item1);

    //     // Mock del servicio
    //     Mockito.when(itemService.getCartItems(12345L)).thenReturn(Arrays.asList(new Item()));

    //     // Realiza la solicitud GET
    //     mockMvc.perform(get("/items/shoppingCart")
    //                     .param("token", "12345")
    //                     .contentType(MediaType.APPLICATION_JSON))
    //             .andExpect(status().isOk())
    //             .andExpect(jsonPath("$[0].title").value("Item 1"));
    // }

    // @Test
    // void testDeleteItem() throws Exception {
    //     // Mock del servicio
    //     Mockito.doNothing().when(itemService).deleteItem(12345L, 1L);

    //     // Realiza la solicitud DELETE
    //     mockMvc.perform(delete("/items/delete/1")
    //                     .param("token", "12345")
    //                     .contentType(MediaType.APPLICATION_JSON))
    //             .andExpect(status().isNoContent());
    // }

    // @Test
    // void testUploadItemData() throws Exception {
    
    //     ItemDTO itemDTO = new ItemDTO();
    //     itemDTO.setTitle("Item 1");
    //     itemDTO.setDescription("Description 1");
    //     itemDTO.setPrice(10.0f);

    //     // Mock del servicio
    //     Mockito.when(userService.getUserByToken(12345L)).thenReturn(new User());
    //     Mockito.when(itemService.saveItem(any(Item.class))).thenReturn(new Item());

    //     // Realiza la solicitud POST
    //     mockMvc.perform(post("/items/itemData")
    //                 .param("token", "12345")
    //                 .contentType(MediaType.APPLICATION_JSON)
    //                 .content("""
    //                     {
    //                         "title": "Item 1",
    //                         "description": "Description 1",
    //                         "price": 10.0
    //                     }
    //                 """))
    //         .andExpect(status().isOk());
    // }

    // @Test
    // void testUpdateItemImage() throws Exception {
    //     // Mock del servicio
    //     Mockito.doNothing().when(itemService).uploadItemImages(eq(1L), any(List.class));

    //     // Realiza la solicitud PUT
    //     mockMvc.perform(multipart("/items/itemImage")
    //                 .file("images", "image-content".getBytes())
    //                 .param("itemId", "1")
    //                 .contentType(MediaType.MULTIPART_FORM_DATA))
    //         .andExpect(status().isNoContent());
    // }

    // @Test
    // void testGetClothes() throws Exception {
    //     // Mock de datos
    //     ClothesDTO clothesDTO = new ClothesDTO();
    //     clothesDTO.setTitle("Clothes 1");
    //     clothesDTO.setDescription("Description 1");
    //     clothesDTO.setPrice(15.0f);

    //     List<ClothesDTO> mockClothes = Arrays.asList(clothesDTO);

    //     // Mock del servicio
    //     Mockito.when(itemService.getClothes()).thenReturn(Arrays.asList(new Clothes()));

    //     // Realiza la solicitud GET
    //     mockMvc.perform(get("/items/clothes")
    //                     .param("token", "12345")
    //                     .contentType(MediaType.APPLICATION_JSON))
    //             .andExpect(status().isOk())
    //             .andExpect(jsonPath("$[0].title").value("Clothes 1"));
    // }

    // @Test
    // void testGetClothesByCategory() throws Exception {
    //     // Mock de datos
    //     ClothesDTO clothesDTO = new ClothesDTO();
    //     clothesDTO.setTitle("Clothes 1");
    //     clothesDTO.setDescription("Description 1");
    //     clothesDTO.setPrice(15.0f);

    //     List<ClothesDTO> mockClothes = Arrays.asList(clothesDTO);

    //     // Mock del servicio
    //     Mockito.when(itemService.getClothesByCategory("Men")).thenReturn(Arrays.asList(new Clothes()));

    //     // Realiza la solicitud GET
    //     mockMvc.perform(get("/items/clothes/Men")
    //                     .contentType(MediaType.APPLICATION_JSON))
    //             .andExpect(status().isOk())
    //             .andExpect(jsonPath("$[0].title").value("Clothes 1"));
    // }

    // @Test
    // void testGetHomeItems() throws Exception {
    //     // Mock de datos
    //     HomeDTO homeDTO = new HomeDTO();
    //     homeDTO.setTitle("Home Item 1");
    //     homeDTO.setDescription("Description 1");
    //     homeDTO.setPrice(50.0f);

    //     List<HomeDTO> mockHomeItems = Arrays.asList(homeDTO);

    //     // Mock del servicio
    //     Mockito.when(itemService.getHomeItems()).thenReturn(Arrays.asList(new Home()));

    //     // Realiza la solicitud GET
    //     mockMvc.perform(get("/items/home")
    //                 .contentType(MediaType.APPLICATION_JSON))
    //         .andExpect(status().isOk())
    //         .andExpect(jsonPath("$[0].title").value("Home Item 1"));
    // }

    // @Test
    // void testGetItemsForPet() throws Exception {
    //     // Mock de datos
    //     PetDTO petDTO = new PetDTO();
    //     petDTO.setTitle("Pet Item 1");
    //     petDTO.setDescription("Description 1");
    //     petDTO.setPrice(20.0f);

    //     List<PetDTO> mockPetItems = Arrays.asList(petDTO);

    //     // Mock del servicio
    //     Mockito.when(itemService.getItemsForPet()).thenReturn(Arrays.asList(new Pet()));

    //     // Realiza la solicitud GET
    //     mockMvc.perform(get("/items/pet")
    //                 .contentType(MediaType.APPLICATION_JSON))
    //         .andExpect(status().isOk())
    //         .andExpect(jsonPath("$[0].title").value("Pet Item 1"));
    // }
}
