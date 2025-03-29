package com.spq.vinted.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.spq.vinted.dto.ItemDTO;
import com.spq.vinted.model.Item;
import com.spq.vinted.model.User;
import com.spq.vinted.repository.UserRepository;


@Service
public class ItemService {
    private AuthService authService;
    private final UserRepository userRepository;


    public ItemService(AuthService authService, UserRepository userRepository) {
        super();
        this.authService = authService;
        this.userRepository = userRepository;
    }

    //public void createItem(long id, String title, String description, float price, String image, long sellerId) {
    //pasar a ItemDTO que se puede hacer poniendo toDTO en el item
    public void createItem(ItemDTO itemDTO) {
        User seller = userRepository.findById(String.valueOf(itemDTO.getSellerId())).orElseThrow(() -> new RuntimeException("Vendedor no encontrado"));
        //Item item = new Item(itemDTO.getId(), itemDTO.getTitle(), itemDTO.getDescription(), itemDTO.getPrice(), itemDTO.getImage(), seller);
    }

    public void addItemToCart(long token, Item item) {
        User user = authService.getUser(token);
        if(user != null) {
            user.getCartItems().add(item);
        }else{
            throw new RuntimeException("Usuario no encontrado");
        }
        
    }

/*
    public void addItemToCart(long token, long itemId) {
        User user = authService.getUser(token);
        if (user == null) {
            throw new RuntimeException("Usuario no encontrado");
        }
    
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new RuntimeException("Producto no encontrado"));
    
        user.getCartItems().add(item);
        userRepository.save(user);
    }
*/



    public List<Item> getCartItems(long token) {
        User user = authService.getUser(token);
        if (user == null) {
            throw new RuntimeException("Usuario no encontrado");
        }
        return user.getCartItems();
    }
}
