package com.spq.client.web;

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
    public void createUser(User user);
	public Long login(String email, String password);
	public List<Item> getItems();
	public List<Clothes> getClothes();
	public List<Clothes> getClothesByCategory(Category category);
	public List<Electronics> getElectronics();
	public List<Home> getHomeItems();
	public List<Pet> getItemsForPet();
	public List<Entertainment> getItemsForEntertainment();
	public void logout(long token);
}
