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
import org.springframework.web.util.UriComponentsBuilder;

import com.spq.client.data.EditUser;
import com.spq.client.data.Login;
import com.spq.client.data.Pet;
import com.spq.client.data.MultipartInputStreamFileResource;
import com.spq.client.data.Signup;
import com.spq.client.data.Species;
import com.spq.client.data.User;
import com.spq.client.data.Purchase;
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
import java.util.Map;

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
			case 400 -> throw new RuntimeException("Username already exists");
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
	public List<Item> getItems(Long token) {
		try {
			if (token == null) {
				return restTemplate.exchange(apiBaseUrl + "/items/items", HttpMethod.GET, null, 
						new ParameterizedTypeReference<List<Item>>() {}).getBody();
			} else {
				return restTemplate.exchange(apiBaseUrl + "/items/items?token=" + token, HttpMethod.GET, null, 
						new ParameterizedTypeReference<List<Item>>() {}).getBody();
			}
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
	public List<Clothes> getClothes(long token) {
		try{
			if(token != -1){
				return restTemplate.exchange(apiBaseUrl + "/items/clothes?token=" + token, HttpMethod.GET, null, new ParameterizedTypeReference<List<Clothes>>() {}).getBody();
			}
			List<Clothes> clothes = restTemplate.exchange(apiBaseUrl + "/items/clothes", HttpMethod.GET, null, new ParameterizedTypeReference<List<Clothes>>() {}).getBody();
			return clothes;
		}catch (HttpStatusCodeException e) {
			throw new RuntimeException("Failed to fetch clothes: " + e.getStatusText(), e);
		}
	}

	@Override
	public List<Clothes> getClothesByCategory(Category category,long token) {
		try{
			if(token != -1){
				return restTemplate.exchange(apiBaseUrl + "/items/clothes/" + category + "?token=" + token, HttpMethod.GET, null, new ParameterizedTypeReference<List<Clothes>>() {}).getBody();
			}
			return restTemplate.exchange(apiBaseUrl + "/items/clothes/" + category, HttpMethod.GET, null, new ParameterizedTypeReference<List<Clothes>>() {}).getBody();
		} catch (HttpStatusCodeException e) {
			throw new RuntimeException("Failed to fetch clothes: " + e.getStatusText(), e);
		}
	}

	@Override
	public List<Electronics> getElectronics(long token) {
		try{
			if(token != -1){
				return restTemplate.exchange(apiBaseUrl + "/items/electronics?token=" + token, HttpMethod.GET, null, new ParameterizedTypeReference<List<Electronics>>() {}).getBody();
			}
			return restTemplate.exchange(apiBaseUrl + "/items/electronics", HttpMethod.GET, null, new ParameterizedTypeReference<List<Electronics>>() {}).getBody();
		} catch (HttpStatusCodeException e) {
			throw new RuntimeException("Failed to fetch electronics: " + e.getStatusText(), e);
		}
	}

	@Override
	public List<Home> getHomeItems(long token) {
		try{
			if(token != -1){
				return restTemplate.exchange(apiBaseUrl + "/items/home?token=" + token, HttpMethod.GET, null, new ParameterizedTypeReference<List<Home>>() {}).getBody();
			}
			return restTemplate.exchange(apiBaseUrl + "/items/home", HttpMethod.GET, null, new ParameterizedTypeReference<List<Home>>() {}).getBody();
		} catch (HttpStatusCodeException e) {
			throw new RuntimeException("Failed to fetch home items: " + e.getStatusText(), e);
		}
	}

	@Override
	public List<Pet> getItemsForPet(long token) {
		try{
			if(token != -1){
				return restTemplate.exchange(apiBaseUrl + "/items/pet?token=" + token, HttpMethod.GET, null, new ParameterizedTypeReference<List<Pet>>() {}).getBody();
			}
			return restTemplate.exchange(apiBaseUrl + "/items/pet", HttpMethod.GET, null, new ParameterizedTypeReference<List<Pet>>() {}).getBody();
		} catch (HttpStatusCodeException e) {
			throw new RuntimeException("Failed to fetch pet items: " + e.getStatusText(), e);
		}
	}

	@Override
	public List<Entertainment> getItemsForEntertainment(long token) {
		try{
			if(token != -1){
				return restTemplate.exchange(apiBaseUrl + "/items/entertainment?token=" + token, HttpMethod.GET, null, new ParameterizedTypeReference<List<Entertainment>>() {}).getBody();
			}
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
	public Purchase createPurchase(long token, Purchase purchase) {
		try {
			String url = UriComponentsBuilder.fromHttpUrl(apiBaseUrl + "/purchases/create")
					.queryParam("token", token)
					.toUriString();

			return restTemplate.postForObject(url, purchase, Purchase.class);
		} catch (HttpStatusCodeException e) {
			switch (e.getStatusCode().value()) {
				case 400 -> throw new RuntimeException("Invalid purchase request");
				case 404 -> throw new RuntimeException("Item not found");
				case 409 -> throw new RuntimeException("Purchase conflict, item already sold");
				default -> throw new RuntimeException("Failed to create purchase: " + e.getStatusText());
			}
		}
	}

	@Override
	public boolean processPayment(long purchaseId, String paymentMethod, long token) {
		String url = apiBaseUrl + "/purchases/pay?purchaseId=" + purchaseId + "&paymentMethod=" + paymentMethod + "&token=" + token;
		try {
			Map<String, String> response = restTemplate.postForObject(url, null, Map.class);
			System.out.println("Server response: " + response);
			return "success".equalsIgnoreCase(response.get("status"));
		} catch (HttpStatusCodeException e) {
			System.out.println("Error response: " + e.getResponseBodyAsString());
			switch (e.getStatusCode().value()) {
				case 400 -> throw new RuntimeException("Invalid payment request");
				case 404 -> throw new RuntimeException("Purchase not found");
				case 409 -> throw new RuntimeException("Payment conflict, already paid");
				default -> throw new RuntimeException("Failed to process payment: " + e.getStatusText());
			}
		}
	}

	@Override
	public void deleteItem(Long token, Long itemId) {
		try {
			System.out.println("borrar" + itemId);
			String url = apiBaseUrl + "/items/delete/" + itemId + "?token=" + token;
			restTemplate.delete(url);
		} catch (HttpStatusCodeException e) {
			switch (e.getStatusCode().value()) {
				case 404 -> throw new RuntimeException("Item not found");
				case 403 -> throw new RuntimeException("Not authorized to delete this item");
				default -> throw new RuntimeException("Failed to delete item: " + e.getStatusText());
			}
		}
	}

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
			String url = apiBaseUrl + "/items/itemImage?itemId=" + itemId;

			HttpHeaders headers = new HttpHeaders();
			headers.setContentType(MediaType.MULTIPART_FORM_DATA);

			MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();

			for (MultipartFile image : images) {
				if(image.isEmpty() && image.getSize() == 0) {
					continue;
				}
				body.add("images", new MultipartInputStreamFileResource(image.getInputStream(), image.getOriginalFilename()));
			}

			HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);

			restTemplate.exchange(url, HttpMethod.PUT, requestEntity, Void.class);
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

	@Override
	public Purchase getPurchaseById(Long token, Long purchaseId) {
		try {
			String url = apiBaseUrl + "/purchases/" + purchaseId + "?token=" + token;
	
			return restTemplate.getForObject(url, Purchase.class);
		} catch (HttpStatusCodeException e) {
			switch (e.getStatusCode().value()) {
				case 404 -> throw new RuntimeException("Purchase not found");
				default -> throw new RuntimeException("Failed to fetch purchase: " + e.getStatusText());
			}
		}
	}

	@Override
	public void deletePurchase(Long token, Long purchaseId) {
		try {
			String url = apiBaseUrl + "/purchases/cancel?token=" + token + "&purchaseId=" + purchaseId;
			restTemplate.delete(url);
		} catch (HttpStatusCodeException e) {
			switch (e.getStatusCode().value()) {
				case 404 -> throw new RuntimeException("Purchase not found");
				case 403 -> throw new RuntimeException("Not authorized to delete this purchase");
				default -> throw new RuntimeException("Failed to delete purchase: " + e.getStatusText());
			}
		}
	}

	@Override
	public User getUserByItemId(Long itemId) {
		try {
			String url = apiBaseUrl + "/items/" + itemId + "/owner";
			return restTemplate.getForObject(url, User.class);
		} catch (HttpStatusCodeException e) {
			switch (e.getStatusCode().value()) {
				case 404 -> throw new RuntimeException("Item not found");
				default -> throw new RuntimeException("Failed to get item owner: " + e.getStatusText());
			}
		}
	}	

	public List<Item> getUserItems(Long userId) {
		try {
			String url = apiBaseUrl + "/items/userItems/"+userId;
			return restTemplate.exchange(url, HttpMethod.GET, null, new ParameterizedTypeReference<List<Item>>() {}).getBody();
		} catch (HttpStatusCodeException e) {
			switch (e.getStatusCode().value()) {
				case 404 -> throw new RuntimeException("User not found");
				default -> throw new RuntimeException("Failed to fetch user items: " + e.getStatusText());
			}
		}
	}
	
	@Override
	public User getSeller(Item item){
		try {
			String url = apiBaseUrl + "/items/seller/" + item.getId();
			return restTemplate.getForObject(url, User.class);
		} catch (HttpStatusCodeException e) {
			switch (e.getStatusCode().value()) {
				case 404 -> throw new RuntimeException("User not found");
				default -> throw new RuntimeException("Failed to fetch seller: " + e.getStatusText());
			}
		}
	}
}
