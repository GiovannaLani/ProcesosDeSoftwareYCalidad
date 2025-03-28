package com.spq.client.web;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import com.spq.client.data.EditUser;
import com.spq.client.data.Login;
import com.spq.client.data.MultipartInputStreamFileResource;
import com.spq.client.data.Signup;
import com.spq.client.data.User;

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
}
