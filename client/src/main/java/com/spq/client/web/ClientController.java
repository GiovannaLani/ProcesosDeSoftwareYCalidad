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

import com.spq.client.data.User;

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
	
	@GetMapping("/logout")
	public String logout(
			@RequestParam(value="token") Long token,
			Model model) {
		try {
			this.token = null;
			vintedService.logout(token);
		} catch (RuntimeException e) {
			System.err.println("Ha oucurrido un error: " + e.getMessage());
            e.printStackTrace();
        }
		
		return "redirect:/";
	}
}
