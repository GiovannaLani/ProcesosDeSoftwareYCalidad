package com.spq.vinted.service;
import com.spq.vinted.dto.ClothesDTO;
import com.spq.vinted.dto.HomeDTO;
import com.spq.vinted.dto.ItemDTO;
import com.spq.vinted.dto.PetDTO;
import com.spq.vinted.model.Clothes;
import com.spq.vinted.model.Electronics;
import com.spq.vinted.model.Entertainment;
import com.spq.vinted.model.Home;
import com.spq.vinted.model.Item;
import com.spq.vinted.model.Pet;
import com.spq.vinted.model.User;
import com.spq.vinted.repository.ItemRepository;
import com.spq.vinted.repository.UserRepository;
import com.spq.vinted.service.ItemService;
import com.spq.vinted.service.UserService;
import com.spq.vinted.controller.ItemController;
import com.spq.vinted.model.User;
import com.spq.vinted.model.Category;

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

        // User user = new User();
        // user.setId(12345L);

        // when(userService.getUserByToken(12345L)).thenReturn(user);

        // // Asignamos seller a un item para probar el filtro
        // item2.setSeller(user);  // el item2 lo vende el mismo user

        // List<Item> resultWithToken = itemService.getItems(12345L);

        // assertEquals(1, resultWithToken.size());
        // assertEquals("Item 1", resultWithToken.get(0).getTitle());
    }


    @Test
    void testGetItemById() throws Exception {
        Item item = new Clothes();
        item.setId(1L);
        item.setTitle("Item 1");
        item.setDescription("Description 1");
        item.setPrice(10.0f);

        when(itemRepository.findById(1L)).thenReturn(Optional.of(item));

        Item result = itemService.getItemById(1L);

        assertEquals("Item 1", result.getTitle());
        assertEquals("Description 1", result.getDescription());
        assertEquals(10.0f, result.getPrice());
    }

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

    @Test
    void testGetClothesByCategory() throws Exception {
        Clothes item1 = new Clothes();
        item1.setTitle("Clothes Item 1");
        item1.setDescription("Description 1");
        item1.setPrice(15.0f);
        item1.setCategory(Category.MAN); // Usamos el enum Category

        Clothes item2 = new Clothes();
        item2.setTitle("Clothes Item 2");
        item2.setDescription("Description 2");
        item2.setPrice(25.0f);
        item2.setCategory(Category.MAN); // Usamos el enum Category

        Clothes item3 = new Clothes();
        item3.setTitle("Clothes Item 3");
        item3.setDescription("Description 3");
        item3.setPrice(30.0f);
        item3.setCategory(Category.WOMAN); // Usamos el enum Category

        // Lista de ítems que incluye ítems de diferentes categorías
        List<Item> clothesItems = Arrays.asList(item1, item2, item3);

        // Mock del repositorio para devolver la lista de ítems
        when(itemRepository.findAll()).thenReturn(clothesItems);

        // Llamada al método del servicio
        List<Clothes> result = itemService.getClothesByCategory(Category.MAN);

        // Verificaciones
        assertEquals(2, result.size()); // Solo los ítems de categoría "MAN" deben estar en el resultado
        assertEquals("Clothes Item 1", result.get(0).getTitle());
        assertEquals("Clothes Item 2", result.get(1).getTitle());
    }

    @Test
    void testGetItemsForClothes() throws Exception {
        Clothes item1 = new Clothes();
        item1.setTitle("Clothes Item 1");
        item1.setDescription("Description 1");
        item1.setPrice(15.0f);

        Clothes item2 = new Clothes();
        item2.setTitle("Clothes Item 2");
        item2.setDescription("Description 2");
        item2.setPrice(25.0f);

        List<Item> clothesItems = Arrays.asList(item1, item2);

        when(itemRepository.findAll()).thenReturn(clothesItems);

        List<Clothes> result = itemService.getClothes();

        assertEquals(2, result.size());
        assertEquals("Clothes Item 1", result.get(0).getTitle());
        assertEquals("Clothes Item 2", result.get(1).getTitle());
    }

    @Test
    void testGetItemsForElectronics() throws Exception {
        Electronics item1 = new Electronics();
        item1.setTitle("Electronics Item 1");
        item1.setDescription("Description 1");
        item1.setPrice(50.0f);

        Electronics item2 = new Electronics();
        item2.setTitle("Electronics Item 2");
        item2.setDescription("Description 2");
        item2.setPrice(60.0f);

        List<Item> electronicsItems = Arrays.asList(item1, item2);

        when(itemRepository.findAll()).thenReturn(electronicsItems);

        List<Electronics> result = itemService.getElectronics();

        assertEquals(2, result.size());
        assertEquals("Electronics Item 1", result.get(0).getTitle());
        assertEquals("Electronics Item 2", result.get(1).getTitle());
    }

    @Test
    void testGetHomeItems() throws Exception {
        Home item1 = new Home();
        item1.setTitle("Pet Item 1");
        item1.setDescription("Description 1");
        item1.setPrice(15.0f);

        Home item2 = new Home();
        item2.setTitle("Pet Item 2");
        item2.setDescription("Description 2");
        item2.setPrice(25.0f);

        List<Item> homeItems = Arrays.asList(item1, item2);

        when(itemRepository.findAll()).thenReturn(homeItems);
      
        List<Home> result = itemService.getHomeItems();

        assertEquals(2, result.size());
        assertEquals("Pet Item 1", result.get(0).getTitle());
        assertEquals("Pet Item 2", result.get(1).getTitle());
    }

    @Test
    void testGetItemsForPet() throws Exception {
        Pet item1 = new Pet();
        item1.setTitle("Pet Item 1");
        item1.setDescription("Description 1");
        item1.setPrice(15.0f);

        Pet item2 = new Pet();
        item2.setTitle("Pet Item 2");
        item2.setDescription("Description 2");
        item2.setPrice(25.0f);

        List<Item> petItems = Arrays.asList(item1, item2);

        when(itemRepository.findAll()).thenReturn(petItems);
      
        List<Pet> result = itemService.getItemsForPet();

        assertEquals(2, result.size());
        assertEquals("Pet Item 1", result.get(0).getTitle());
        assertEquals("Pet Item 2", result.get(1).getTitle());
    }

    @Test
    void testGetItemsForEntertainment() throws Exception {
        // Mock de datos
        Entertainment item1 = new Entertainment();
        item1.setTitle("Entertainment Item 1");
        item1.setDescription("Description 1");
        item1.setPrice(20.0f);

        Entertainment item2 = new Entertainment();
        item2.setTitle("Entertainment Item 2");
        item2.setDescription("Description 2");
        item2.setPrice(30.0f);

        // Lista de ítems que incluye solo ítems de tipo Entertainment
        List<Item> entertainmentItems = Arrays.asList(item1, item2);

        // Mock del repositorio para devolver la lista de ítems
        when(itemRepository.findAll()).thenReturn(entertainmentItems);

        // Llamada al método del servicio
        List<Entertainment> result = itemService.getItemsforEntertainment();

        // Verificaciones
        assertEquals(2, result.size()); // Solo los ítems de tipo Entertainment deben estar en el resultado
        assertEquals("Entertainment Item 1", result.get(0).getTitle());
        assertEquals("Entertainment Item 2", result.get(1).getTitle());
    }
}
