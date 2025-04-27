package com.spq.vinted.integration;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandlers;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.boot.test.web.client.TestRestTemplate;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.spq.vinted.dto.UserDTO;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class IntegrationTest {
    @Autowired
    private TestRestTemplate restTemplate;

    private HttpClient client;
    private ObjectMapper objectMapper;

    private long token;
    private long userId;
    private long itemId;

    @BeforeEach
    public void setup() {
        client = HttpClient.newHttpClient();
        objectMapper = new ObjectMapper();
    }

    @Test
    public void testUserFlow_Signup_Login_GetProfile_Logout() {
        try {
            // 1. Crear usuario
            ObjectNode signupData = objectMapper.createObjectNode();
            String uniqueEmail = "integrationtest" + System.currentTimeMillis() + "@example.com";
            signupData.put("email", uniqueEmail);
            signupData.put("password", "testpassword6");
            String uniqueName = "integrationtest" + System.currentTimeMillis() + "Name";
            signupData.put("username", uniqueName);
            signupData.put("name", "Test6");
            signupData.put("surname", "User6");
            ResponseEntity<Void> signupResponse = restTemplate.postForEntity("/users/signup", signupData, Void.class);
            assertEquals(HttpStatus.CREATED, signupResponse.getStatusCode(), "Error al crear usuario");

            // 2. Login
            ObjectNode loginData = objectMapper.createObjectNode();
            loginData.put("email", uniqueEmail);
            loginData.put("password", "testpassword6");
            ResponseEntity<Long> loginResponse = restTemplate.postForEntity("/users/login", loginData, Long.class);
            assertEquals(HttpStatus.OK, loginResponse.getStatusCode(), "Error en login");

            if (loginResponse.getBody() != null) {
                token = loginResponse.getBody();
            } else {
                fail("Login response body is null");
            }
            System.out.println("Token recibido: " + token);

           // 3. Obtener el ID del usuario usando el token
           ResponseEntity<Long> getUserIdResponse = restTemplate.getForEntity("/users/userId?token="+token, Long.class);
            assertEquals(HttpStatus.OK, getUserIdResponse.getStatusCode(), "Error al obtener userId");

            if (getUserIdResponse.getBody() != null) {
                userId = getUserIdResponse.getBody();
            } else {
                fail("getUserIdResponse body is null");
            }
            System.out.println("User ID recibido: " + userId);

            // 4. Obtener perfil del usuario
            ResponseEntity<UserDTO> getProfileResponse = restTemplate.getForEntity("/users/profile/"+userId+"?token="+token, UserDTO.class);
            assertEquals(HttpStatus.OK, getProfileResponse.getStatusCode(), "Error al obtener perfil del usuario");

            // 5. Logout
            ResponseEntity<Void> logoutResponse = restTemplate.postForEntity("/users/logout?token="+token, null,Void.class);
            assertEquals(HttpStatus.NO_CONTENT, logoutResponse.getStatusCode(), "Error al hacer logout");

        } catch (Exception ex) {
            ex.printStackTrace();
            fail("Test falló por excepción: " + ex.getMessage());
        }
    }
/* 
    @Test
    public void testLogin_UploadItem_ViewItems_Logout() {
        try {
            // 1. Crear usuario
            ObjectNode signupData = objectMapper.createObjectNode();
            signupData.put("email", "itemintegration8@example.com");
            signupData.put("password", "testpassword8");
            signupData.put("username", "itemintegrationuser8");
            signupData.put("name", "Item8");
            signupData.put("surname", "Tester8");

            HttpRequest signupRequest = HttpRequest.newBuilder()
                    .uri(URI.create("http://localhost:8080/users/signup"))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(objectMapper.writeValueAsString(signupData)))
                    .build();

            HttpResponse<String> signupResponse = client.send(signupRequest, HttpResponse.BodyHandlers.ofString());
            assertEquals(201, signupResponse.statusCode(), "Error al crear usuario");

            // 2. Login
            ObjectNode loginData = objectMapper.createObjectNode();
            loginData.put("email", "itemintegration8@example.com");
            loginData.put("password", "testpassword8");

            HttpRequest loginRequest = HttpRequest.newBuilder()
                    .uri(URI.create("http://localhost:8080/users/login"))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(objectMapper.writeValueAsString(loginData)))
                    .build();

            HttpResponse<String> loginResponse = client.send(loginRequest, HttpResponse.BodyHandlers.ofString());
            assertEquals(200, loginResponse.statusCode(), "Error en login");

            token = Long.parseLong(loginResponse.body());
            System.out.println("Token recibido: " + token);

            /* 3. Subir un ítem
            ObjectNode itemData = objectMapper.createObjectNode();
            itemData.put("title", "Integration Test Item");
            itemData.put("description", "Item created during integration test");
            itemData.put("price", 15.99);
            itemData.put("size", "M");
            itemData.put("clothesType", "TSHIRT");
            itemData.put("category", "BOY");
            itemData.put("type", "clothes");

            HttpRequest uploadItemRequest = HttpRequest.newBuilder()
                    .uri(URI.create("http://localhost:8080/items/itemData?token=" + token))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(objectMapper.writeValueAsString(itemData)))
                    .build();

            HttpResponse<String> uploadItemResponse = client.send(uploadItemRequest, HttpResponse.BodyHandlers.ofString());
            assertEquals(200, uploadItemResponse.statusCode(), "Error al subir ítem");

            itemId = Long.parseLong(uploadItemResponse.body());
            System.out.println("Item creado con ID: " + itemId);

            // 4. Ver ítems
            HttpRequest getItemsRequest = HttpRequest.newBuilder()
                    .uri(URI.create("http://localhost:8080/items/items?token=" + token))
                    .GET()
                    .build();

            HttpResponse<String> getItemsResponse = client.send(getItemsRequest, HttpResponse.BodyHandlers.ofString());
            assertEquals(200, getItemsResponse.statusCode(), "Error al obtener items");
            System.out.println("Items actuales: " + getItemsResponse.body());

            // 5. Logout
            HttpRequest logoutRequest = HttpRequest.newBuilder()
                    .uri(URI.create("http://localhost:8080/users/logout?token=" + token))
                    .POST(HttpRequest.BodyPublishers.noBody())
                    .build();

            HttpResponse<String> logoutResponse = client.send(logoutRequest, HttpResponse.BodyHandlers.ofString());
            assertEquals(204, logoutResponse.statusCode(), "Error al hacer logout");

            System.out.println("Logout exitoso");

        } catch (Exception ex) {
            ex.printStackTrace();
            fail("Test falló por excepción: " + ex.getMessage());
        }
    }
    */
}
