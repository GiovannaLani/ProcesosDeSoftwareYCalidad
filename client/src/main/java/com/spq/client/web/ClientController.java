package com.spq.client.web;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import com.spq.client.data.Category;
import com.spq.client.data.Item;
import com.spq.client.data.Pet;
import com.spq.client.data.Purchase;
import com.spq.client.data.Clothes;
import com.spq.client.data.Electronics;
import com.spq.client.data.Entertainment;
import com.spq.client.data.Home;
import com.spq.client.data.User;

import java.util.List;
import com.spq.client.data.Signup;

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

	}
	
	@GetMapping("/register")
	public String showRegisterPage(
			@RequestParam(value = "redirectUrl", required = false) String redirectUrl,
			Model model) {
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
			model.addAttribute("redirectUrl", redirectUrl);
			vintedService.createUser(new Signup(email, password, username, name, surname));
			token = vintedService.login(email, password);
			userId = vintedService.getUserIdFromToken(token);
			return "redirect:/login?redirectUrl=" + redirectUrl;
		} catch (RuntimeException e) {
			if (e.getMessage().equals("User already exists")) {
				redirectAttributes.addFlashAttribute("errorMessage", "El usuario ya existe");
			} else if (e.getMessage().equals("Invalid credentials")) {
                redirectAttributes.addFlashAttribute("errorMessage", "La contraseña es incorrecta");
			} else {
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
		if (redirectUrl == null) {
			redirectUrl = "/";
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
			model.addAttribute("redirectUrl", redirectUrl);
			token = vintedService.login(email, password);
			userId = vintedService.getUserIdFromToken(token);
			return "redirect:"+ redirectUrl;
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
			model.addAttribute("items", items);
	
			//if (token != null) {
			//	List<Item> cartItems = vintedService.getCartItems(token);
			//	model.addAttribute("cartItems", cartItems);
			//}
	
			return "product";
		} catch (RuntimeException e) {
			System.err.println("Ha ocurrido un error: " + e.getMessage());
			e.printStackTrace();
			return "redirect:/";
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

			model.addAttribute("item", item);
			model.addAttribute("sellerId", sellerId);
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
		return "redirect:/";
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
			Model model) {
		try {
			this.token = null;
			this.userId = null;
			vintedService.logout(token);
		} catch (RuntimeException e) {
			System.err.println("Ha ocurrido un error: " + e.getMessage());
            e.printStackTrace();
        }
		
		return "redirect:/";
	}

	@GetMapping("/deleteUser")
	public String deleteUser(
			@RequestParam(value="token") Long token,
			Model model) {
		try {
			this.token = null;
			vintedService.deleteUser(token);
		} catch (RuntimeException e) {
			e.printStackTrace();
		}
		return "redirect:/";
	}

	@GetMapping("/userProfile/{id}")
	public String showUserProfile(
			@PathVariable("id") Long id,
			@RequestParam(value = "token") Long token,
			@RequestParam(value = "redirectUrl", required = false) String redirectUrl,
			Model model) {
		if (redirectUrl == null) {
			redirectUrl = "/";
		}
	
		model.addAttribute("user", vintedService.getUser(id, token));
		model.addAttribute("redirectUrl", redirectUrl);
		List<Item> items = vintedService.getUserItems(id);
    	model.addAttribute("items", items);
		boolean isMyProfile = (id.equals(userId));
		model.addAttribute("isMyProfile", isMyProfile);
	
		//try {
		//	List<Item> cartItems = vintedService.getCartItems(token); 
		//	model.addAttribute("cartItems", cartItems);
		//} catch (RuntimeException e) {
		//	System.err.println("Error al obtener los artículos del carrito: " + e.getMessage());
		//	e.printStackTrace();
		//}
	
		return "userProfile";
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
			return "redirect:" + redirectUrl;
		} catch (RuntimeException e) {
			if ("User not found".equals(e.getMessage())) {
				redirectAttributes.addFlashAttribute("errorMessage", "Usuario no encontrado.");
			} else {
				redirectAttributes.addFlashAttribute("errorMessage", "Error inesperado.");
			}
			return "redirect:/editUser?redirectUrl=" + redirectUrl;
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
	
		//try {
		//	List<Item> cartItems = vintedService.getCartItems(token);
		//	model.addAttribute("cartItems", cartItems);
		//} catch (RuntimeException e) {
		//	System.err.println("Error al obtener los artículos del carrito: " + e.getMessage());
		//	e.printStackTrace();
		//}
	
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
			RedirectAttributes redirectAttributes) {

		if (redirectUrl == null) {
			redirectUrl = "/";
		}

		try {
			vintedService.uploadItem(token, title, description, category, price, brand, size, clothCategory, clothingType, species, homeType, electronicsType, entertainmentType, itemImages);
			return "redirect:" + redirectUrl;
		} catch (RuntimeException e) {
			if ("User not found".equals(e.getMessage())) {
				redirectAttributes.addFlashAttribute("errorMessage", "Usuario no encontrado.");
			} else {
				redirectAttributes.addFlashAttribute("errorMessage", "Error inesperado.");
			}
			return "redirect:/editUser?redirectUrl=" + redirectUrl;
		}
	} 

	@GetMapping("/shoppingCart")
	public String showCart(
			@RequestParam("token") Long token,
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
			model.addAttribute("cartSize", cartItems.size());
			model.addAttribute("totalPrice",String.format("%.2f", cartItems.stream().mapToDouble(Item::getPrice).sum()));
			return "shoppingCart"; 
		} catch (RuntimeException e) {
			model.addAttribute("errorMessage", "Error al cargar el carrito.");
			e.printStackTrace();
			return "error"; 
		}
	}

	@GetMapping("/createPurchase/{itemId}")
	public String showPurchasePage(
			@PathVariable Long itemId,
			@RequestParam("token") Long token,
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
	
			model.addAttribute("item", item);
			model.addAttribute("buyerId", buyerId);
			model.addAttribute("token", token);
	
			return "purchase";  
		} catch (Exception e) {
			redirectAttributes.addFlashAttribute("errorMessage", "Ocurrió un error al mostrar la compra.");
			return "redirect:/login";
		}
	}
	

	@PostMapping("/createPurchase/{itemId}")
	public String createPurchase(
			@PathVariable Long itemId,
			@RequestParam("token") Long token,
			@RequestParam("paymentMethod") String paymentMethod,
			RedirectAttributes redirectAttributes) {
		try {
			Long buyerId = vintedService.getUserIdFromToken(token);
			if (buyerId == null) {
				redirectAttributes.addFlashAttribute("errorMessage", "Usuario no autenticado.");
				return "redirect:/login";
			}
	
			Item item = vintedService.getItemById(itemId);
			if (item == null) {
				redirectAttributes.addFlashAttribute("errorMessage", "El artículo no existe.");
				return "redirect:/login";
			}
	
			User seller = vintedService.getSeller(item);
			if (seller == null) {
				redirectAttributes.addFlashAttribute("errorMessage", "No se encontró el vendedor.");
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
	
			Purchase createdPurchase = vintedService.createPurchase(token, purchase);
	
			redirectAttributes.addFlashAttribute("successMessage", "Compra iniciada. Procede con el pago.");
			return "redirect:/processPayment/" + createdPurchase.id() + "?token=" + token;
	
		} catch (Exception e) {
			redirectAttributes.addFlashAttribute("errorMessage", "Error al crear la compra.");
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

	@PostMapping("/processPayment/{purchaseId}")
	public String processPayment(
			@PathVariable Long purchaseId,
			@RequestParam("token") Long token,
			@RequestParam("paymentMethod") String paymentMethod,
			RedirectAttributes redirectAttributes) {
		try {
			System.out.println(token);
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
			RedirectAttributes redirectAttributes) {
		if (redirectUrl == null) {
			redirectUrl = "/";
		}

		if (token == null) {
			return "redirect:/login";
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
			return "redirect:/login";
		}
		try {
			vintedService.removeItemFromCart(token, itemId);
			redirectAttributes.addFlashAttribute("successMessage", "Artículo eliminado del carrito.");
		} catch (RuntimeException e) {
			redirectAttributes.addFlashAttribute("errorMessage", "Error al eliminar el artículo del carrito.");
			e.printStackTrace();
		}

		return "redirect:" + redirectUrl;
	}

}


