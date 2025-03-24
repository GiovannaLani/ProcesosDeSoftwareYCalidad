package com.spq.vinted.controller;

import com.spq.vinted.dto.LoginDTO;
import com.spq.vinted.dto.UserDTO;
import com.spq.vinted.service.AuthService;

import io.swagger.v3.oas.annotations.tags.Tag;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/auth")
@Tag(name = "Authorization Controller", description = "Login and logout operations")
public class AuthController {
    @Autowired
    private AuthService authService;
	
	@PostMapping("")
    public ResponseEntity<Void> createUser(@RequestBody UserDTO user) {
        try {
            authService.createUser(user.getEmail(),  user.getPassword());
            return new ResponseEntity<>(HttpStatus.CREATED);
        } catch (RuntimeException e) {
            if (e.getMessage().equals("User already exists")) {
                return new ResponseEntity<>(HttpStatus.CONFLICT);
            } else if (e.getMessage().equals("Invalid credentials")) {
                return new ResponseEntity<>(HttpStatus.FORBIDDEN);
            } else {
                return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
            }
        }
    }
    
	
	@PostMapping("/login")

	public ResponseEntity<Long> logIn(@RequestBody LoginDTO loginData) {
		try {
			long token = authService.logIn(loginData.getEmail(), loginData.getPassword());
			return new ResponseEntity<>(token, HttpStatus.OK);
		} catch (RuntimeException e) {
			if (e.getMessage().equals("Invalid credentials")) {
				return new ResponseEntity<>(HttpStatus.FORBIDDEN);
			}
			if (e.getMessage().equals("User not found")) {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
			return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
	@PostMapping("/logout")
	public ResponseEntity<Void> deleteUser(@RequestParam("token") long token) {
		try {
			authService.LogOut(token);
			return new ResponseEntity<>(HttpStatus.NO_CONTENT);
		} catch (RuntimeException e) {
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		}
	}
}
