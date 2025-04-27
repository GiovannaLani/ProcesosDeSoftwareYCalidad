package com.spq.vinted.service;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.spq.vinted.dto.ClothesDTO;
import com.spq.vinted.dto.ElectronicsDTO;
import com.spq.vinted.dto.EntertainmentDTO;
import com.spq.vinted.dto.HomeDTO;
import com.spq.vinted.dto.ItemDTO;
import com.spq.vinted.dto.PetDTO;
import com.spq.vinted.model.Category;
import com.spq.vinted.model.Clothes;
import com.spq.vinted.model.Electronics;
import com.spq.vinted.model.Entertainment;
import com.spq.vinted.model.Home;
import com.spq.vinted.model.Item;
import com.spq.vinted.model.Pet;
import com.spq.vinted.repository.ItemRepository;

import com.spq.vinted.model.User;
import com.spq.vinted.repository.UserRepository;

import jakarta.transaction.Transactional;


@Service
public class ItemService {
    private UserService userService;
    private final UserRepository userRepository;


    
    static ItemRepository itemRepository;
    //ItemRepository itemRepository;
    
    public ItemService(ItemRepository itemRepository, UserRepository userRepository, UserService userService) {
        this.itemRepository = itemRepository;
        this.userRepository = userRepository;
        this.userService = userService;
    }

    public List<Item> getItems(Long token) {
        try {
            if (token == null) {
                return itemRepository.findAll();
            } else {
                Long userId = userService.getUserByToken(token).getId();
                return itemRepository.findBySellerIdNot(userId);
            }
        } catch (Exception e) {
            throw new RuntimeException("Failed to fetch items: " + e.getMessage(), e);
        }
    }

    public Item getItemById(long id) {
        return itemRepository.findById(id).orElseThrow(() -> new RuntimeException("Item not found"));
    }

    public List<Clothes> getClothes(){
        return itemRepository.findAll().stream().filter(item -> item instanceof Clothes).map(item -> (Clothes) item).collect(Collectors.toList());
    }

    public List<Clothes> getClothesByCategory(Category category){
        return itemRepository.findAll().stream().filter(item -> item instanceof Clothes).map(item -> (Clothes) item).filter(clothes -> clothes.getCategory().equals(category)).collect(Collectors.toList());
    }

    public List<Electronics> getElectronics(){
        return itemRepository.findAll().stream().filter(item -> item instanceof Electronics).map(item -> (Electronics) item).collect(Collectors.toList());
    }
    public List<Home> getHomeItems(){
        return itemRepository.findAll().stream().filter(item -> item instanceof Home).map(item -> (Home) item).collect(Collectors.toList());
    }
    public List<Pet> getItemsForPet(){
        return itemRepository.findAll().stream().filter(item -> item instanceof Pet).map(item -> (Pet) item).collect(Collectors.toList());
    }

    public User getItemOwner(long itemId) {
        Item item = itemRepository.findById(itemId).orElseThrow(() -> new RuntimeException("Item not found"));
        return item.getSeller();
    }    

    public List<Entertainment> getItemsforEntertainment(){
        return itemRepository.findAll().stream().filter(item -> item instanceof Entertainment).map(item -> (Entertainment) item).collect(Collectors.toList());
    }
    public Item saveItem(Item item) {
        return itemRepository.save(item);
    }
   public void uploadItemImages(long id, List<MultipartFile> itemImages) throws IOException {
    String uploadDir = "uploads/items/";
    Item item = itemRepository.findById(id).orElseThrow(() -> new RuntimeException("Item not found"));
    
    File uploadPath = new File(uploadDir);
    if (!uploadPath.exists()) {
        uploadPath.mkdirs(); 
    }

    List<String> imageNames = new ArrayList<>(); 
    
    for (MultipartFile itemImage : itemImages) {
        String uniqueFileName = UUID.randomUUID().toString() + "_" + itemImage.getOriginalFilename();
        Path filePath = Paths.get(uploadDir).resolve(uniqueFileName).toAbsolutePath();

        Files.copy(itemImage.getInputStream(), filePath);

        imageNames.add(uniqueFileName); 
    }

   
    item.setImages(imageNames); 
    itemRepository.save(item);}
    
    public void addItemToCart(long token, long itemId) {
        User user = userService.getUserByToken(token);
        
        if (user == null) {
            throw new RuntimeException("Usuario no encontrado");
        }
    
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new RuntimeException("Artículo no encontrado"));
    
        user = userRepository.findById(String.valueOf(user.getId()))
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
    
        if (user.getCartItems() == null) {
            user.setCartItems(new ArrayList<>());
        }
    
        if (!user.getCartItems().contains(item)) {
            user.getCartItems().add(item);
            item.getUsersWithItemInCart().add(user);
    
            userRepository.save(user);
            itemRepository.save(item);
        }
    }
    
    public List<Item> getCartItems(long token) {
        User user = userService.getUserByToken(token);
        if (user == null) {
            throw new RuntimeException("Usuario no encontrado");
        }
    
        user = userRepository.findById(String.valueOf(user.getId()))
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
    
        List<Item> cartItems = user.getCartItems();
        if (cartItems == null || cartItems.isEmpty()) {
            return Collections.emptyList();
        }
    
        return cartItems;
    }

    public void removeItemFromCart(long token, long itemId) {
        User user = userService.getUserByToken(token);
        if (user == null) {
            throw new RuntimeException("Usuario no encontrado");
        }
    
        user = userRepository.findById(String.valueOf(user.getId()))
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
    
        if (user.getCartItems() == null || user.getCartItems().isEmpty()) {
            throw new RuntimeException("El carrito está vacío.");
        }
    
        Item itemToRemove = user.getCartItems().stream()
                .filter(item -> item.getId() == itemId)
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Artículo no encontrado en el carrito"));
    
        user.getCartItems().remove(itemToRemove);
        itemToRemove.getUsersWithItemInCart().remove(user);
    
        itemRepository.save(itemToRemove);
        userRepository.save(user);
    }

    public void addItemToWishlist(long token, long itemId) {
        User user = userService.getUserByToken(token);
        
        if (user == null) {
            throw new RuntimeException("Usuario no encontrado");
        }
    
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new RuntimeException("Artículo no encontrado"));
    
        user = userRepository.findById(String.valueOf(user.getId()))
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
    
        if (user.getWishlistItems() == null) {
            user.setWishlistItems(new ArrayList<>());
        }
    
        if (!user.getWishlistItems().contains(item)) {
            user.getWishlistItems().add(item);
            item.getUsersWithItemInWishlist().add(user);
    
            userRepository.save(user);
            itemRepository.save(item);
        }
    }

    public List<Item> getWishlistItems(long token) {
        User user = userService.getUserByToken(token);
        if (user == null) {
            throw new RuntimeException("Usuario no encontrado");
        }
    
        user = userRepository.findById(String.valueOf(user.getId()))
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
    
        List<Item> wishlistItems = user.getWishlistItems();
        if (wishlistItems == null || wishlistItems.isEmpty()) {
            return Collections.emptyList();
        }
    
        return wishlistItems;
    }

    public void removeItemFromWishlist(long token, long itemId) {
        User user = userService.getUserByToken(token);
        if (user == null) {
            throw new RuntimeException("Usuario no encontrado");
        }
    
        user = userRepository.findById(String.valueOf(user.getId()))
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
    
        if (user.getWishlistItems() == null || user.getWishlistItems().isEmpty()) {
            throw new RuntimeException("La wishlist está vacía.");
        }
    
        Item itemToRemove = user.getWishlistItems().stream()
                .filter(item -> item.getId() == itemId)
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Artículo no encontrado en la wishlist"));
    
        user.getWishlistItems().remove(itemToRemove);
        itemToRemove.getUsersWithItemInWishlist().remove(user);
    
        itemRepository.save(itemToRemove);
        userRepository.save(user);
    }

    public List<Item> getUserItems(long userId) {
        User user = userRepository.findById(String.valueOf(userId)).orElseThrow(() -> new RuntimeException("User not found"));
        return user.getItemsForSale();
    }

    @Transactional
    public void deleteItem(long token, long itemId) {
        User user = userService.getUserByToken(token);
        if (user == null) {
            throw new RuntimeException("Not authorized");
        }

        Item item = getItemById(itemId);
        if (item == null) {
            throw new RuntimeException("Item not found");
        }

        // Eliminar el item del carrito de todos los usuarios
        for (User u : item.getUsersWithItemInCart()) {
            u.getCartItems().remove(item);
            userRepository.save(u);
        }
        item.getUsersWithItemInCart().clear();

        // Eliminar el item de la wishlist de todos los usuarios
        for (User u : item.getUsersWithItemInWishlist()) {
            u.getWishlistItems().remove(item);
            userRepository.save(u);
        }
        item.getUsersWithItemInWishlist().clear();

        itemRepository.save(item);
        itemRepository.delete(item);

        System.out.println("Item borrado correctamente.");
    }
    
    public static ItemDTO getDTOById(Long itemId) {
        Item item = itemRepository.findById(itemId).orElseThrow(() -> new RuntimeException("Item not found with id: " + itemId));
        return convertToDTO(item);
    }

    public static ItemDTO convertToDTO(Item item) {
        ItemDTO itemDTO;
        
        if (item instanceof Clothes) {
            Clothes clothesItem = (Clothes) item;
            ClothesDTO clothesDTO = new ClothesDTO();
            clothesDTO.setSize(clothesItem.getSize());
            clothesDTO.setCategory(clothesItem.getCategory());
            clothesDTO.setClothesType(clothesItem.getClothesType());
            clothesDTO.setId(clothesItem.getId());
            clothesDTO.setTitle(clothesItem.getTitle());
            clothesDTO.setDescription(clothesItem.getDescription());
            clothesDTO.setPrice(clothesItem.getPrice());
            clothesDTO.setSellerId(clothesItem.getSeller().getId());
            clothesDTO.setImages(clothesItem.getImages());
            
            itemDTO = clothesDTO;
        } else if (item instanceof Electronics) {
            Electronics electronicsItem = (Electronics) item;
            ElectronicsDTO electronicsDTO = new ElectronicsDTO();

            electronicsDTO.setElectronicsType(electronicsItem.getElectronicsType());
            electronicsDTO.setId(electronicsItem.getId());
            electronicsDTO.setTitle(electronicsItem.getTitle());
            electronicsDTO.setDescription(electronicsItem.getDescription());
            electronicsDTO.setPrice(electronicsItem.getPrice());
            electronicsDTO.setSellerId(electronicsItem.getSeller().getId());
            electronicsDTO.setImages(electronicsItem.getImages());

            itemDTO = electronicsDTO;
        } else if (item instanceof Pet) {
            Pet petItem = (Pet) item;
            PetDTO petDTO = new PetDTO();

            petDTO.setSpecies(petItem.getSpecies());
            petDTO.setId(petItem.getId());
            petDTO.setTitle(petItem.getTitle());
            petDTO.setDescription(petItem.getDescription());
            petDTO.setPrice(petItem.getPrice());
            petDTO.setSellerId(petItem.getSeller().getId());
            petDTO.setImages(petItem.getImages());

            itemDTO = petDTO;
        } else if (item instanceof Entertainment) {
            Entertainment entertainmentItem = (Entertainment) item;
            EntertainmentDTO entertainmentDTO = new EntertainmentDTO();


            entertainmentDTO.setEntertainmentType(entertainmentItem.getEntertainmentType());
            entertainmentDTO.setId(entertainmentItem.getId());
            entertainmentDTO.setTitle(entertainmentItem.getTitle());
            entertainmentDTO.setDescription(entertainmentItem.getDescription());
            entertainmentDTO.setPrice(entertainmentItem.getPrice());
            entertainmentDTO.setSellerId(entertainmentItem.getSeller().getId());
            entertainmentDTO.setImages(entertainmentItem.getImages());
            itemDTO = entertainmentDTO;
        } else if (item instanceof Home) {
            Home homeItem = (Home) item;
            HomeDTO homeDTO = new HomeDTO();

            homeDTO.setHomeType(homeItem.getHomeType());
            homeDTO.setId(homeItem.getId());
            homeDTO.setTitle(homeItem.getTitle());
            homeDTO.setDescription(homeItem.getDescription());
            homeDTO.setPrice(homeItem.getPrice());
            homeDTO.setSellerId(homeItem.getSeller().getId());
            homeDTO.setImages(homeItem.getImages());

            itemDTO = homeDTO;
        } else {
            throw new IllegalArgumentException("Tipo de item desconocido: " + item.getClass().getName());
        }

        itemDTO.setId(item.getId());
        itemDTO.setTitle(item.getTitle());
        itemDTO.setDescription(item.getDescription());
        itemDTO.setPrice(item.getPrice());
        itemDTO.setSellerId(item.getSeller().getId());
        
        // Si tienes imágenes, configúralas
        if (item.getImages() != null) {
            itemDTO.setImages(item.getImages());
        }
        
        return itemDTO;
    }
    
    public List<Item> searchItems(Long token, String query) {
        if (query == null || query.isBlank()) return getItems(token);
        List<Item> items = itemRepository.findAll().stream()
                .filter(item -> item.getTitle().toLowerCase().contains(query.toLowerCase()))
                .collect(Collectors.toList());
        System.out.println("2"+items);
        return items;
    }
}


