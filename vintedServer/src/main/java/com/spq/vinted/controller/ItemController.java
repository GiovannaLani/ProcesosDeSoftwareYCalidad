package com.spq.vinted.controller;

import java.util.ArrayList;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
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
import com.spq.vinted.model.User;
import com.spq.vinted.service.ItemService;
import com.spq.vinted.service.UserService;

import io.swagger.v3.oas.annotations.tags.Tag;


@RestController
@RequestMapping("/items")
@Tag(name = "Item Controller", description = "API for managing items")
public class ItemController {
    private ItemService itemService;
    private UserService userService;

    public ItemController(ItemService itemService, UserService userService) {
        super();
        this.itemService = itemService;
        this.userService = userService;
    }
    
    @PostMapping("/itemData")
    public ResponseEntity<Void> uploadItemData(
            @RequestParam("token") long token,
            @RequestBody ItemDTO itemDTO) {
        User user = userService.getUserByToken(token);
        if (user == null) {
            return ResponseEntity.status(403).build(); // Forbidden
        }

        if (itemDTO instanceof ClothesDTO) {
            ClothesDTO clothes = (ClothesDTO) itemDTO;
            Clothes clothesItem = new Clothes(
                    clothes.getTitle(),
                    clothes.getDescription(),
                    clothes.getPrice(),
                    null,
                    clothes.getSize(),
                    clothes.getBrand(),
                    clothes.getCategory(),
                    user
            );
            itemService.saveItem(clothesItem);
            System.out.println("titulo: " + clothes.getTitle()+
                    " descripcion: " + clothes.getDescription() +
                    " precio: " + clothes.getPrice() +
                    " categoria: " + clothes.getCategory() +
                    " talla: " + clothes.getSize() );
        } else if (itemDTO instanceof ElectronicsDTO) {
            ElectronicsDTO electronics = (ElectronicsDTO) itemDTO;
            Electronics electronicsItem = new Electronics(
                    electronics.getTitle(),
                    electronics.getDescription(),
                    electronics.getPrice(),
                    null,
                    user
            );
            itemService.saveItem(electronicsItem);
            System.out.println("title"+ ": " + electronics.getTitle() +
                    " description: " + electronics.getDescription() +
                    " price: " + electronics.getPrice() );
        } else if (itemDTO instanceof PetDTO) {
            PetDTO pet = (PetDTO) itemDTO;
            Pet petItem = new Pet(
                    pet.getTitle(),
                    pet.getDescription(),
                    pet.getPrice(),
                    null,
                    pet.getSpecies(),
                    user
            );
            itemService.saveItem(petItem);
            System.out.println("titulo: " + pet.getTitle() +
                    " descripcion: " + pet.getDescription() +
                    " precio: " + pet.getPrice() +
                    " tipo: " + pet.getSpecies() );
        } else if (itemDTO instanceof HomeDTO){
            HomeDTO home = (HomeDTO) itemDTO;
            Home homeItem = new Home(
                    home.getTitle(),
                    home.getDescription(),
                    home.getPrice(),
                    null,
                    user
            );
            itemService.saveItem(homeItem);
            System.out.println("titulo: " + home.getTitle() +
                    " descripcion: " + home.getDescription() +
                    " precio: " + home.getPrice() );
        }else if(itemDTO instanceof EntertainmentDTO){
            EntertainmentDTO entertainment = (EntertainmentDTO) itemDTO;
            Entertainment entertainmentItem = new Entertainment(
                    entertainment.getTitle(),
                    entertainment.getDescription(),
                    entertainment.getPrice(),
                    null,
                    user
            );
            itemService.saveItem(entertainmentItem);
            System.out.println("titulo: " + entertainment.getTitle() +
                    " descripcion: " + entertainment.getDescription() +
                    " precio: " + entertainment.getPrice());
        }

        return ResponseEntity.ok().build();
    }

/* 
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
    }*/


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