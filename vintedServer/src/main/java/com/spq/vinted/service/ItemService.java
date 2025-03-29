package com.spq.vinted.service;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

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


@Service
public class ItemService {

    ItemRepository itemRepository;

    public ItemService(ItemRepository itemRepository) {
        this.itemRepository = itemRepository;
    }

    public List<Item> getItems() {
        return itemRepository.findAll();
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
        uploadPath.mkdirs(); // Crea la carpeta si no existe
    }

    List<String> imageNames = new ArrayList<>(); // Para guardar los nombres de las imágenes

    for (MultipartFile itemImage : itemImages) {
        String uniqueFileName = UUID.randomUUID().toString() + "_" + itemImage.getOriginalFilename();
        Path filePath = Paths.get(uploadDir).resolve(uniqueFileName).toAbsolutePath();

        Files.copy(itemImage.getInputStream(), filePath);

        imageNames.add(uniqueFileName); // Agregar el nombre de la imagen a la lista
    }

    // Guardar la lista de imágenes en el Item
    item.setImages(imageNames); // item.setImage() debe cambiarse a una lista en la entidad
    itemRepository.save(item);
}

}
