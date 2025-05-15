package com.spq.vinted.service;
import com.spq.vinted.dto.ClothesDTO;
import com.spq.vinted.dto.ElectronicsDTO;
import com.spq.vinted.dto.EntertainmentDTO;
import com.spq.vinted.dto.HomeDTO;
import com.spq.vinted.dto.ItemDTO;
import com.spq.vinted.dto.PetDTO;
import com.spq.vinted.model.Clothes;
import com.spq.vinted.model.ClothesSize;
import com.spq.vinted.model.ClothesType;
import com.spq.vinted.model.Electronics;
import com.spq.vinted.model.ElectronicsType;
import com.spq.vinted.model.Entertainment;
import com.spq.vinted.model.EntertainmentType;
import com.spq.vinted.model.Home;
import com.spq.vinted.model.HomeType;
import com.spq.vinted.model.Item;
import com.spq.vinted.model.Pet;
import com.spq.vinted.model.Species;
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
import org.mockito.MockedStatic;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.reset;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.never;
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

        List<Item> result = itemService.getAllItems(null);

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

        List<Item> resultWithToken = itemService.getAllItems(12345L);

        assertEquals(2, resultWithToken.size());
        assertEquals("Item 3", resultWithToken.get(0).getTitle());
        assertEquals("Item 4", resultWithToken.get(1).getTitle());

        when(userService.getUserByToken(23456L)).thenThrow(new RuntimeException("User not found"));

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
        itemService.getAllItems(23456L);
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
        User userSuccess = new User();
        userSuccess.setId(123L);
        userSuccess.setCartItems(new ArrayList<>());
    
        Item itemSuccess = new Clothes();
        itemSuccess.setId(456L);
        itemSuccess.setUsersWithItemInCart(new ArrayList<>());
    
        when(userService.getUserByToken(123L)).thenReturn(userSuccess);
        when(itemRepository.findById(456L)).thenReturn(Optional.of(itemSuccess));
        when(userRepository.findById("123")).thenReturn(Optional.of(userSuccess));
    
        itemService.addItemToCart(123L, 456L);
    
        assertTrue(userSuccess.getCartItems().contains(itemSuccess));
        assertTrue(itemSuccess.getUsersWithItemInCart().contains(userSuccess));
        verify(userRepository, times(1)).save(userSuccess);
        verify(itemRepository, times(1)).save(itemSuccess);
    
        reset(userRepository, itemRepository, userService);
    
        when(userService.getUserByToken(999L)).thenReturn(null);
    
        RuntimeException exUserToken = assertThrows(RuntimeException.class, () -> {
            itemService.addItemToCart(999L, 456L);
        });
        assertEquals("Usuario no encontrado", exUserToken.getMessage());
    
        verify(userRepository, never()).save(any());
        verify(itemRepository, never()).save(any());
    
        reset(userRepository, itemRepository, userService);
    
        User userItemNotFound = new User();
        userItemNotFound.setId(123L);
    
        when(userService.getUserByToken(123L)).thenReturn(userItemNotFound);
        when(itemRepository.findById(789L)).thenReturn(Optional.empty());
    
        RuntimeException exItem = assertThrows(RuntimeException.class, () -> {
            itemService.addItemToCart(123L, 789L);
        });
        assertEquals("Artículo no encontrado", exItem.getMessage());
    
        verify(userRepository, never()).save(any());
        verify(itemRepository, never()).save(any());
    
        reset(userRepository, itemRepository, userService);
    
        User userRepoNotFound = new User();
        userRepoNotFound.setId(123L);
    
        when(userService.getUserByToken(123L)).thenReturn(userRepoNotFound);
        when(itemRepository.findById(456L)).thenReturn(Optional.of(new Clothes()));
        when(userRepository.findById("123")).thenReturn(Optional.empty());
    
        RuntimeException exRepoUser = assertThrows(RuntimeException.class, () -> {
            itemService.addItemToCart(123L, 456L);
        });
        assertEquals("Usuario no encontrado", exRepoUser.getMessage());
    
        verify(userRepository, never()).save(any());
    
        reset(userRepository, itemRepository, userService);
    
    
        User userItemAlreadyInCart = new User();
        userItemAlreadyInCart.setId(123L);
        userItemAlreadyInCart.setCartItems(new ArrayList<>());
    
        Item itemAlreadyInCart = new Clothes();
        itemAlreadyInCart.setId(456L);
        itemAlreadyInCart.setUsersWithItemInCart(new ArrayList<>());
    
        userItemAlreadyInCart.getCartItems().add(itemAlreadyInCart);
        itemAlreadyInCart.getUsersWithItemInCart().add(userItemAlreadyInCart);
    
        when(userService.getUserByToken(123L)).thenReturn(userItemAlreadyInCart);
        when(itemRepository.findById(456L)).thenReturn(Optional.of(itemAlreadyInCart));
        when(userRepository.findById("123")).thenReturn(Optional.of(userItemAlreadyInCart));
    
        itemService.addItemToCart(123L, 456L);
    
        verify(userRepository, never()).save(any());
        verify(itemRepository, never()).save(any());

        // ---------- Caso: Carrito nulo ----------
        // User userNullCart = new User();
        // userNullCart.setId(123L);
        // userNullCart.setCartItems(null);
    
        // Item itemNullCart = new Clothes();
        // itemNullCart.setId(456L);
        // itemNullCart.setUsersWithItemInCart(new ArrayList<>());
    
        // when(userService.getUserByToken(123L)).thenReturn(userNullCart);
        // when(itemRepository.findById(456L)).thenReturn(Optional.of(itemNullCart));
        // when(userRepository.findById("123")).thenReturn(Optional.of(userNullCart));
    
        // itemService.addItemToCart(123L, 456L);
    
        // assertNotNull(userNullCart.getCartItems());
        // assertTrue(userNullCart.getCartItems().contains(itemNullCart));
        // verify(userRepository, times(1)).save(userNullCart);
    
        // reset(userRepository, itemRepository, userService);
    
    }

    @Test
    void testAddItemToWishlist() {
        User userSuccess = new User();
        userSuccess.setId(123L);
        userSuccess.setWishlistItems(new ArrayList<>());

        Item itemSuccess = new Clothes();
        itemSuccess.setId(456L);
        itemSuccess.setUsersWithItemInWishlist(new ArrayList<>());

        when(userService.getUserByToken(123L)).thenReturn(userSuccess);
        when(itemRepository.findById(456L)).thenReturn(Optional.of(itemSuccess));
        when(userRepository.findById("123")).thenReturn(Optional.of(userSuccess));

        itemService.addItemToWishlist(123L, 456L);

        assertTrue(userSuccess.getWishlistItems().contains(itemSuccess));
        assertTrue(itemSuccess.getUsersWithItemInWishlist().contains(userSuccess));
        verify(userRepository, times(1)).save(userSuccess);
        verify(itemRepository, times(1)).save(itemSuccess);

        reset(userRepository, itemRepository, userService);

        when(userService.getUserByToken(999L)).thenReturn(null);

        RuntimeException exUserToken = assertThrows(RuntimeException.class, () -> {
            itemService.addItemToWishlist(999L, 456L);
        });
        assertEquals("Usuario no encontrado", exUserToken.getMessage());

        verify(userRepository, never()).save(any());
        verify(itemRepository, never()).save(any());

        reset(userRepository, itemRepository, userService);

        User userItemNotFound = new User();
        userItemNotFound.setId(123L);

        when(userService.getUserByToken(123L)).thenReturn(userItemNotFound);
        when(itemRepository.findById(789L)).thenReturn(Optional.empty());

        RuntimeException exItem = assertThrows(RuntimeException.class, () -> {
            itemService.addItemToWishlist(123L, 789L);
        });
        assertEquals("Artículo no encontrado", exItem.getMessage());

        verify(userRepository, never()).save(any());
        verify(itemRepository, never()).save(any());

        reset(userRepository, itemRepository, userService);

        User userRepoNotFound = new User();
        userRepoNotFound.setId(123L);

        when(userService.getUserByToken(123L)).thenReturn(userRepoNotFound);
        when(itemRepository.findById(456L)).thenReturn(Optional.of(new Clothes()));
        when(userRepository.findById("123")).thenReturn(Optional.empty());

        RuntimeException exRepoUser = assertThrows(RuntimeException.class, () -> {
            itemService.addItemToWishlist(123L, 456L);
        });
        assertEquals("Usuario no encontrado", exRepoUser.getMessage());

        verify(userRepository, never()).save(any());

        reset(userRepository, itemRepository, userService);

        User userItemAlreadyInWishlist = new User();
        userItemAlreadyInWishlist.setId(123L);
        userItemAlreadyInWishlist.setWishlistItems(new ArrayList<>());

        Item itemAlreadyInWishlist = new Clothes();
        itemAlreadyInWishlist.setId(456L);
        itemAlreadyInWishlist.setUsersWithItemInWishlist(new ArrayList<>());

        userItemAlreadyInWishlist.getWishlistItems().add(itemAlreadyInWishlist);
        itemAlreadyInWishlist.getUsersWithItemInWishlist().add(userItemAlreadyInWishlist);

        when(userService.getUserByToken(123L)).thenReturn(userItemAlreadyInWishlist);
        when(itemRepository.findById(456L)).thenReturn(Optional.of(itemAlreadyInWishlist));
        when(userRepository.findById("123")).thenReturn(Optional.of(userItemAlreadyInWishlist));

        itemService.addItemToWishlist(123L, 456L);

        verify(userRepository, never()).save(any());
        verify(itemRepository, never()).save(any());
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
    void testGetWishlistItems() {
        User user = new User();
        user.setId(123L);
        user.setWishlistItems(new ArrayList<>());

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

        user.getWishlistItems().add(item1);
        user.getWishlistItems().add(item2);

        when(userService.getUserByToken(123L)).thenReturn(user);
        when(userRepository.findById(String.valueOf(123L))).thenReturn(Optional.of(user));

        List<Item> result = itemService.getWishlistItems(123L);

        assertEquals(2, result.size(), "La wishlist debería contener 2 ítems");
        assertEquals("Clothes Item 1", result.get(0).getTitle());
        assertEquals("Electronics Item 1", result.get(1).getTitle());

        when(userService.getUserByToken(456L)).thenReturn(null);

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            itemService.getWishlistItems(456L);
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
    void testRemoveItemFromWishlist() {
        User user = new User();
        user.setId(123L);
        user.setWishlistItems(new ArrayList<>());

        Item item1 = new Clothes();
        item1.setId(456L);
        item1.setUsersWithItemInWishlist(new ArrayList<>());

        Item item2 = new Electronics();
        item2.setId(789L);
        item2.setUsersWithItemInWishlist(new ArrayList<>());

        user.getWishlistItems().add(item1);
        user.getWishlistItems().add(item2);
        item1.getUsersWithItemInWishlist().add(user);
        item2.getUsersWithItemInWishlist().add(user);

        when(userService.getUserByToken(123L)).thenReturn(user);
        when(userRepository.findById("123")).thenReturn(Optional.of(user));

        itemService.removeItemFromWishlist(123L, 456L);

        assertFalse(user.getWishlistItems().contains(item1), "El item1 debería haberse eliminado de la wishlist");
        assertTrue(user.getWishlistItems().contains(item2), "El item2 debería permanecer en la wishlist");
        assertFalse(item1.getUsersWithItemInWishlist().contains(user), "El usuario debería haberse eliminado de la lista del item1");
        assertTrue(item2.getUsersWithItemInWishlist().contains(user), "El usuario debería permanecer en la lista del item2");

        verify(userRepository, times(1)).save(user);
        verify(itemRepository, times(1)).save(item1);

        when(userService.getUserByToken(999L)).thenReturn(null);
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            itemService.removeItemFromWishlist(999L, 456L);
        });
        assertEquals("Usuario no encontrado", exception.getMessage());

        User emptyWishlistUser = new User();
        emptyWishlistUser.setId(555L);
        emptyWishlistUser.setWishlistItems(new ArrayList<>());
        when(userService.getUserByToken(555L)).thenReturn(emptyWishlistUser);
        when(userRepository.findById("555")).thenReturn(Optional.of(emptyWishlistUser));
        RuntimeException emptyWishlistException = assertThrows(RuntimeException.class, () -> {
            itemService.removeItemFromWishlist(555L, 456L);
        });
        assertEquals("La wishlist está vacía.", emptyWishlistException.getMessage());

        when(userService.getUserByToken(123L)).thenReturn(user);
        when(userRepository.findById("123")).thenReturn(Optional.of(user));
        RuntimeException itemNotFoundException = assertThrows(RuntimeException.class, () -> {
            itemService.removeItemFromWishlist(123L, 999L);
        });
        assertEquals("Artículo no encontrado en la wishlist", itemNotFoundException.getMessage());
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

    @Test
    void testGetDTOById() {
    
        User mockSeller = new User();
        mockSeller.setId(100L); 
    
        Item mockItem = new Clothes();
        mockItem.setId(1L);
        mockItem.setTitle("Camiseta");
        mockItem.setSeller(mockSeller); 
    
        when(itemRepository.findById(1L)).thenReturn(Optional.of(mockItem));

        ItemDTO result = ItemService.getDTOById(1L);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("Camiseta", result.getTitle());
        verify(itemRepository, times(1)).findById(1L);

        when(itemRepository.findById(999L)).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            ItemService.getDTOById(999L);
        });

        assertEquals("Item not found with id: 999", exception.getMessage());
        verify(itemRepository, times(1)).findById(999L);
    }

    @Test
    void testConvertToDTO(){
        User seller = new User();
        seller.setId(100L);
        
        Clothes clothes = new Clothes();
        clothes.setId(1L);
        clothes.setTitle("Camiseta");
        clothes.setDescription("Camiseta de algodón");
        clothes.setPrice(19.99f);
        clothes.setSeller(seller);
        clothes.setSize(ClothesSize.M);
        clothes.setCategory(Category.WOMAN);
        clothes.setClothesType(ClothesType.TSHIRT);
        clothes.setImages(Arrays.asList("img1.jpg", "img2.jpg"));

        ItemDTO result = ItemService.convertToDTO(clothes);

        // Verificación
        assertTrue(result instanceof ClothesDTO);
        ClothesDTO clothesDTO = (ClothesDTO) result;
        
        assertEquals(1L, clothesDTO.getId());
        assertEquals("Camiseta", clothesDTO.getTitle());
        assertEquals("Camiseta de algodón", clothesDTO.getDescription());
        assertEquals(19.99f, clothesDTO.getPrice());
        assertEquals(100L, clothesDTO.getSellerId());
        assertEquals(ClothesSize.M, clothesDTO.getSize());
        assertEquals(Category.WOMAN, clothesDTO.getCategory());
        assertEquals(ClothesType.TSHIRT, clothesDTO.getClothesType());
        assertEquals(2, clothesDTO.getImages().size());


        Electronics electronics = new Electronics();
        electronics.setId(2L);
        electronics.setTitle("Smartphone");
        electronics.setDescription("Último modelo");
        electronics.setPrice(599.99f);
        electronics.setSeller(seller);
        electronics.setElectronicsType(ElectronicsType.DEVICE);
        electronics.setImages(Arrays.asList("phone1.jpg"));

        ItemDTO result1 = ItemService.convertToDTO(electronics);

        assertTrue(result1 instanceof ElectronicsDTO);
        ElectronicsDTO electronicsDTO = (ElectronicsDTO) result1;
        
        assertEquals(2L, electronicsDTO.getId());
        assertEquals(ElectronicsType.DEVICE, electronicsDTO.getElectronicsType());
        assertEquals(1, electronicsDTO.getImages().size());


        Pet pet = new Pet();
        pet.setId(3L);
        pet.setSpecies(Species.DOG);
        pet.setTitle("Cachorro Labrador");
        pet.setPrice(300.0f);
        pet.setSeller(seller);

        ItemDTO result2 = ItemService.convertToDTO(pet);

        assertTrue(result2 instanceof PetDTO);
        assertEquals(Species.DOG, ((PetDTO) result2).getSpecies());

        Home home = new Home();
        home.setHomeType(HomeType.FURNITURE);
        home.setSeller(seller);

        ItemDTO result3 = ItemService.convertToDTO(home);

        assertInstanceOf(HomeDTO.class, result3);
        assertEquals(HomeType.FURNITURE, ((HomeDTO) result3).getHomeType());


        Entertainment entertainment = new Entertainment();
        entertainment.setEntertainmentType(EntertainmentType.BOOK);
        entertainment.setSeller(seller);

        ItemDTO result4 = ItemService.convertToDTO(entertainment);

        assertInstanceOf(EntertainmentDTO.class, result4);
        assertEquals(EntertainmentType.BOOK, ((EntertainmentDTO) result4).getEntertainmentType());


        class UnknownItem extends Item {
            @Override
            public ItemDTO toDTO() {
                throw new UnsupportedOperationException();
            }
        }
        
        UnknownItem unknownItem = new UnknownItem();
    
        IllegalArgumentException ex = assertThrows(
            IllegalArgumentException.class,
            () -> ItemService.convertToDTO(unknownItem)
        );
    
        assertTrue(ex.getMessage().contains("Tipo de item desconocido"));


        Clothes clothes1 = new Clothes();
        clothes1.setSeller(null);

        assertThrows(NullPointerException.class,
            () -> ItemService.convertToDTO(clothes1));

        
    }
    @Test
    void testSearchItems_NoQuery_NoType() {
        int page = 0;
        Pageable pageable = PageRequest.of(page, 28);
        Clothes clothes = new Clothes();
        clothes.setId(1L);
        clothes.setTitle("Test Clothes");

        List<Item> items = List.of(clothes);
        Page<Item> itemPage = new PageImpl<>(items, pageable, items.size());

        when(itemRepository.findAll(pageable)).thenReturn(itemPage);

        Page<Item> result = itemService.searchItems(null, null, page, null);
        assertEquals(1, result.getTotalElements());
        assertEquals("Test Clothes", result.getContent().get(0).getTitle());

        result = itemService.searchItems(null, "", page, "");
        assertEquals(1, result.getTotalElements());
        assertEquals("Test Clothes", result.getContent().get(0).getTitle());
    }

    @Test
    void testSearchItems_WithQuery() {
        int page = 0;
        Pageable pageable = PageRequest.of(page, 28);
        String query = "shirt";

        Clothes clothes = new Clothes();
        clothes.setId(1L);
        clothes.setTitle("Test Shirt");

        List<Item> items = List.of(clothes);
        Page<Item> itemPage = new PageImpl<>(items, pageable, items.size());

        when(itemRepository.searchByQuery(query, pageable)).thenReturn(itemPage);

        Page<Item> result = itemService.searchItems(null, query, page, null);

        assertEquals(1, result.getTotalElements());
        assertEquals("Test Shirt", result.getContent().get(0).getTitle());
    }

    @Test
    void testSearchItems_WithQueryAndType_Duplicate() {
        int page = 0;
        Pageable pageable = PageRequest.of(page, 28);
        String query = "laptop";
        String type = "Electronics";

        Electronics electronics = new Electronics();
        electronics.setId(1L);
        electronics.setTitle("Gaming Laptop");

        List<Item> items = List.of(electronics);
        Page<Item> itemPage = new PageImpl<>(items, pageable, items.size());

        when(itemRepository.searchByTypeAndQuery(Electronics.class, query, pageable)).thenReturn(itemPage);

        Page<Item> result = itemService.searchItems(null, query, page, type);

        assertEquals(1, result.getTotalElements());
        assertEquals("Gaming Laptop", result.getContent().get(0).getTitle());
    }

    @Test
    void testSearchItems_WithQueryAndType() {
        int page = 0;
        Pageable pageable = PageRequest.of(page, 28);
        String query = "laptop";
        String type = "Electronics";

        Electronics electronics = new Electronics();
        electronics.setId(1L);
        electronics.setTitle("Gaming Laptop");

        List<Item> items = List.of(electronics);
        Page<Item> itemPage = new PageImpl<>(items, pageable, items.size());

        when(itemRepository.searchByTypeAndQuery(Electronics.class, query, pageable)).thenReturn(itemPage);

        Page<Item> result = itemService.searchItems(null, query, page, type);

        assertEquals(1, result.getTotalElements());
        assertEquals("Gaming Laptop", result.getContent().get(0).getTitle());
    }
    @Test
void testGetItems_NoToken_NoType() {
    int page = 0;
    Pageable pageable = PageRequest.of(page, 28);

    Clothes clothes = new Clothes();
    clothes.setId(1L);
    clothes.setTitle("Test Clothes");

    List<Item> items = List.of(clothes);
    Page<Item> itemPage = new PageImpl<>(items, pageable, items.size());

    when(itemRepository.findAll(pageable)).thenReturn(itemPage);

    Page<Item> result = itemService.getItems(null, page, null);

    assertEquals(1, result.getTotalElements());
    assertEquals("Test Clothes", result.getContent().get(0).getTitle());
}
@Test
void testGetItems_WithToken_NoType() {
    int page = 0;
    long token = 12345L;
    Pageable pageable = PageRequest.of(page, 28);
    Long userId = 1L;

    User user = new User();
    user.setId(userId);
    when(userService.getUserByToken(token)).thenReturn(user);

    Clothes clothes = new Clothes();
    clothes.setId(1L);
    clothes.setTitle("Test Clothes");

    List<Item> items = List.of(clothes);
    Page<Item> itemPage = new PageImpl<>(items, pageable, items.size());

    when(itemRepository.findBySellerIdNot(userId, pageable)).thenReturn(itemPage);

    Page<Item> result = itemService.getItems(token, page, null);

    assertEquals(1, result.getTotalElements());
    assertEquals("Test Clothes", result.getContent().get(0).getTitle());
}
@Test
void testGetItems_WithType() {
    int page = 0;
    String type = "Electronics";
    Pageable pageable = PageRequest.of(page, 28);

    Electronics electronics = new Electronics();
    electronics.setId(1L);
    electronics.setTitle("Smartphone");

    List<Item> items = List.of(electronics);
    Page<Item> itemPage = new PageImpl<>(items, pageable, items.size());

    when(itemRepository.findByType(Electronics.class, pageable)).thenReturn(itemPage);

    Page<Item> result = itemService.getItems(null, page, type);

    assertEquals(1, result.getTotalElements());
    assertEquals("Smartphone", result.getContent().get(0).getTitle());
}
@Test
void testGetItems_WithTokenAndType() {
    int page = 0;
    long token = 12345L;
    String type = "Home";
    Pageable pageable = PageRequest.of(page, 28);
    Long userId = 1L;

    User userWithId = new User();
    userWithId.setId(userId);
    when(userService.getUserByToken(token)).thenReturn(userWithId);

    Home homeItem = new Home();
    homeItem.setId(1L);
    homeItem.setTitle("Sofa");

    List<Item> items = List.of(homeItem);
    Page<Item> itemPage = new PageImpl<>(items, pageable, items.size());

    when(itemRepository.findBySellerIdNotAndType(userId, Home.class, pageable)).thenReturn(itemPage);

    Page<Item> result = itemService.getItems(token, page, type);

    assertEquals(1, result.getTotalElements());
    assertEquals("Sofa", result.getContent().get(0).getTitle());
}

}
