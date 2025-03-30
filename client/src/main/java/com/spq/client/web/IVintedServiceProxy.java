package com.spq.client.web;

import org.springframework.web.multipart.MultipartFile;

import com.spq.client.data.Signup;
import com.spq.client.data.User;
import com.spq.client.data.Item;
import com.spq.client.data.Pet;
import com.spq.client.data.Category;
import com.spq.client.data.Clothes;
import com.spq.client.data.Electronics;
import com.spq.client.data.Entertainment;
import com.spq.client.data.Home;

import java.util.List;

public interface IVintedServiceProxy {
    public void createUser(Signup user);
	public Long login(String email, String password);
	public List<Item> getItems();
	public Item getItemById(Long id);
	public List<Clothes> getClothes();
	public List<Clothes> getClothesByCategory(Category category);
	public List<Electronics> getElectronics();
	public List<Home> getHomeItems();
	public List<Pet> getItemsForPet();
	public List<Entertainment> getItemsForEntertainment();
	public void logout(long token);
	public void deleteUser(long token);
	public void updateUser(long token, String name, String surname, String description, MultipartFile profileImage);
	public User getUser(long id, long token);
	public Long getUserIdFromToken(Long token);
	public void uploadItem(long token, String title, String description, String category, float price, String brand, String size, String clothCategory, String clothingType, String species, String homeType, String electronicsType, String entertainmentType, List<MultipartFile> images);
	public long uploadItemData(long token, String title, String description, String category, float price, String brand, String size, String clothCategory, String clothingType, String species, String homeType, String electronicsType, String entertainmentType);
	public void uploadItemImage(long token, List<MultipartFile> images);
	public List<Item> getCartItems(Long token);
	public void addItemToCart(Long token, Long itemId);
	public void removeItemFromCart(Long token, Long itemId);
}
