package com.spq.vinted.controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.net.MalformedURLException;

import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;

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

    @PostMapping("/add-to-cart")
    public ResponseEntity<Void> addItemToCart(@RequestParam("token") long token, @RequestParam("itemId") long itemId) {
        try {
            itemService.addItemToCart(token, itemId);
            return ResponseEntity.ok().build();
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }
    

    @GetMapping("/cart")
    public ResponseEntity<List<ItemDTO>> getCart(@RequestParam("token") long token) {
        try {
            List<Item> cartItems = itemService.getCartItems(token);
            if (cartItems == null || cartItems.isEmpty()) {
                return ResponseEntity.ok(null);
            }
            List<ItemDTO> cartItemDTOs = new ArrayList<ItemDTO>();
            for (Item item : cartItems) {
                cartItemDTOs.add(item.toDTO());
            }
            return ResponseEntity.ok(cartItemDTOs);
        } catch (RuntimeException e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    @DeleteMapping("/cart/remove")
    public ResponseEntity<Void> deleteItemFromCart(@RequestParam("token") long token, @RequestParam("itemId") long itemId) {
        try {
            itemService.removeItemFromCart(token, itemId);
            return ResponseEntity.ok().build();
        } catch (RuntimeException e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    @PostMapping("/itemData")
    public ResponseEntity<Long> uploadItemData(
            @RequestParam("token") long token,
            @RequestBody ItemDTO itemDTO) {
        User user = userService.getUserByToken(token);
        if (user == null) {
            return ResponseEntity.status(403).build();
        }
        Item savedItem = null;
        if (itemDTO instanceof ClothesDTO clothes) {
            Clothes clothesItem = new Clothes(clothes.getTitle(), clothes.getDescription(), clothes.getPrice(),
                    clothes.getSize(), clothes.getClothesType(), clothes.getCategory(), user);
            savedItem = itemService.saveItem(clothesItem);
        } else if (itemDTO instanceof ElectronicsDTO electronics) {
            Electronics electronicsItem = new Electronics(electronics.getTitle(), electronics.getDescription(),
                    electronics.getPrice(), user, electronics.getElectronisType());
            savedItem = itemService.saveItem(electronicsItem);
        } else if (itemDTO instanceof PetDTO pet) {
            Pet petItem = new Pet(pet.getTitle(), pet.getDescription(), pet.getPrice(), pet.getSpecies(), user);
            savedItem = itemService.saveItem(petItem);
        } else if (itemDTO instanceof HomeDTO home) {
            Home homeItem = new Home(home.getTitle(), home.getDescription(), home.getPrice(), user, home.getHomeType());
            savedItem = itemService.saveItem(homeItem);
        } else if (itemDTO instanceof EntertainmentDTO entertainment) {
            Entertainment entertainmentItem = new Entertainment(entertainment.getTitle(), entertainment.getDescription(), entertainment.getPrice(), user, entertainment.getEntreainmentType());
            savedItem = itemService.saveItem(entertainmentItem);
        }
        if (savedItem == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }

        return ResponseEntity.ok(savedItem.getId());
    }

    @PutMapping(value = "/itemImage", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Void> updateItemImage(
            @RequestParam("itemId") long itemId,
            @RequestPart("images") List<MultipartFile> images) {
    
        try {
            
            itemService.uploadItemImages(itemId, images);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            if ("Item not found".equals(e.getMessage())) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
            }
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
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

    @GetMapping("/item/{id}")
    public ResponseEntity<ItemDTO> getItemById(@PathVariable long id) {
        try {
            Item item = itemService.getItemById(id);
            return ResponseEntity.ok(item.toDTO());
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
                clothesDTOs.add((ClothesDTO) c.toDTO());
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
                clothesDTOs.add((ClothesDTO) c.toDTO());
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
                electronicsDTOs.add((ElectronicsDTO) e.toDTO());
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
                homeDTOs.add((HomeDTO) homeItem.toDTO());
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
                petDTOs.add((PetDTO) petItem.toDTO());
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
                entertainmentDTOs.add((EntertainmentDTO) entertainmentItem.toDTO());
            }
            return ResponseEntity.ok(entertainmentDTOs);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
    @GetMapping("/images/{nombreImagen}")
	@ResponseBody
	public ResponseEntity<Resource> showImagen(@PathVariable String nombreImagen) throws MalformedURLException {
		Path rutaArchivo = Paths.get("uploads/items").resolve(nombreImagen).toAbsolutePath();
		Resource recurso = new UrlResource(rutaArchivo.toUri());

		if (recurso.exists() && recurso.isReadable()) {
			return ResponseEntity.ok()
				.contentType(MediaType.IMAGE_JPEG)
				.body(recurso);
		} else {
			return ResponseEntity.notFound().build();
		}
	}

    @GetMapping("/{itemId}/owner")
    public ResponseEntity<User> getItemOwner(@PathVariable long itemId) {
        try {
            User owner = itemService.getItemOwner(itemId);
            return ResponseEntity.ok(owner);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }    

}
