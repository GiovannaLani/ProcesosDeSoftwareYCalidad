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

import com.spq.vinted.model.Category;
import com.spq.vinted.model.Clothes;
import com.spq.vinted.model.Electronics;
import com.spq.vinted.model.Entertainment;
import com.spq.vinted.model.Home;
import com.spq.vinted.model.Item;
import com.spq.vinted.model.Pet;
import com.spq.vinted.repository.ItemRepository;

import com.spq.vinted.dto.ItemDTO;
import com.spq.vinted.model.Item;
import com.spq.vinted.model.User;
import com.spq.vinted.repository.UserRepository;


@Service
public class ItemService {
    @Autowired
    private UserService userService;
    private final UserRepository userRepository;


    
    ItemRepository itemRepository;
    
    public ItemService(ItemRepository itemRepository, UserRepository userRepository) {
        this.itemRepository = itemRepository;
        this.userRepository = userRepository;
    }

    public List<Item> getItems() {
        return itemRepository.findAll();
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
             
        List<Item> cartItems = user.getCartItems();
        if (cartItems == null || !user.getCartItems().contains(item)) {
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

        List<Item> cartItems = user.getCartItems();
        if (cartItems == null || cartItems.isEmpty()) {
            throw new RuntimeException("El carrito está vacío.");
        }

        Item itemToRemove = cartItems.stream()
                .filter(item -> item.getId() == itemId)
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Artículo no encontrado en el carrito"));

        cartItems.remove(itemToRemove);

        itemToRemove.getUsersWithItemInCart().remove(user);

        userRepository.save(user);
        itemRepository.save(itemToRemove); 
    }

}


