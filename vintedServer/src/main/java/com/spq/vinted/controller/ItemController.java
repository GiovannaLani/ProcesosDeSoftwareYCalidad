package com.spq.vinted.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.spq.vinted.dto.ItemDTO;
import com.spq.vinted.dto.UserDTO;
import com.spq.vinted.model.Item;
import com.spq.vinted.model.User;
import com.spq.vinted.service.ItemService;


@RestController
@RequestMapping("/items")
public class ItemController {
    private ItemService itemService;

    public ItemController(ItemService itemService) {
        super();
        this.itemService = itemService;
    }
    @PostMapping("/add-to-cart")
    public ResponseEntity<Void> addItemToCart(@RequestParam("token") long token, @RequestParam ItemDTO item) {
        // User seller = userRepository.findById(item.getSellerId()).orElseThrow(() -> new RuntimeException("Seller not found"));
        // itemService.addItemToCart(token, item);
        return ResponseEntity.ok().build();
    }
    

    @GetMapping("/cart")
    public ResponseEntity<List<Item>> getCart(@RequestParam("token") long token) {
        List<Item> cartItems = itemService.getCartItems(token);
        return ResponseEntity.ok(cartItems);
}
}
