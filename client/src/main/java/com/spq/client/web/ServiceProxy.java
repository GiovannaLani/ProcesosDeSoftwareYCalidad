package com.spq.client.web;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;

import com.spq.client.data.Login;
import com.spq.client.data.User;
import com.spq.client.data.Purchase;

@Service
public class ServiceProxy implements IVintedServiceProxy {
    RestTemplate restTemplate;

	@Value("${api.base.url}")
	private String apiBaseUrl;

	public ServiceProxy(RestTemplate restTemplate) {
		this.restTemplate = restTemplate;
	}

    @Override
	public void createUser(User user) {
		try {
			restTemplate.postForObject(apiBaseUrl + "/users", user, Void.class);
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
    public void createPurchase(Purchase purchase) {
        try {
            restTemplate.postForObject(apiBaseUrl + "/purchases/create", purchase, Void.class);
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
    public boolean processPayment(long purchaseId, String paymentMethod) {
        String url = apiBaseUrl + "/purchases/pay?purchaseId=" + purchaseId + "&paymentMethod=" + paymentMethod;
        try {
            restTemplate.postForObject(url, null, Void.class);
            return true;
        } catch (HttpStatusCodeException e) {
            switch (e.getStatusCode().value()) {
                case 400 -> throw new RuntimeException("Invalid payment request");
                case 404 -> throw new RuntimeException("Purchase not found");
                case 409 -> throw new RuntimeException("Payment conflict, already paid");
                default -> throw new RuntimeException("Failed to process payment: " + e.getStatusText());
            }
        }
    }

}
