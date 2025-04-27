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
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


class ItemServiceTest {

    @Mock
    private ItemRepository itemRepository;

    @Mock
    private UserRepository userRepository;

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
        
        Item item3 = new Clothes();
        item3.setTitle("Item 3");
        item3.setDescription("Description 3");
        item3.setPrice(10.0f);

        Item item4 = new Home();
        item4.setTitle("Item 4");
        item4.setDescription("Description 4");
        item4.setPrice(20.0f);

        User user = new User();
        user.setId(12345L);
        when(userService.getUserByToken(12345L)).thenReturn(user);

        List<Item> itemsToken = Arrays.asList(item3, item4);
        when(itemRepository.findBySellerIdNot(12345L)).thenReturn(itemsToken);

        when(itemRepository.findAll()).thenReturn(itemsToken);

        List<Item> resultWithToken = itemService.getItems(12345L);

        assertEquals(2, resultWithToken.size());
        assertEquals("Item 3", resultWithToken.get(0).getTitle());
        assertEquals("Item 4", resultWithToken.get(1).getTitle());

        when(userService.getUserByToken(23456L)).thenThrow(new RuntimeException("User not found"));

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
        itemService.getItems(23456L);
        });

        assertEquals("Failed to fetch items: User not found", exception.getMessage());
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

   
    @Test
    void testSearchItems() throws Exception {
        User user = new User();
        user.setId(123L);

        Item item1 = new Clothes();
        item1.setTitle("Clothes Item 1");
        item1.setDescription("Description 1");
        item1.setPrice(15.0f);

        Item item2 = new Electronics();
        item2.setTitle("Electronics Item 1");
        item2.setDescription("Description 2");
        item2.setPrice(50.0f);

        List<Item> allItems = Arrays.asList(item1, item2);

        when(userService.getUserByToken(123L)).thenReturn(user);

        when(itemRepository.findAll()).thenReturn(allItems);

        when(itemRepository.findBySellerIdNot(123L)).thenReturn(allItems);

        List<Item> result = itemService.searchItems(123L, "Item");

        assertEquals(2, result.size());
        assertEquals("Clothes Item 1", result.get(0).getTitle());
        assertEquals("Electronics Item 1", result.get(1).getTitle());

        List<Item> resultWithNullQuery = itemService.searchItems(123L, null);

        assertEquals(2, resultWithNullQuery.size());
        assertEquals("Clothes Item 1", resultWithNullQuery.get(0).getTitle());
        assertEquals("Electronics Item 1", resultWithNullQuery.get(1).getTitle());

        List<Item> resultWithBlankQuery = itemService.searchItems(123L, "   ");

        assertEquals(2, resultWithBlankQuery.size());
        assertEquals("Clothes Item 1", resultWithBlankQuery.get(0).getTitle());
        assertEquals("Electronics Item 1", resultWithBlankQuery.get(1).getTitle());
    }


    @Test
    void testAddItemToCart() {

        User user = new User();
        user.setId(123L);
        user.setCartItems(new ArrayList<>()); 

        Item item = new Clothes();
        item.setId(456L);
        item.setUsersWithItemInCart(new ArrayList<>());

        when(userService.getUserByToken(123L)).thenReturn(user);
        when(itemRepository.findById(456L)).thenReturn(Optional.of(item));
        when(userRepository.findById(String.valueOf(user.getId()))).thenReturn(Optional.of(user));

        itemService.addItemToCart(123L, 456L);

        assertTrue(user.getCartItems().contains(item), "El item debería estar en el carrito del usuario");
        assertTrue(item.getUsersWithItemInCart().contains(user), "El usuario debería estar en la lista de usuarios del item");

        verify(userRepository, times(1)).save(user);
        verify(itemRepository, times(1)).save(item);

    }

    @Test
    void testGetCartItems() {
        User user = new User();
        user.setId(123L);
        user.setCartItems(new ArrayList<>());
    
        Item item1 = new Clothes();
        item1.setId(1L);
        item1.setTitle("Clothes Item 1");
        item1.setDescription("Description 1");
        item1.setPrice(15.0f);
    
        Item item2 = new Electronics();
        item2.setId(2L);
        item2.setTitle("Electronics Item 1");
        item2.setDescription("Description 2");
        item2.setPrice(50.0f);

        user.getCartItems().add(item1);
        user.getCartItems().add(item2);

        when(userService.getUserByToken(123L)).thenReturn(user);
        when(userRepository.findById(String.valueOf(123L))).thenReturn(Optional.of(user));
    
        List<Item> result = itemService.getCartItems(123L);
    
        assertEquals(2, result.size(), "El carrito debería contener 2 ítems");
        assertEquals("Clothes Item 1", result.get(0).getTitle());
        assertEquals("Electronics Item 1", result.get(1).getTitle());

        when(userService.getUserByToken(456L)).thenReturn(null);
    
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            itemService.getCartItems(456L);
        });
    
        assertEquals("Usuario no encontrado", exception.getMessage());
    }

    @Test
    void testGetClothesByCategory() throws Exception {
        Clothes item1 = new Clothes();
        item1.setTitle("Clothes Item 1");
        item1.setDescription("Description 1");
        item1.setPrice(15.0f);
        item1.setCategory(Category.MAN); 

        Clothes item2 = new Clothes();
        item2.setTitle("Clothes Item 2");
        item2.setDescription("Description 2");
        item2.setPrice(25.0f);
        item2.setCategory(Category.MAN); 

        Clothes item3 = new Clothes();
        item3.setTitle("Clothes Item 3");
        item3.setDescription("Description 3");
        item3.setPrice(30.0f);
        item3.setCategory(Category.WOMAN); 

        List<Item> clothesItems = Arrays.asList(item1, item2, item3);

        when(itemRepository.findAll()).thenReturn(clothesItems);

        List<Clothes> result = itemService.getClothesByCategory(Category.MAN);

        assertEquals(2, result.size());
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

        List<Item> entertainmentItems = Arrays.asList(item1, item2);

        when(itemRepository.findAll()).thenReturn(entertainmentItems);

        List<Entertainment> result = itemService.getItemsforEntertainment();


        assertEquals(2, result.size());
        assertEquals("Entertainment Item 1", result.get(0).getTitle());
        assertEquals("Entertainment Item 2", result.get(1).getTitle());
    }

    @Test
    void testRemoveItemFromCart() {
        User user = new User();
        user.setId(123L);
        user.setCartItems(new ArrayList<>());
    
        Item item1 = new Clothes();
        item1.setId(456L);
        item1.setUsersWithItemInCart(new ArrayList<>());
    
        Item item2 = new Electronics();
        item2.setId(789L);
        item2.setUsersWithItemInCart(new ArrayList<>());
    
        user.getCartItems().add(item1);
        user.getCartItems().add(item2);
        item1.getUsersWithItemInCart().add(user);
        item2.getUsersWithItemInCart().add(user);
    
        when(userService.getUserByToken(123L)).thenReturn(user);
        when(userRepository.findById("123")).thenReturn(Optional.of(user));
    
        itemService.removeItemFromCart(123L, 456L);
    
        assertFalse(user.getCartItems().contains(item1), "El item1 debería haberse eliminado del carrito");
        assertTrue(user.getCartItems().contains(item2), "El item2 debería permanecer en el carrito");
        assertFalse(item1.getUsersWithItemInCart().contains(user), "El usuario debería haberse eliminado de la lista del item1");
        assertTrue(item2.getUsersWithItemInCart().contains(user), "El usuario debería permanecer en la lista del item2");
    
        verify(userRepository, times(1)).save(user);
        verify(itemRepository, times(1)).save(item1);
    
        when(userService.getUserByToken(999L)).thenReturn(null);
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            itemService.removeItemFromCart(999L, 456L);
        });
        assertEquals("Usuario no encontrado", exception.getMessage());
    
        User emptyCartUser = new User();
        emptyCartUser.setId(555L);
        emptyCartUser.setCartItems(new ArrayList<>());
        when(userService.getUserByToken(555L)).thenReturn(emptyCartUser);
        when(userRepository.findById("555")).thenReturn(Optional.of(emptyCartUser));
        RuntimeException emptyCartException = assertThrows(RuntimeException.class, () -> {
            itemService.removeItemFromCart(555L, 456L);
        });
        assertEquals("El carrito está vacío.", emptyCartException.getMessage());
    
        when(userService.getUserByToken(123L)).thenReturn(user);
        when(userRepository.findById("123")).thenReturn(Optional.of(user));
        RuntimeException itemNotFoundException = assertThrows(RuntimeException.class, () -> {
            itemService.removeItemFromCart(123L, 999L);
        });
        assertEquals("Artículo no encontrado en el carrito", itemNotFoundException.getMessage());
    }

    @Test
    void testDeleteItem() {
        User user = new User();
        user.setId(1L);
        user.setCartItems(new ArrayList<>());

        User otherUser = new User();
        otherUser.setId(2L);
        otherUser.setCartItems(new ArrayList<>());

        Item item = new Clothes();
        item.setId(100L);
        item.setUsersWithItemInCart(new ArrayList<>(List.of(user, otherUser)));

        user.getCartItems().add(item);
        otherUser.getCartItems().add(item);

        when(userService.getUserByToken(1L)).thenReturn(user);
        when(itemRepository.findById(100L)).thenReturn(Optional.of(item));
        when(userRepository.findById("1")).thenReturn(Optional.of(user));
        when(userRepository.findById("2")).thenReturn(Optional.of(otherUser));

        itemService.deleteItem(1L, 100L);

        assertFalse(user.getCartItems().contains(item), "El item debería haberse eliminado del carrito del owner");
        assertFalse(otherUser.getCartItems().contains(item), "El item debería haberse eliminado del carrito de otros usuarios");
        assertTrue(item.getUsersWithItemInCart().isEmpty(), "La lista de usuarios con el item en el carrito debería estar vacía");

        verify(userRepository, times(1)).save(user);
        verify(userRepository, times(1)).save(otherUser);
        verify(itemRepository, times(1)).save(item);
        verify(itemRepository, times(1)).delete(item);

        when(userService.getUserByToken(999L)).thenReturn(null);
        RuntimeException unauthorizedException = assertThrows(RuntimeException.class, () -> {
            itemService.deleteItem(999L, 100L);
        });
        assertEquals("Not authorized", unauthorizedException.getMessage());

        when(userService.getUserByToken(1L)).thenReturn(user);
        when(itemRepository.findById(404L)).thenReturn(Optional.empty());
        RuntimeException itemNotFoundException = assertThrows(RuntimeException.class, () -> {
            itemService.deleteItem(1L, 404L);
        });
        assertEquals("Item not found", itemNotFoundException.getMessage());
    }

    @Test
    void testGetUserItems() {
        User user = new User();
        user.setId(123L);
    
        Item item1 = new Clothes();
        item1.setId(1L);
        item1.setTitle("Camiseta");
    
        Item item2 = new Electronics();
        item2.setId(2L);
        item2.setTitle("Smartphone");
    
        user.setItemsForSale(new ArrayList<>(List.of(item1, item2)));
    
        when(userRepository.findById("123")).thenReturn(Optional.of(user));

        List<Item> result = itemService.getUserItems(123L);
    
        assertEquals(2, result.size(), "Deberían devolverse 2 items");
        assertEquals("Camiseta", result.get(0).getTitle());
        assertEquals("Smartphone", result.get(1).getTitle());
    
        when(userRepository.findById("999")).thenReturn(Optional.empty());
    
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            itemService.getUserItems(999L);
        });
    
        assertEquals("User not found", exception.getMessage());
    }

    @Test
    void testGetItemOwner() {
        User user = new User();
        user.setId(1L);
        user.setUsername("user1");

        Item item = new Electronics();
        item.setId(100L);
        item.setSeller(user);

        when(itemRepository.findById(100L)).thenReturn(Optional.of(item));

        User result = itemService.getItemOwner(100L);
    
        assertNotNull(result, "Debería devolver el propietario del item");
        assertEquals(1L, result.getId());
        assertEquals("user1", result.getUsername());

        when(itemRepository.findById(999L)).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            itemService.getItemOwner(999L);
        });

        assertEquals("Item not found", exception.getMessage());
    }

    @Test
    void testSaveItem() {
        Item itemToSave = new Clothes();
        itemToSave.setTitle("Camiseta");
        itemToSave.setPrice(19.99f);
        
        Item savedItem = new Clothes();
        savedItem.setId(1L); 
        savedItem.setTitle("Camiseta");
        savedItem.setPrice(19.99f);
        
        when(itemRepository.save(itemToSave)).thenReturn(savedItem);
        
        Item result = itemService.saveItem(itemToSave);
        
        assertNotNull(result, "Debería devolver el item guardado");
        assertEquals(1L, result.getId(), "El ID debería ser el generado");
        assertEquals("Camiseta", result.getTitle(), "El título debería coincidir");
        assertEquals(19.99f, result.getPrice(), 0.001, "El precio debería coincidir");
        
        verify(itemRepository, times(1)).save(itemToSave);
    }

    @Test
    void testUploadItemImages() throws IOException {
        long itemId = 1L;
        Item existingItem = new Clothes();
        existingItem.setId(itemId);
        existingItem.setImages(new ArrayList<>());
        
        MultipartFile file1 = mock(MultipartFile.class);
        MultipartFile file2 = mock(MultipartFile.class);
        
        when(file1.getOriginalFilename()).thenReturn("image1.jpg");
        when(file2.getOriginalFilename()).thenReturn("image2.jpg");
        when(file1.getInputStream()).thenReturn(new ByteArrayInputStream("content1".getBytes()));
        when(file2.getInputStream()).thenReturn(new ByteArrayInputStream("content2".getBytes()));
        
        when(itemRepository.findById(itemId)).thenReturn(Optional.of(existingItem));
        when(itemRepository.save(existingItem)).thenReturn(existingItem);
    
        itemService.uploadItemImages(itemId, List.of(file1, file2));
    
        verify(itemRepository, times(1)).findById(itemId);
        verify(itemRepository, times(1)).save(existingItem);
        

        assertEquals(2, existingItem.getImages().size(), "Deberían haberse añadido 2 imágenes");
        
        assertTrue(existingItem.getImages().get(0).endsWith("_image1.jpg"), 
            "El nombre del archivo 1 debería estar en la lista");
        assertTrue(existingItem.getImages().get(1).endsWith("_image2.jpg"), 
            "El nombre del archivo 2 debería estar en la lista");
    
        assertTrue(existingItem.getImages().get(0).matches(".*[a-f0-9]{8}-([a-f0-9]{4}-){3}[a-f0-9]{12}_.*"),
            "El nombre debería contener un UUID");
        assertTrue(existingItem.getImages().get(1).matches(".*[a-f0-9]{8}-([a-f0-9]{4}-){3}[a-f0-9]{12}_.*"),
            "El nombre debería contener un UUID");
    
        when(itemRepository.findById(999L)).thenReturn(Optional.empty());
        assertThrows(RuntimeException.class, () -> {
            itemService.uploadItemImages(999L, List.of(file1));
        });
    }
}
