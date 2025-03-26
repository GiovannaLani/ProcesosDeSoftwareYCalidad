package com.spq.vinted.controller;

import java.util.ArrayList;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
import com.spq.vinted.service.ItemService;

import io.swagger.v3.oas.annotations.tags.Tag;


@RestController
@RequestMapping("/items")
@Tag(name = "Item Controller", description = "API for managing items")
public class ItemController {
    private ItemService itemService;

    public ItemController(ItemService itemService) {
        super();
        this.itemService = itemService;
    }
    


    @GetMapping("/items")
    public ResponseEntity<List<ItemDTO>> getItems() {
        try {
            List<Item> items = itemService.getItems();
            List<ItemDTO> itemDTOs = new ArrayList<ItemDTO>();
            for(Item item: items){
                itemDTOs.add(item.toDTO());
            }
            return ResponseEntity.ok(itemDTOs);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }


    @GetMapping("/clothes")
    public ResponseEntity<List<ClothesDTO>> getClothes() {
        try {
            List<Clothes> clothes = itemService.getClothes();
            List<ClothesDTO> clothesDTOs = new ArrayList<ClothesDTO>();
            for(Clothes c: clothes){
                clothesDTOs.add(c.toDTO());
            }
            return ResponseEntity.ok(clothesDTOs);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/clothes/{category}")
    public ResponseEntity<List<ClothesDTO>> getClothesByCategory(@PathVariable Category category) {
        try {
            List<Clothes> clothes = itemService.getClothesByCategory(category);
            List<ClothesDTO> clothesDTOs = new ArrayList<ClothesDTO>();
            for(Clothes c: clothes){
                clothesDTOs.add(c.toDTO());
            }
            return ResponseEntity.ok(clothesDTOs);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/electronics")
    public ResponseEntity<List<ElectronicsDTO>> getElectronics() {
        try {
            List<Electronics> electronics = itemService.getElectronics();
            List<ElectronicsDTO> electronicsDTOs = new ArrayList<ElectronicsDTO>();
            for(Electronics e: electronics){
                electronicsDTOs.add(e.toDTO());
            }
            return ResponseEntity.ok(electronicsDTOs);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/home")   
    public ResponseEntity<List<HomeDTO>> getHomeItems() {
        try {
            List<Home> homeItems = itemService.getHomeItems();
            List<HomeDTO> homeDTOs = new ArrayList<HomeDTO>();
            for(Home homeItem: homeItems){
                homeDTOs.add(homeItem.toDTO());
            }
            return ResponseEntity.ok(homeDTOs);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/pet")
    public ResponseEntity<List<PetDTO>> getItemsForPet() {
        try {
            List<Pet> petItems = itemService.getItemsForPet();
            List<PetDTO> petDTOs = new ArrayList<PetDTO>();
            for(Pet petItem: petItems){
                petDTOs.add(petItem.toDTO());
            }
            return ResponseEntity.ok(petDTOs);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/entertainment")   
    public ResponseEntity<List<EntertainmentDTO>> getItemsforEntertainment() {
        try {
            List<Entertainment> entertainmentItems = itemService.getItemsforEntertainment();
            List<EntertainmentDTO> entertainmentDTOs = new ArrayList<EntertainmentDTO>();
            for(Entertainment entertainmentItem: entertainmentItems){
                entertainmentDTOs.add(entertainmentItem.toDTO());
            }
            return ResponseEntity.ok(entertainmentDTOs);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
}