package com.spq.client.web;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import com.spq.client.data.EditUser;
import com.spq.client.data.Login;
import com.spq.client.data.Pet;
import com.spq.client.data.MultipartInputStreamFileResource;
import com.spq.client.data.Signup;
import com.spq.client.data.Species;
import com.spq.client.data.User;
import com.spq.client.data.Item;
import com.spq.client.data.Category;
import com.spq.client.data.Clothes;
import com.spq.client.data.ClothesSize;
import com.spq.client.data.ClothesType;
import com.spq.client.data.Electronics;
import com.spq.client.data.ElectronicsType;
import com.spq.client.data.Entertainment;
import com.spq.client.data.EntertainmentType;
import com.spq.client.data.Home;
import com.spq.client.data.HomeType;

import java.util.List;

@Service
public class ServiceProxy implements IVintedServiceProxy {
    RestTemplate restTemplate;

	@Value("${api.base.url}")
	private String apiBaseUrl;

	public ServiceProxy(RestTemplate restTemplate) {
		this.restTemplate = restTemplate;
	}

    @Override
	public void createUser(Signup user) {
		try {
			restTemplate.postForObject(apiBaseUrl + "/users/signup", user, Void.class);
		} catch (HttpStatusCodeException e) {
			switch (e.getStatusCode().value()) {
			case 409 -> throw new RuntimeException("User already exists");
			case 403 -> throw new RuntimeException("Invalid credentials");
			default -> throw new RuntimeException("Failed to create user: " + e.getStatusText());
			}
		}
	}

	@Override
	public Long login(String email, String password) {
		Login login = new Login(email, password);
		try {
			Long token = restTemplate.postForObject(apiBaseUrl + "/users/login", login, Long.class);
			return token;
		} catch (HttpStatusCodeException e) {
			switch (e.getStatusCode().value()) {
			case 403 -> throw new RuntimeException("Invalid credentials");
			case 404 -> throw new RuntimeException("User not found");
			default -> throw new RuntimeException("Failed to login: " + e.getStatusText());
			}
		}
	}
	@Override
	public List<Item> getItems() {
		try{
			return restTemplate.exchange(apiBaseUrl + "/items/items", HttpMethod.GET, null, new ParameterizedTypeReference<List<Item>>() {}).getBody();
    	} catch (HttpStatusCodeException e) {
        	throw new RuntimeException("Failed to fetch items: " + e.getStatusText(), e);
    	}
	}

	@Override
	public Item getItemById(Long id) {
		try{
			Item item = restTemplate.getForObject(apiBaseUrl + "/items/item/" + id, Item.class);
			return item;
		} catch (HttpStatusCodeException e) {
			switch (e.getStatusCode().value()) {
			case 404 -> throw new RuntimeException("Item not found");
			default -> throw new RuntimeException("Failed to fetch item: " + e.getStatusText());
			}
		}
	}

	@Override
	public List<Clothes> getClothes() {
		try{
			List<Clothes> clothes = restTemplate.exchange(apiBaseUrl + "/items/clothes", HttpMethod.GET, null, new ParameterizedTypeReference<List<Clothes>>() {}).getBody();
			return clothes;
		}catch (HttpStatusCodeException e) {
			throw new RuntimeException("Failed to fetch clothes: " + e.getStatusText(), e);
		}
	}

	@Override
	public List<Clothes> getClothesByCategory(Category category) {
		try{
			return restTemplate.exchange(apiBaseUrl + "/items/clothes/" + category, HttpMethod.GET, null, new ParameterizedTypeReference<List<Clothes>>() {}).getBody();
		} catch (HttpStatusCodeException e) {
			throw new RuntimeException("Failed to fetch clothes: " + e.getStatusText(), e);
		}
	}

	@Override
	public List<Electronics> getElectronics() {
		try{
			return restTemplate.exchange(apiBaseUrl + "/items/electronics", HttpMethod.GET, null, new ParameterizedTypeReference<List<Electronics>>() {}).getBody();
		} catch (HttpStatusCodeException e) {
			throw new RuntimeException("Failed to fetch electronics: " + e.getStatusText(), e);
		}
	}

	@Override
	public List<Home> getHomeItems() {
		try{
			return restTemplate.exchange(apiBaseUrl + "/items/home", HttpMethod.GET, null, new ParameterizedTypeReference<List<Home>>() {}).getBody();
		} catch (HttpStatusCodeException e) {
			throw new RuntimeException("Failed to fetch home items: " + e.getStatusText(), e);
		}
	}

	@Override
	public List<Pet> getItemsForPet() {
		try{
			return restTemplate.exchange(apiBaseUrl + "/items/pet", HttpMethod.GET, null, new ParameterizedTypeReference<List<Pet>>() {}).getBody();
		} catch (HttpStatusCodeException e) {
			throw new RuntimeException("Failed to fetch pet items: " + e.getStatusText(), e);
		}
	}

	@Override
	public List<Entertainment> getItemsForEntertainment() {
		try{
			return restTemplate.exchange(apiBaseUrl + "/items/entertainment", HttpMethod.GET, null, new ParameterizedTypeReference<List<Entertainment>>() {}).getBody();
		} catch (HttpStatusCodeException e) {
			throw new RuntimeException("Failed to fetch entertainment items: " + e.getStatusText(), e);
		}
	}

	@Override
	public void logout(long token) {
		try {
			restTemplate.postForObject(apiBaseUrl + "/users/logout?token=" + token, null, Void.class);
		} catch (HttpStatusCodeException e) {
			switch (e.getStatusCode().value()) {
			case 404 -> throw new RuntimeException("User not found");
			default -> throw new RuntimeException("Failed to log out: " + e.getStatusText());
			}
		}
	}

	@Override
	public void deleteUser(long token) {
		try {
			restTemplate.delete(apiBaseUrl + "/users/delete?token=" + token);
		} catch (HttpStatusCodeException e) {
			switch (e.getStatusCode().value()) {
			case 404 -> throw new RuntimeException("User not found");
			default -> throw new RuntimeException("Failed to delete user: " + e.getStatusText());
			}
		}
	}

	@Override
	public void updateUser(long token, String name, String surname, String description, MultipartFile profileImage) {
		updateUserData(token, name, surname, description);
		if (profileImage != null && !profileImage.isEmpty()) {
			updateProfileImage(token, profileImage);
		}
	}
	
	public void updateUserData(long token, String name, String surname, String description) {
		EditUser editUser = new EditUser(name, surname, description);
		try {
			restTemplate.put(apiBaseUrl + "/users/editUserData?token=" + token, editUser);
		} catch (HttpStatusCodeException e) {
			switch (e.getStatusCode().value()) {
				case 404:
					throw new RuntimeException("User not found");
				default:
					throw new RuntimeException("Failed to update user: " + e.getStatusText());
			}
		}
	}
	
	public void updateProfileImage(long token, MultipartFile profileImage) {
		try {
			String url = apiBaseUrl + "/users/editProfileImage?token=" + token;
	
			HttpHeaders headers = new HttpHeaders();
			headers.setContentType(MediaType.MULTIPART_FORM_DATA);
	
			MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
			body.add("profileImage", new MultipartInputStreamFileResource(profileImage.getInputStream(), profileImage.getOriginalFilename()));
	
			HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);
	
			restTemplate.exchange(url, HttpMethod.PUT, requestEntity, Void.class);
		} catch (HttpStatusCodeException e) {
			if (e.getStatusCode().value() == 404) {
				throw new RuntimeException("User not found");
			} else {
				throw new RuntimeException("Failed to update user: " + e.getStatusText());
			}
		} catch (IOException e) {
			throw new RuntimeException("Error reading image file", e);
		}
	}
	


	@Override
	public User getUser(long id, long token) {
		try {
			User user = restTemplate.getForObject(apiBaseUrl + "/users/profile/"+id+"?token=" + token, User.class);
			return user;
		} catch (HttpStatusCodeException e) {
			switch (e.getStatusCode().value()) {
			case 404 -> throw new RuntimeException("User not found");
			default -> throw new RuntimeException("Failed to get user: " + e.getStatusText());
			}
		}
	}

	@Override
	public Long getUserIdFromToken(Long token) {
		try {
			Long id = restTemplate.getForObject(apiBaseUrl + "/users/userId?token=" + token, Long.class);
			return id;
		} catch (HttpStatusCodeException e) {
			switch (e.getStatusCode().value()) {
			case 404 -> throw new RuntimeException("User not found");
			default -> throw new RuntimeException("Failed to get user: " + e.getStatusText());
			}
		}
	}

	@Override
	public void uploadItem(long token, String title, String description, String category, float price, String brand, String size, String clothCategory, String clothingType, String species, String homeType, String electronicsType, String entertainmentType, List<MultipartFile> images){
		long itemId = uploadItemData(token, title, description, category, price, brand, size, clothCategory, clothingType, species, homeType, electronicsType, entertainmentType);
		uploadItemImage(itemId, images);
	}
	@Override
	public long uploadItemData(long token, String title, String description, String category, float price, String brand, String size, String clothCategory, String clothingType, String species, String homeType, String electronicsType, String entertainmentType) {
		System.out.println("Uploading item data...");
		
		String url = apiBaseUrl + "/items/itemData?token=" + token;
		
		Item item = switch (category.toLowerCase()) {
			case "clothes" -> new Clothes(title, description, price, ClothesSize.L, Category.WOMAN, ClothesType.SWEATER);
			case "electronics" -> new Electronics(title, description, price, ElectronicsType.DEVICE);
			case "home" -> new Home(title, description, price, HomeType.DECORATION);
			case "pet" -> new Pet(title, description, price, Species.valueOf(species.toUpperCase()));
			case "entertainment" -> new Entertainment(title, description, price, EntertainmentType.BOOK);
			default -> throw new RuntimeException("Invalid category");
		};
		
		try {
			Long itemId = restTemplate.postForObject(url, item, Long.class);
			if (itemId == null) {
				throw new RuntimeException("Failed to get item ID");
			}
			return itemId;
		} catch (HttpStatusCodeException e) {
			switch (e.getStatusCode().value()) {
				case 404 -> throw new RuntimeException("User not found");
				default -> throw new RuntimeException("Failed to upload item: " + e.getStatusText());
			}
		}
	}

	@Override
	public void uploadItemImage(long itemId, List<MultipartFile> images) {
		try {
			System.out.println("Uploading item images...");
			String url = apiBaseUrl + "/items/itemImage?itemId=" + itemId;

			HttpHeaders headers = new HttpHeaders();
			headers.setContentType(MediaType.MULTIPART_FORM_DATA);

			MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();

			for (MultipartFile image : images) {
				if(image.isEmpty() && image.getSize() == 0) {
					continue;
				}
				body.add("images", new MultipartInputStreamFileResource(image.getInputStream(), image.getOriginalFilename()));
				System.out.println("Image name: " + image.getOriginalFilename());
			}

			HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);
			System.out.println("Request entity: " + requestEntity);

			restTemplate.exchange(url, HttpMethod.PUT, requestEntity, Void.class);
			System.out.println("Images uploaded successfully for item ID: " + itemId);
		} catch (HttpStatusCodeException e) {
			if (e.getStatusCode().value() == 404) {
				throw new RuntimeException("Item not found");
			} else {
				throw new RuntimeException("Failed to upload item images: " + e.getStatusText());
			}
		} catch (IOException e) {
			throw new RuntimeException("Error reading image file", e);
		}
	}

	@Override
	public void addItemToCart(Long token, Long itemId) {
		try {
			String url = apiBaseUrl + "/items/shoppingCart/add?token=" + token + "&itemId=" + itemId;
			HttpHeaders headers = new HttpHeaders();
			headers.setContentType(MediaType.APPLICATION_JSON);			

			HttpEntity<Void> requestEntity = new HttpEntity<>(headers);

			restTemplate.postForObject(url, requestEntity, Void.class);
		} catch (HttpStatusCodeException e) {
			switch (e.getStatusCode().value()) {
				case 404 -> throw new RuntimeException("Item or user not found");
				default -> throw new RuntimeException("Failed to add item to cart: " + e.getStatusText());
			}
		}
	}

	@Override
	public List<Item> getCartItems(Long token) {
		try {
			String url = apiBaseUrl + "/items/shoppingCart?token=" + token;
			return restTemplate.exchange(url, HttpMethod.GET, null, new ParameterizedTypeReference<List<Item>>() {}).getBody();
		} catch (HttpStatusCodeException e) {
			switch (e.getStatusCode().value()) {
				case 404 -> throw new RuntimeException("User not found");
				default -> throw new RuntimeException("Failed to fetch cart items: " + e.getStatusText());
			}
		}
	}

	@Override
	public void removeItemFromCart(Long token, Long itemId) {
		try {
			String url = apiBaseUrl + "/items/shoppingCart/remove?token=" + token + "&itemId=" + itemId;
			HttpHeaders headers = new HttpHeaders();
			headers.setContentType(MediaType.APPLICATION_JSON);

			HttpEntity<Void> requestEntity = new HttpEntity<>(headers);

			restTemplate.postForObject(url, requestEntity, Void.class);
		} catch (HttpStatusCodeException e) {
			switch (e.getStatusCode().value()) {
				case 404 -> throw new RuntimeException("Item or user not found");
				default -> throw new RuntimeException("Failed to remove item from cart: " + e.getStatusText());
			}
		}
	}

}
