package com.spq.client.web;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.spq.client.data.Ad;
import com.spq.client.data.Category;
import com.spq.client.data.ChatRoomInfo;
import com.spq.client.data.Item;
import com.spq.client.data.Offer;
import com.spq.client.data.Pet;
import com.spq.client.data.Purchase;
import com.spq.client.data.Rating;
import com.spq.client.data.Clothes;
import com.spq.client.data.Electronics;
import com.spq.client.data.Entertainment;
import com.spq.client.data.Home;
import com.spq.client.data.User;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import com.spq.client.data.Signup;
import java.util.stream.Collectors;

import jakarta.servlet.http.HttpServletRequest;

@Controller
public class ClientController {
@Autowired
	IVintedServiceProxy vintedService;

	private Long token = null; 
	private Long userId = null;
    @ModelAttribute
	public void addAttributes(Model model, HttpServletRequest request) {

		String currentUrl = ServletUriComponentsBuilder.fromRequestUri(request).toUriString();
		model.addAttribute("currentUrl", currentUrl);
		if (token != null) {
			model.addAttribute("token", token);
			model.addAttribute("loggedUserId", userId);
		}
		model.addAttribute("profileImageBaseUrl", "http://localhost:8080/users/profile/imagen/");
		model.addAttribute("itemImageBaseUrl", "http://localhost:8080/items/images/");
		model.addAttribute("adsImageBaseUrl", "http://localhost:8080/ads/images/");
		
		// Manejo del carrito
		if (token != null) {
			if (vintedService.getCartItems(token) != null) {
				model.addAttribute("cartSize", vintedService.getCartItems(token).size());
			} else {
				model.addAttribute("cartSize", 0);
			}
		} else {
			model.addAttribute("cartSize", 0);
		}

		// Manejo de la wishlist
		if (token != null) {
			if (vintedService.getWishlistItems(token) != null) {
				model.addAttribute("wishlistSize", vintedService.getWishlistItems(token).size());
			} else {
				model.addAttribute("wishlistSize", 0);
			}
		} else {
			model.addAttribute("wishlistSize", 0);
		}
	}
	
	@GetMapping("/register")
	public String showRegisterPage(
			@RequestParam(value = "redirectUrl", required = false) String redirectUrl,
			Model model) {
		if (redirectUrl == null || redirectUrl.isEmpty() || redirectUrl.equals("null")) {
			redirectUrl = "/allItems";
			if (token != null) {
				redirectUrl += "?token=" + token;
			}
		}
		model.addAttribute("redirectUrl", redirectUrl);
		return "register";
	}
	
	@PostMapping("/register")
	public String register(@RequestParam(value = "email") String email,
			@RequestParam(value = "password") String password,
			@RequestParam(value = "username") String username,
			@RequestParam(value = "name") String name,
			@RequestParam(value = "surname") String surname,
			@RequestParam(value = "redirectUrl") String redirectUrl,
			Model model,
			RedirectAttributes redirectAttributes) {
		try {
			if (redirectUrl == null || redirectUrl.isEmpty() || redirectUrl.equals("null")) {
				redirectUrl = "/allItems";
			}
			model.addAttribute("redirectUrl", redirectUrl);
			vintedService.createUser(new Signup(email, password, username, name, surname));
			token = vintedService.login(email, password);
			userId = vintedService.getUserIdFromToken(token);
			if (!redirectUrl.contains("token=")) {
				if (redirectUrl.contains("?")) {
					redirectUrl += "&token=" + token;
				} else {
					redirectUrl += "?token=" + token;
				}
			}
			return "redirect:" + redirectUrl;
		} catch (RuntimeException e) {
			if (e.getMessage().equals("User already exists")) {
				redirectAttributes.addFlashAttribute("errorMessage", "El email ya existe");
			} else if (e.getMessage().equals("Username already exists")) {
                redirectAttributes.addFlashAttribute("errorMessage", "El nombre de usuario ya existe");
			} else if (e.getMessage().equals("Invalid credentials")) {
                redirectAttributes.addFlashAttribute("errorMessage", "La contraseña es incorrecta");
			}else {
				redirectAttributes.addFlashAttribute("errorMessage", "Error inesperado");
				e.printStackTrace();
			}
		}
		return "redirect:/register?redirectUrl=" + redirectUrl;
	}
	
    @GetMapping("/login")
	public String showLoginPage(
			@RequestParam(value = "redirectUrl", required = false) String redirectUrl,
			Model model) {
		if (redirectUrl == null || redirectUrl.isEmpty() || redirectUrl.equals("null")) {
			redirectUrl = "/allItems";
			if (token != null) {
				redirectUrl += "?token=" + token;
			}
		}
		model.addAttribute("redirectUrl", redirectUrl);
		return "login";
	}

	@PostMapping("/login")
	public String login(
			@RequestParam(value = "email") String email,
			@RequestParam(value = "password") String password,
			@RequestParam(value = "redirectUrl") String redirectUrl,
			Model model,
			RedirectAttributes redirectAttributes) {
		try {
			if ("admin@admin".equals(email) && "admin".equals(password)) {
				long adminToken = System.currentTimeMillis(); 
				return "redirect:/uploadAd?token=" + adminToken;
			}

			if (redirectUrl == null || redirectUrl.isEmpty() || redirectUrl.equals("null")) {
				redirectUrl = "/allItems";
			}
			model.addAttribute("redirectUrl", redirectUrl);
			token = vintedService.login(email, password);
			userId = vintedService.getUserIdFromToken(token);
			if (!redirectUrl.contains("token=")) {
				if (redirectUrl.contains("?")) {
					redirectUrl += "&token=" + token;
				} else {
					redirectUrl += "?token=" + token;
				}
			}
			return "redirect:" + redirectUrl;
		} catch (RuntimeException e) {
			if (e.getMessage().equals("Invalid credentials")) {
				redirectAttributes.addFlashAttribute("errorMessage", "La contraseña no es correcta");
			} else if (e.getMessage().equals("User not found")) {
				redirectAttributes.addFlashAttribute("errorMessage", "El usuario no existe");
			} else {
				redirectAttributes.addFlashAttribute("errorMessage", "Error inesperado");
			}
		}
		return "redirect:/login?redirectUrl=" + redirectUrl;
	}

	@GetMapping("/allItems")
	public String getItems(
			@RequestParam(value = "token", required = false) Long token,
			@RequestParam(value = "redirectUrl", required = false) String redirectUrl,
			Model model) {
		try {
			List<Item> items = vintedService.getItems(token); 
			List<Ad> ads = vintedService.getAllAds();
			model.addAttribute("items", items);
			model.addAttribute("ads", ads);
			return "product";
		} catch (RuntimeException e) {
			System.err.println("Ha ocurrido un error: " + e.getMessage());
			e.printStackTrace();
			return "allItems";
		}
	}

	@GetMapping("/item/{id}")
	public String getItemById(
			@RequestParam(value = "token", required = false) Long token,
			@PathVariable Long id,
			@RequestParam(value = "redirectUrl", required = false) String redirectUrl,
			Model model) {
		try {
			Item item = vintedService.getItemById(id);
			Long sellerId = vintedService.getSeller(item).id();
			User seller = vintedService.getSeller(item);

			model.addAttribute("item", item);
			model.addAttribute("sellerId", sellerId);
			model.addAttribute("seller", seller);
			model.addAttribute("profileImageBaseUrl", "http://localhost:8080/users/profile/imagen/");

			return "product-details"; 
		} catch (RuntimeException e) {
			System.err.println("Ha ocurrido un error: " + e.getMessage());
			e.printStackTrace();
		}
		return null;
	}

	@GetMapping("/clothes")
	public String getClothes(
		@RequestParam(value = "token", required = false) Long token,
		@RequestParam(value = "redirectUrl", required = false) String redirectUrl,
		Model model) {
		try {
			List<Clothes> clothes = null;
			if(token == null) {
				clothes = vintedService.getClothes(-1);
			}else {
				clothes = vintedService.getClothes(token);
			}
			model.addAttribute("items", clothes);
			return "product"; 
		} catch (RuntimeException e) {
			System.err.println("Ha ocurrido un error: " + e.getMessage());
			e.printStackTrace();
		}
		return "product";
	}

	@GetMapping("/clothes/{category}")
	public String getClothesByCategory(
		@RequestParam(value = "token", required = false) Long token,
		@PathVariable Category category,
		@RequestParam(value = "redirectUrl", required = false) String redirectUrl,
		Model model) {
			try {
		List<Clothes> clothesCategory = null;
		if(token == null) {
			clothesCategory = vintedService.getClothesByCategory(category, -1);
		}else {
			clothesCategory = vintedService.getClothesByCategory(category, token);
		}
		model.addAttribute("items", clothesCategory);
		return "product";
    } catch (RuntimeException e) {
        System.err.println("Ha ocurrido un error: " + e.getMessage());
        e.printStackTrace();
    }
    return null;
}

	@GetMapping("/electronics")
	public String getElectronics(
		@RequestParam(value = "token", required = false) Long token,
		@RequestParam(value = "redirectUrl", required = false) String redirectUrl,
		Model model) {
		try {
			List<Electronics> electronics = null;
			if(token == null) {
				electronics = vintedService.getElectronics(-1);
			}else {
				electronics = vintedService.getElectronics(token);
			}
			model.addAttribute("items", electronics);
			return "product"; 
		} catch (RuntimeException e) {
			System.err.println("Ha ocurrido un error: " + e.getMessage());
			e.printStackTrace();
		}
		return null;
	}
	
	@GetMapping("/pet")
	public String getItemsForPet(
		@RequestParam(value = "token", required = false) Long token,
		@RequestParam(value = "redirectUrl", required = false) String redirectUrl,
		Model model) {
		try {
			List<Pet> pets = null;
			if(token == null) {
				pets = vintedService.getItemsForPet(-1);
			}else {
				pets = vintedService.getItemsForPet(token);
			}
			model.addAttribute("items", pets);
			return "product"; 
		} catch (RuntimeException e) {
			System.err.println("Ha ocurrido un error: " + e.getMessage());
			e.printStackTrace();
		}
		return null;
	}

	@GetMapping("/entertainment")
	public String getItemsForEntertainment(
		@RequestParam(value = "token", required = false) Long token,
		@RequestParam(value = "redirectUrl", required = false) String redirectUrl,
		Model model
	) {
		try {
			List<Entertainment> entertainment = null;
			if (token == null) {
				entertainment = vintedService.getItemsForEntertainment(-1);
			} else {
				entertainment = vintedService.getItemsForEntertainment(token);	
			}
			model.addAttribute("items", entertainment);
			return "product"; 
		} catch (RuntimeException e) {
			System.err.println("Ha ocurrido un error: " + e.getMessage());
			e.printStackTrace();
		}
		return null;
	}

	@GetMapping("/home")
	public String getHomeItems(
		@RequestParam(value = "token", required = false) Long token,
		@RequestParam(value = "redirectUrl", required = false) String redirectUrl,
		Model model
		) {
			try {
			List<Home> homeItems = null;
			if(token == null) {
				homeItems = vintedService.getHomeItems(-1);
			}else {
				homeItems = vintedService.getHomeItems(token);
			}
			model.addAttribute("items", homeItems);
			return "product"; 
		} catch (RuntimeException e) {
			System.err.println("Ha ocurrido un error: " + e.getMessage());
			e.printStackTrace();
		}
		return null;
	}
	
	@GetMapping("/logout")
	public String logout(
			@RequestParam(value="token") Long token,
			@RequestParam(value = "redirectUrl", required = false) String redirectUrl,
			Model model) {
		try {
			this.token = null;
			this.userId = null;
			vintedService.logout(token);
		} catch (RuntimeException e) {
			System.err.println("Ha ocurrido un error: " + e.getMessage());
            e.printStackTrace();
        }
		
		return "redirect:/login?redirectUrl=" + redirectUrl;
	}

	@GetMapping("/deleteUser")
	public String deleteUser(
			@RequestParam(value="token") Long token,
			@RequestParam(value = "redirectUrl", required = false) String redirectUrl,
			Model model) {
				try {
			this.token = null;
			vintedService.deleteUser(token);
		} catch (RuntimeException e) {
			e.printStackTrace();
		}
		return "redirect:login";
	}

	@GetMapping("/userProfile/{id}")
	public String showUserProfile(
			@PathVariable("id") Long id,
			@RequestParam(value = "token") Long token,
			@RequestParam(value = "redirectUrl", required = false) String redirectUrl,
			Model model,
			RedirectAttributes redirectAttributes) {
		if (redirectUrl == null) {
			redirectUrl = "/";
		}

		return loadUserProfile(id, token, model, redirectAttributes);
	}
	
	@PostMapping("/editUser")
	public String editUser(
			@RequestParam("token") Long token,
			@RequestParam("name") String name,
			@RequestParam("surname") String surname,
			@RequestParam(value = "description", required = false) String description,
			@RequestParam(value = "profileImage", required = false) MultipartFile profileImage,
			@RequestParam(value = "redirectUrl", required = false) String redirectUrl,
			RedirectAttributes redirectAttributes) {
				
		if (redirectUrl == null) {
			redirectUrl = "/";
		}

		try {
			vintedService.updateUser(token, name, surname, description, profileImage);
			return "redirect:" + redirectUrl+"?token="+token;
		} catch (RuntimeException e) {
			if ("User not found".equals(e.getMessage())) {
				redirectAttributes.addFlashAttribute("errorMessage", "Usuario no encontrado.");
			} else {
				redirectAttributes.addFlashAttribute("errorMessage", "Error inesperado.");
			}
			return "redirect:" + redirectUrl+"?token="+token;
		}
	}

	@GetMapping("/editUser/{id}")
	public String showEditUser(
			@PathVariable("id") Long id,
			@RequestParam("token") Long token,
			@RequestParam(value = "redirectUrl", required = false) String redirectUrl,
			Model model) {
		if (redirectUrl == null) {
			redirectUrl = "/";
		}
		if(!id.equals(userId)) {
			return "redirect:/";
		}
		model.addAttribute("user", vintedService.getUser(id, token));
		model.addAttribute("redirectUrl", redirectUrl);
		return "editUser";
	}

	@GetMapping("/uploadItem")
	public String showUploadItem(
			@RequestParam(value = "token", required = false) Long token,
			@RequestParam(value = "redirectUrl", required = false) String redirectUrl,
			Model model) {
		if (redirectUrl == null) {
			redirectUrl = "/";
		}
		if (token == null) {
			return "redirect:/login";
		}
		model.addAttribute("redirectUrl", redirectUrl);

		System.out.println("redirectUrl del get: " + redirectUrl);
		model.addAttribute("redirectUrl", redirectUrl);
		return "uploadItem";
	}
	
	@PostMapping("/uploadItem")
	public String uploadItem(
			@RequestParam("token") Long token,
			@RequestParam("title") String title,
			@RequestParam("description") String description,
			@RequestParam("category") String category,
			@RequestParam("price") Float price,
			@RequestParam(value = "brand", required = false) String brand,
			@RequestParam(value = "size", required = false) String size,
			@RequestParam(value = "clothCategory", required = false) String clothCategory,
			@RequestParam(value = "clothingType", required = false) String clothingType,
			@RequestParam(value = "species", required = false) String species,
			@RequestParam(value = "homeType", required = false) String homeType,
			@RequestParam(value = "electronicsType", required = false) String electronicsType,
			@RequestParam(value = "entertainmentType", required = false) String entertainmentType,
			@RequestParam(value = "redirectUrl", required = false) String redirectUrl,
			@RequestParam(value = "itemImages") List<MultipartFile> itemImages,
			RedirectAttributes redirectAttributes,
			Model model) {

		if (redirectUrl == null) {
			redirectUrl = "/";
		}

		try {
			if(token!= null) {
				redirectUrl += "?token=" + token;
			}
			model.addAttribute("redirectUrl", redirectUrl);
			System.out.println("redirectUrl del post: " + redirectUrl);

			vintedService.uploadItem(token, title, description, category, price, brand, size, clothCategory, clothingType, species, homeType, electronicsType, entertainmentType, itemImages);
			return "redirect:" + redirectUrl;
		} catch (RuntimeException e) {
			if ("User not found".equals(e.getMessage())) {
				redirectAttributes.addFlashAttribute("errorMessage", "Usuario no encontrado.");
			} else {
				redirectAttributes.addFlashAttribute("errorMessage", "Error inesperado.");
			}
			return "redirect:" + redirectUrl;
		}
	} 

	@GetMapping("/shoppingCart")
	public String showCart(
			@RequestParam("token") Long token,
			@RequestParam(value = "redirectUrl", required = false) String redirectUrl,
			Model model) {
		if (token == null) {
			return "redirect:/login";
		}
		try {
			List<Item> cartItems = vintedService.getCartItems(token);
			if(cartItems == null || cartItems.isEmpty()) {
				cartItems = List.of(); 
			}
			model.addAttribute("cartItems", cartItems);
			model.addAttribute("totalPrice",String.format("%.2f", cartItems.stream().mapToDouble(Item::getPrice).sum()));
			return "shoppingCart";
		} catch (RuntimeException e) {
			model.addAttribute("errorMessage", "Error al cargar el carrito.");
			e.printStackTrace();
			return "error"; 
		}
	}

	@GetMapping("/wishlist")
	public String showWishlist(
			@RequestParam("token") Long token,
			@RequestParam(value = "redirectUrl", required = false) String redirectUrl,
			Model model) {
		if (token == null) {
			return "redirect:/login";
		}
		try {
			List<Item> wishlistItems = vintedService.getWishlistItems(token);
			if (wishlistItems == null || wishlistItems.isEmpty()) {
				wishlistItems = List.of();
			}
			model.addAttribute("wishlistItems", wishlistItems);
			return "wishlist";
		} catch (RuntimeException e) {
			model.addAttribute("errorMessage", "Error al cargar la wishlist.");
			e.printStackTrace();
			return "error";
		}
	}
	
	@GetMapping("/createPurchase/{itemId}")
	public String showPurchasePage(
			@PathVariable Long itemId,
			@RequestParam("token") Long token,
			@RequestParam(value = "offerId", required = false) Long offerId,
			Model model,
			RedirectAttributes redirectAttributes) {
				try {

			Long buyerId = vintedService.getUserIdFromToken(token);
			if (buyerId == null) {
				redirectAttributes.addFlashAttribute("errorMessage", "Debes iniciar sesión para comprar.");
				return "redirect:/login";
			}
	
			Item item = vintedService.getItemById(itemId);
			if (item == null) {
				redirectAttributes.addFlashAttribute("errorMessage", "El artículo no existe.");
				return "redirect:/login";
			}
			if(offerId != null) {
				Offer offer = vintedService.getOfferById(offerId);
				model.addAttribute("offer", offer);
			}
			model.addAttribute("item", item);
			model.addAttribute("buyerId", buyerId);
			model.addAttribute("token", token);
	
			return "purchase";  
		} catch (Exception e) {
			redirectAttributes.addFlashAttribute("errorMessage", "Ocurrió un error al mostrar la compra.");
			return "redirect:/login";
		}
	}
	
	

	@GetMapping("/createMultiplePurchase")
	public String showPurchasePage(
			@RequestParam("itemIds") List<Long> itemIds,
			@RequestParam("token") Long token,
			Model model,
			RedirectAttributes redirectAttributes) {
		try {
			Long buyerId = vintedService.getUserIdFromToken(token);
			if (buyerId == null) {
				redirectAttributes.addFlashAttribute("errorMessage", "Debes iniciar sesión para comprar.");
				return "redirect:/login";
			}
	
			List<Item> items = new ArrayList<>();
			for (Long itemId : itemIds) {
				Item item = vintedService.getItemById(itemId);
				if (item == null) {
					redirectAttributes.addFlashAttribute("errorMessage", "Uno o más artículos no existen.");
					return "redirect:/allItems";
				}
				items.add(item);
			}
	
			model.addAttribute("items", items);
			model.addAttribute("buyerId", buyerId);
			model.addAttribute("token", token);
			System.out.println("Token: " + token);
			return "purchase";
		} catch (Exception e) {
			redirectAttributes.addFlashAttribute("errorMessage", "Ocurrió un error al mostrar la compra.");
			return "redirect:/allItems";  
		}
	}
		
	@PostMapping("/createPurchase/{itemId}")
	public String createPurchase(
		@PathVariable Long itemId,
			@RequestParam("token") Long token,
			@RequestParam(value = "offerId", required = false) Long offerId,
			@RequestParam("paymentMethod") String paymentMethod,
			RedirectAttributes redirectAttributes) {
		try {
			Long buyerId = vintedService.getUserIdFromToken(token);
			if (buyerId == null) {
				System.out.println("purchase el token es null");
				redirectAttributes.addFlashAttribute("errorMessage", "Usuario no autenticado.");
				return "redirect:/login";
			}
	
			Item item = vintedService.getItemById(itemId);
			if (item == null) {
				System.out.println("purchase el item es null");
				redirectAttributes.addFlashAttribute("errorMessage", "El artículo no existe.");
				return "redirect:/login";
			}
	
			User seller = vintedService.getSeller(item);
			if (seller == null) {
				System.out.println("purchase el seller es null");
				redirectAttributes.addFlashAttribute("errorMessage", "No se encontró el vendedor.");
				return "redirect:/login";
			}
			
			Purchase purchase = new Purchase(
					null,
					itemId,
					vintedService.getUser(buyerId, token).username(),
					seller.username(),
					(offerId != null ? vintedService.getOfferById(offerId).price().floatValue() : item.getPrice()),
					paymentMethod,
					"PENDING"
					);
					
			Purchase createdPurchase = vintedService.createPurchase(token, purchase);
	
			redirectAttributes.addFlashAttribute("successMessage", "Compra iniciada. Procede con el pago.");
			return "redirect:/processPayment/" + createdPurchase.id() + "?token=" + token;
			
		} catch (Exception e) {
			redirectAttributes.addFlashAttribute("errorMessage", "Error al crear la compra.");
			e.printStackTrace();
			return "redirect:/login";
		}
	}

	@PostMapping("/createMultiplePurchase")
	public String createPurchase(
			@RequestParam("token") Long token,
			@RequestParam("itemIds") List<Long> itemIds,
			@RequestParam("paymentMethod") String paymentMethod,
			RedirectAttributes redirectAttributes) {
		try {
			System.out.println("Token: " + token);
			Long buyerId = vintedService.getUserIdFromToken(token);
			if (buyerId == null) {
				redirectAttributes.addFlashAttribute("errorMessage", "Usuario no autenticado.");
				return "redirect:/login";
			}
	
			List<Purchase> purchases = new ArrayList<>();
			for (Long itemId : itemIds) {
				Item item = vintedService.getItemById(itemId);
				if (item == null) {
					redirectAttributes.addFlashAttribute("errorMessage", "Uno o más artículos no existen.");
					return "redirect:/login";
				}
	
				User seller = vintedService.getSeller(item);
				if (seller == null) {
					redirectAttributes.addFlashAttribute("errorMessage", "No se encontró el vendedor de un artículo.");
					return "redirect:/login";
				}
	
				Purchase purchase = new Purchase(
						null,
						itemId,
						vintedService.getUser(buyerId, token).username(),
						seller.username(),
						item.getPrice(),
						paymentMethod,
						"PENDING"
				);
				purchases.add(purchase);
			}
	
			List<Purchase> createdPurchases = vintedService.createPurchases(token, purchases);
	
			redirectAttributes.addFlashAttribute("successMessage", "Compras iniciadas. Procede con el pago.");
			return "redirect:/processMultiplePayment?purchaseIds=" + createdPurchases.stream()
					.map(p -> p.id().toString())
					.collect(Collectors.joining(",")) + "&token=" + token;
	
		} catch (Exception e) {
			redirectAttributes.addFlashAttribute("errorMessage", "Error al crear las compras.");
			e.printStackTrace();
			return "redirect:/login";
		}
	}	

	@GetMapping("/processPayment/{purchaseId}")
	public String showPaymentConfirmation(
			@PathVariable Long purchaseId,
			@RequestParam("token") Long token,
			Model model,
			RedirectAttributes redirectAttributes) {
		try {
			System.out.println("Token: " + token);
			System.out.println("Purchase ID: " + purchaseId);
			Purchase purchase = vintedService.getPurchaseById(token, purchaseId);
			System.out.println("Purchase: " + purchase);
			if (purchase == null) {
				System.out.println("a");
				redirectAttributes.addFlashAttribute("errorMessage", "Compra no encontrada.");
				return "redirect:/login";
			}
	
			model.addAttribute("purchase", purchase);
			return "paymentConfirmation";
		} catch (Exception e) {
			redirectAttributes.addFlashAttribute("errorMessage", "Error al cargar la confirmación de pago.");
			return "redirect:/login";
		}
	}

	@GetMapping("/processMultiplePayment")
	public String showPaymentConfirmation(
			@RequestParam("purchaseIds") List<Long> purchaseIds,
			@RequestParam("token") Long token,
			Model model,
			RedirectAttributes redirectAttributes) {
		try {
			System.out.println("Token: " + token);
			System.out.println("Purchase IDs: " + purchaseIds);
	
			List<Purchase> purchases = new ArrayList<>();
			for (Long purchaseId : purchaseIds) {
				Purchase purchase = vintedService.getPurchaseById(token, purchaseId);
				if (purchase == null) {
					redirectAttributes.addFlashAttribute("errorMessage", "Una o más compras no fueron encontradas.");
					return "redirect:/login";
				}
				purchases.add(purchase);
			}
	
			String purchaseIdsString = purchaseIds.stream()
					.map(String::valueOf)
					.collect(Collectors.joining(","));
	
			model.addAttribute("purchases", purchases);
			model.addAttribute("purchaseIds", purchaseIdsString);
			model.addAttribute("token", token);
	
			return "paymentConfirmation";
		} catch (Exception e) {
			redirectAttributes.addFlashAttribute("errorMessage", "Error al cargar la confirmación de pago.");
			e.printStackTrace();
			return "redirect:/login";
		}
	}

	@PostMapping("/processPayment/{purchaseId}")
	public String processPayment(
		@PathVariable Long purchaseId,
			@RequestParam("token") Long token,
			@RequestParam("paymentMethod") String paymentMethod,
			RedirectAttributes redirectAttributes,
			Model model) {
		try {
			Purchase purchase = vintedService.getPurchaseById(token, purchaseId);
			if (purchase == null) {
				redirectAttributes.addFlashAttribute("errorMessage", "Compra no encontrada.");
				return "redirect:/login";
			}
	
			if (!"PENDING".equals(purchase.status())) {
				redirectAttributes.addFlashAttribute("errorMessage", "La compra ya ha sido procesada.");
				return "redirect:/login";
			}
	
			boolean paymentSuccess = vintedService.processPayment(purchaseId, paymentMethod, token);
			if (paymentSuccess) {
				try {
					vintedService.deleteItem(token, purchase.itemId());
					redirectAttributes.addFlashAttribute("successMessage", "Pago realizado con éxito. El artículo ha sido eliminado.");
				} catch (RuntimeException e) {
					redirectAttributes.addFlashAttribute("warningMessage", "Pago realizado con éxito, pero no se pudo eliminar el artículo.");
					e.printStackTrace();
				}
				return "redirect:/allItems";
			} else {
				redirectAttributes.addFlashAttribute("errorMessage", "Error en el pago.");
			}
		} catch (Exception e) {
			redirectAttributes.addFlashAttribute("errorMessage", "Error al procesar el pago.");
			e.printStackTrace();
		}
		return "redirect:/allItems";
	}

	@PostMapping("/processMultiplePayment")
	public String processPayments(
			@RequestParam("purchaseIds") String purchaseIds,
			@RequestParam("token") Long token,
			@RequestParam("paymentMethod") String paymentMethod,
			RedirectAttributes redirectAttributes) {
		try {
			List<Long> purchaseIdList = Arrays.stream(purchaseIds.split(","))
					.map(Long::valueOf)
					.collect(Collectors.toList());
		
			boolean allPaymentsSuccessful = true;
	
			for (Long purchaseId : purchaseIdList) {
				boolean paymentSuccess = vintedService.processPayment(purchaseId, paymentMethod, token);
				if (paymentSuccess) {
					try {
						Purchase purchase = vintedService.getPurchaseById(token, purchaseId);
						if (purchase != null) {
							vintedService.deleteItem(token, purchase.itemId());
						}
					} catch (RuntimeException e) {
						redirectAttributes.addFlashAttribute("warningMessage", "Pago realizado, pero no se pudo eliminar un artículo.");
						e.printStackTrace();
					}
				} else {
					allPaymentsSuccessful = false;
				}
			}
	
			if (allPaymentsSuccessful) {
				redirectAttributes.addFlashAttribute("successMessage", "Todos los pagos se procesaron con éxito y los artículos fueron eliminados.");
			} else {
				redirectAttributes.addFlashAttribute("warningMessage", "Algunos pagos no se pudieron procesar.");
			}
	
			return "redirect:/allItems";
		} catch (Exception e) {
			redirectAttributes.addFlashAttribute("errorMessage", "Error al procesar los pagos: " + e.getMessage());
			e.printStackTrace();
			return "redirect:/allItems";
		}
	}

	@DeleteMapping("/deletePurchase/{purchaseId}")
	public String deletePurchase(
		@PathVariable Long purchaseId,
			@RequestParam("token") Long token,
			RedirectAttributes redirectAttributes) {
		try {
			vintedService.deletePurchase(token, purchaseId);
			redirectAttributes.addFlashAttribute("successMessage", "Compra eliminada con éxito.");
		} catch (RuntimeException e) {
			redirectAttributes.addFlashAttribute("errorMessage", "Error al eliminar la compra: " + e.getMessage());
			e.printStackTrace();
		}
		return "redirect:/allItems";
	}
	
	@PostMapping("/shoppingCart/add")
	public String addItemToCart(
			@RequestParam("token") Long token,
			@RequestParam("itemId") Long itemId,
			@RequestParam(value = "redirectUrl", required = false) String redirectUrl,
			RedirectAttributes redirectAttributes,
			Model model) {
		if (redirectUrl == null) {
			redirectUrl = "/";
		}

		if (token == null) {
			return "redirect:" + redirectUrl;
		}
		try {
			vintedService.addItemToCart(token, itemId);
			redirectAttributes.addFlashAttribute("successMessage", "Artículo añadido al carrito.");
		} catch (RuntimeException e) {
			redirectAttributes.addFlashAttribute("errorMessage", "Error al añadir el artículo al carrito.");
			e.printStackTrace();
		}
		return "redirect:" + redirectUrl;
	}

	@PostMapping("/wishlist/add")
	public String addItemToWishlist(
			@RequestParam("token") Long token,
			@RequestParam("itemId") Long itemId,
			@RequestParam(value = "redirectUrl", required = false) String redirectUrl,
			RedirectAttributes redirectAttributes,
			Model model) {
		if (redirectUrl == null) {
			redirectUrl = "/";
		}

		if (token == null) {
			return "redirect:" + redirectUrl;
		}
		try {
			vintedService.addItemToWishlist(token, itemId);
			redirectAttributes.addFlashAttribute("successMessage", "Artículo añadido a la wishlist.");
		} catch (RuntimeException e) {
			redirectAttributes.addFlashAttribute("errorMessage", "Error al añadir el artículo a la wishlist.");
			e.printStackTrace();
		}
		return "redirect:" + redirectUrl;
	}
	

	@PostMapping("/shoppingCart/remove")
	public String removeItemFromCart(
			@RequestParam("token") Long token,
			@RequestParam("itemId") Long itemId,
			@RequestParam(value = "redirectUrl", required = false) String redirectUrl,
			RedirectAttributes redirectAttributes) {
		if (redirectUrl == null) {
			redirectUrl = "/";
		}

		if (token == null) {
			return "redirect:" + redirectUrl;
		}
		try {
			vintedService.removeItemFromCart(token, itemId);
			redirectAttributes.addFlashAttribute("successMessage", "Artículo eliminado del carrito.");
		} catch (RuntimeException e) {
			redirectAttributes.addFlashAttribute("errorMessage", "Error al eliminar el artículo del carrito.");
			e.printStackTrace();
		}

		return "redirect:" + redirectUrl + "?token=" + token;
	}

	
	@PostMapping("/wishlist/remove")
	public String removeItemFromWishlist(
			@RequestParam("token") Long token,
			@RequestParam("itemId") Long itemId,
			@RequestParam(value = "redirectUrl", required = false) String redirectUrl,
			RedirectAttributes redirectAttributes) {
		if (redirectUrl == null) {
			redirectUrl = "/";
		}
		if (token == null) {
			return "redirect:" + redirectUrl;
		}
		try {
			vintedService.removeItemFromWishlist(token, itemId);
			redirectAttributes.addFlashAttribute("successMessage", "Artículo eliminado de la wishlist.");
		} catch (RuntimeException e) {
			redirectAttributes.addFlashAttribute("errorMessage", "Error al eliminar el artículo de la wishlist.");
			e.printStackTrace();
		}

		return "redirect:" + redirectUrl + "?token=" + token;
	}


	
	@PostMapping("/userProfile/{id}/follow")
	public String followUser(
			@PathVariable("id") Long targetUserId,
			@RequestParam("token") Long token,
			@RequestParam(value = "redirectUrl", required = false) String redirectUrl,
			RedirectAttributes redirectAttributes) {
		if (redirectUrl == null) {
			redirectUrl = "/";
		}

		try {
			System.out.println("TOKEN USUARIO: " + token + " ID USUARIO TARGET: " + targetUserId);
			vintedService.followUser(token, targetUserId);
			redirectAttributes.addFlashAttribute("successMessage", "Has comenzado a seguir al usuario.");
		} catch (RuntimeException e) {
			redirectAttributes.addFlashAttribute("errorMessage", "Error al seguir al usuario.");
			e.printStackTrace();
		}
		System.out.println("REDIRECT" + redirectUrl);
		return "redirect:" + redirectUrl;
	}

	@PostMapping("/userProfile/{id}/unfollow")
	public String unfollowUser(
			@PathVariable("id") Long targetUserId,
			@RequestParam("token") Long token,
			@RequestParam(value = "redirectUrl", required = false) String redirectUrl,
			RedirectAttributes redirectAttributes) {
		if (redirectUrl == null) {
			redirectUrl = "/";
		}

		try {
			vintedService.unfollowUser(token, targetUserId);
			redirectAttributes.addFlashAttribute("successMessage", "Has dejado de seguir al usuario.");
		} catch (RuntimeException e) {
			redirectAttributes.addFlashAttribute("errorMessage", "Error al dejar de seguir al usuario.");
			e.printStackTrace();
		}

		return "redirect:" + redirectUrl;
	}

	@GetMapping("/userProfile/{id}/followers")
	public String getFollowers(
			@PathVariable("id") Long userId,
			@RequestParam("token") Long token,
			Model model) {
		try {
			List<User> followers = vintedService.getFollowers(userId);
			model.addAttribute("followers", followers);
			return "userProfile"; 
		} catch (RuntimeException e) {
			model.addAttribute("errorMessage", "Error al cargar los seguidores.");
			e.printStackTrace();
			return "error";
		}
	}

	@GetMapping("/userProfile/{id}/following")
	public String getFollowing(
			@PathVariable("id") Long userId,
			@RequestParam("token") Long token,
			Model model) {
		try {
			List<User> following = vintedService.getFollowing(userId);
			model.addAttribute("following", following);
			return "userProfile"; 
		} catch (RuntimeException e) {
			model.addAttribute("errorMessage", "Error al cargar los usuarios seguidos.");
			e.printStackTrace();
			return "error";
		}
	}

	@GetMapping("/search")
    public String searchItems(
            @RequestParam("search_text") String search,
            @RequestParam("token") Long token,
            Model model,
            RedirectAttributes redirectAttributes) {
        try {
            List<Item> items = vintedService.searchItems(token, search);
            if (items != null && !items.isEmpty()) {
                model.addAttribute("items", items);
                return "search";
            } else {
                redirectAttributes.addFlashAttribute("errorMessage", "No se encontraron artículos.");
                return "redirect:/allItems";
            }
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error al buscar los artículos.");
            e.printStackTrace();
            return "redirect:/allItems";
        }
    }
	@GetMapping("/searchUser")
	public String searchUser(
			@RequestParam("username") String username,
			@RequestParam("token") Long token,
			Model model,
			RedirectAttributes redirectAttributes) {
		try {
			User user = vintedService.getUserByUsername(username, token);
			if (user != null) {
				return loadUserProfile(user.id(), token, model, redirectAttributes);
			} else {
				redirectAttributes.addFlashAttribute("errorMessage", "Usuario no encontrado.");
				return "redirect:/allItems";
			}
		} catch (Exception e) {
			redirectAttributes.addFlashAttribute("errorMessage", "Error al buscar el usuario.");
			e.printStackTrace();
			return "redirect:/allItems";
		}
	}
	
	@GetMapping("/vintedChat")
	public String showVintedChat(
			@RequestParam(value="token", required = false) Long token,
			@RequestParam(value = "redirectUrl", required = false) String redirectUrl,
			Model model) {
		if (token == null) {
			return "redirect:/login";
		}
		if (redirectUrl == null || redirectUrl.isEmpty() || redirectUrl.equals("null")) {
			redirectUrl = "/vintedChat";
			if (token != null) {
				redirectUrl += "?token=" + token;
			}
		}
		model.addAttribute("redirectUrl", redirectUrl);

		Long userId = vintedService.getUserIdFromToken(token);
		model.addAttribute("loggedUserId", userId);

		if (userId == null) {
			return "redirect:/login";
		}
		model.addAttribute("buyerId", userId);

		List<ChatRoomInfo> chatRooms = vintedService.getChatRoomsForUser(userId);
		model.addAttribute("chatRooms", chatRooms);
		for(ChatRoomInfo chatRoom : chatRooms) {
			System.out.println("user id: "+ userId + "seller id" + chatRoom.sellerId() + "seller name: " + chatRoom.sellerName() + "buyer id: " + chatRoom.buyerId() + "buyer name: " + chatRoom.buyerName() + "item id: " + chatRoom.itemId() + "item title: " + chatRoom.itemImage() );
		}
		return "vintedChat";
	}

	@PostMapping("/vintedChat/chatRoom")
	public String createChatRoom(
			@RequestParam("token") Long token,
			@RequestParam("sellerId") long sellerId,
			@RequestParam("itemId") long itemId,
			@RequestParam(value = "redirectUrl", required = false) String redirectUrl,
			Model model) {

		if (token == null) {
			return "redirect:/login";
		}
		long buyerId = vintedService.getUserIdFromToken(token);

		vintedService.createChatRoom(buyerId, sellerId, itemId);

		if (redirectUrl == null || redirectUrl.isBlank() || redirectUrl.equals("null")) {
			redirectUrl = "/vintedChat?token=" + token;
		}

		return "redirect:" + redirectUrl;
	}
	

	@PostMapping("/rateUser")
	public String rateUser(
			@RequestParam("ratedUserId") Long ratedUserId,
			@RequestParam("ratingUserId") Long ratingUserId,
			@RequestParam("score") int score,
			@RequestParam(value = "comment", required = false) String comment,
			@RequestParam("token") Long token,
			RedirectAttributes redirectAttributes,
			Model model) {
		try {
			Rating rating = new Rating(0L, ratedUserId, ratingUserId, score, comment);
			String response = vintedService.addRating(rating, token);
			redirectAttributes.addFlashAttribute("successMessage", response);
		} catch (Exception e) {
			redirectAttributes.addFlashAttribute("errorMessage", "Error al enviar la valoración.");
			e.printStackTrace();
		}
		return loadUserProfile(ratedUserId, token, model, redirectAttributes);
	}

	@GetMapping("/user/{userId}/ratings")
	public String getUserRatings(
			@PathVariable long userId,
			Model model,
			RedirectAttributes redirectAttributes) {
		try {
			List<Rating> ratings = vintedService.getRatingsForUser(userId);
			model.addAttribute("ratings", ratings);
			return "userProfile";
		} catch (Exception e) {
			redirectAttributes.addFlashAttribute("errorMessage", "Error al obtener las valoraciones.");
			e.printStackTrace();
			return "redirect:/userProfile/" + userId;
		}
	}

	private String loadUserProfile(Long userId, Long token, Model model, RedirectAttributes redirectAttributes) {
		try {
			User user = vintedService.getUser(userId, token);
			model.addAttribute("user", user);
	
			List<Item> items = vintedService.getUserItems(userId);
			model.addAttribute("items", items);
	
			List<Rating> ratings = vintedService.getRatingsForUser(userId);
			model.addAttribute("ratings", ratings);
	
			boolean isMyProfile = userId.equals(this.userId);
			model.addAttribute("isMyProfile", isMyProfile);
			
			List<User> followers = vintedService.getFollowers(userId);
			List<User> following = vintedService.getFollowing(userId);
			
			model.addAttribute("isFollowing", vintedService.getFollowing(this.userId).contains(user));
			model.addAttribute("followers", followers);
			model.addAttribute("following", following);
			model.addAttribute("followersCount", followers.size());
			model.addAttribute("followingCount", following.size());
	
			return "userProfile";
		} catch (Exception e) {
			redirectAttributes.addFlashAttribute("errorMessage", "Error al cargar el perfil del usuario.");
			e.printStackTrace();
			return "redirect:/userProfile";
		}
	}

	@GetMapping("/uploadAd")
	public String showUploadAd(
			@RequestParam(value = "token", required = false) Long token,
			@RequestParam(value = "redirectUrl", required = false) String redirectUrl,
			Model model) {
		if (redirectUrl == null) {
			redirectUrl = "/";
		}
		if (token == null) {
			return "redirect:/login";
		}
		model.addAttribute("redirectUrl", redirectUrl);
		model.addAttribute("token", token);
		return "uploadAd";
	}

	@PostMapping("/uploadAd")
	public String uploadAd(
			@RequestParam("token") Long token,
			@RequestParam("title") String title,
			@RequestParam("description") String description,
			@RequestParam(value = "adImage") MultipartFile adImage,
			@RequestParam(value = "redirectUrl", required = false) String redirectUrl,
			RedirectAttributes redirectAttributes,
			Model model) {

		if (redirectUrl == null) {
			redirectUrl = "/";
		}

		try {
			if (token != null) {
				redirectUrl += "?token=" + token;
			}
			model.addAttribute("redirectUrl", redirectUrl);

			
			long adId = vintedService.uploadAdData(token, title, description);

			if (adImage != null && !adImage.isEmpty()) {
				vintedService.uploadAdImage(adId, adImage);
			}

			redirectAttributes.addFlashAttribute("successMessage", "Anuncio creado correctamente.");
			return "uploadAd";
		} catch (RuntimeException e) {
			redirectAttributes.addFlashAttribute("errorMessage", "Error al crear el anuncio.");
			return "redirect:" + redirectUrl;
		}
	}

}

