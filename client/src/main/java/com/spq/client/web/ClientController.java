package com.spq.client.web;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

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
	
	@GetMapping("/logout")
	public String logout(
			@RequestParam(value="token") Long token,
			Model model) {
		try {
			this.token = null;
			this.userId = null;
			vintedService.logout(token);
		} catch (RuntimeException e) {
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
			@RequestParam(value="token") Long token,
			@RequestParam(value = "redirectUrl", required = false) String redirectUrl,
			Model model) {
		if (redirectUrl == null) {
			redirectUrl = "/";
		}
		model.addAttribute("user", vintedService.getUser(id, token));
		model.addAttribute("redirectUrl", redirectUrl);

		boolean isMyProfile = (id.equals(userId));
    	model.addAttribute("isMyProfile", isMyProfile);

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
}
