package com.spq.vinted.service;

import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.spq.vinted.model.User;
import com.spq.vinted.repository.UserRepository;

@Service
public class AuthService {
    private Map<Long, User> activeUsers;
	private UserRepository userRepository;
	
	public AuthService(UserRepository userRepository) {
		activeUsers = new HashMap<>();
		this.userRepository = userRepository;
	}
	
	public void createUser(String email, String password) {
        System.out.println("Creating user with email: " + email);
		User newUser = new User(email, password);
        System.out.println("User created: " + newUser);
		userRepository.findByEmail(email).ifPresent(u -> {throw new RuntimeException("User already exists");});
        System.out.println("User does not exist, saving user");
		userRepository.save(newUser);
	}
	
	public long logIn(String email, String password) {
		
		User user = userRepository.findByEmail(email).orElseThrow(() -> new RuntimeException("User not found"));		
		long token = System.currentTimeMillis();
		activeUsers.put(token, user);
		return token;
	}
	
	public void LogOut(long token) {
		User loggedOutUser = activeUsers.remove(token);
		if (loggedOutUser == null) {
			throw new RuntimeException("Token not found in active users list");
		}
	}
	
	public User getUser(long token) {
		User user = activeUsers.get(token);
		return user;
	}
}
