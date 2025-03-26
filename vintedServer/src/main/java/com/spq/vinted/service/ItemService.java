package com.spq.vinted.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

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
    
}
