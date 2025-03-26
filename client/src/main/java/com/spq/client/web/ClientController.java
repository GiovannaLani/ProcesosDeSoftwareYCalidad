package com.spq.client.web;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import org.springframework.web.bind.annotation.PathVariable;

import com.spq.client.data.User;
import com.spq.client.data.Category;
import com.spq.client.data.Item;
import com.spq.client.data.Pet;
import com.spq.client.data.Clothes;
import com.spq.client.data.Electronics;
import com.spq.client.data.Entertainment;
import com.spq.client.data.Home;

import java.util.List;

import jakarta.servlet.http.HttpServletRequest;

@Controller
public class ClientController {
@Autowired
	IVintedServiceProxy vintedService;

	private Long token = null; 

    @ModelAttribute
	public void addAttributes(Model model, HttpServletRequest request) {

		String currentUrl = ServletUriComponentsBuilder.fromRequestUri(request).toUriString();
		model.addAttribute("currentUrl", currentUrl);
		if (token != null) {
			model.addAttribute("token", token);
		}
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
			@RequestParam(value = "redirectUrl") String redirectUrl,
			Model model,
			RedirectAttributes redirectAttributes) {
		try {
			model.addAttribute("redirectUrl", redirectUrl);
			vintedService.createUser(new User(email, password));
			return "redirect:/login?redirectUrl=" + redirectUrl;
		} catch (RuntimeException e) {
			if (e.getMessage().equals("User already exists")) {
				redirectAttributes.addFlashAttribute("errorMessage", "The user already exists");
			} else if (e.getMessage().equals("Invalid credentials")) {
                redirectAttributes.addFlashAttribute("errorMessage", "The password is incorrect");
			} else {
				redirectAttributes.addFlashAttribute("errorMessage", "Unexpected error");
				e.printStackTrace();
			}
		}
		return "redirect:/register?redirectUrl=" + redirectUrl;
	}
	
    @GetMapping("/")
	public String showLoginPage(
			@RequestParam(value = "redirectUrl", required = false) String redirectUrl,
			Model model) {
		if (redirectUrl == null) {
			redirectUrl = "/";
		}
		model.addAttribute("redirectUrl", redirectUrl);
		return "index";
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
			return "redirect:"+ redirectUrl;
		} catch (RuntimeException e) {
			if (e.getMessage().equals("Invalid credentials")) {
				redirectAttributes.addFlashAttribute("errorMessage", "The password is incorrect");
			} else if (e.getMessage().equals("User not found")) {
				redirectAttributes.addFlashAttribute("errorMessage", "The user does not exist");
			} else {
				redirectAttributes.addFlashAttribute("errorMessage", "Unexpected error");
			}
		}
		return "redirect:/login?redirectUrl=" + redirectUrl;
	}

	@GetMapping("/items")
	public List<Item> getItems() {
		try {
			return vintedService.getItems();
		} catch (RuntimeException e) {
			System.err.println("Ha ocurrido un error: " + e.getMessage());
			e.printStackTrace();
		}
		return null;
	}

	@GetMapping("/clothes")
	public List<Clothes> getClothes() {
		try {
			return vintedService.getClothes();
		} catch (RuntimeException e) {
			System.err.println("Ha ocurrido un error: " + e.getMessage());
			e.printStackTrace();
		}
		return null;
	}

	@GetMapping("/clothes/{category}")
	public List<Clothes> getClothesByCategory(@PathVariable Category category) {
    try {
        return vintedService.getClothesByCategory(category);
    } catch (RuntimeException e) {
        System.err.println("Ha ocurrido un error: " + e.getMessage());
        e.printStackTrace();
    }
    return null;
}

	@GetMapping("/electronics")
	public List<Electronics> getElectronics() {
		try {
			return vintedService.getElectronics();
		} catch (RuntimeException e) {
			System.err.println("Ha ocurrido un error: " + e.getMessage());
			e.printStackTrace();
		}
		return null;
	}

	@GetMapping("/pet")
	public List<Pet> getItemsForPet() {
		try {
			return vintedService.getItemsForPet();
		} catch (RuntimeException e) {
			System.err.println("Ha ocurrido un error: " + e.getMessage());
			e.printStackTrace();
		}
		return null;
	}

	@GetMapping("/entertainment")
	public List<Entertainment> getItemsForEntertainment() {
		try {
			return vintedService.getItemsForEntertainment();
		} catch (RuntimeException e) {
			System.err.println("Ha ocurrido un error: " + e.getMessage());
			e.printStackTrace();
		}
		return null;
	}

	@GetMapping("/home")
	public List<Home> getHomeItems() {
		try {
			return vintedService.getHomeItems();
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
			vintedService.logout(token);
		} catch (RuntimeException e) {
			System.err.println("Ha ocurrido un error: " + e.getMessage());
            e.printStackTrace();
        }
		
		return "redirect:/";
	}
}
