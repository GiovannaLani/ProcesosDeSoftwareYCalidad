package com.spq.vinted.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.spq.vinted.dto.*;
import com.spq.vinted.model.Item;
import com.spq.vinted.model.Rating;
import com.spq.vinted.model.User;
import com.spq.vinted.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UserController.class)
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    @InjectMocks
    private UserController userController;

    @Autowired
    private ObjectMapper objectMapper;

    private UserDTO userDTO;
    private RatingDTO ratingDTO;

    @BeforeEach
    void setUp() {
        userDTO = new UserDTO(1L, "testuser", "Test", "User", "Description", "profile.jpg");
        ratingDTO = new RatingDTO(1L, 2L, 5, "Great user!");
    }

    @Test
    void testCreateUser() throws Exception {
        SignupDTO signupDTO = new SignupDTO("test@example.com", "password123", "testuser", "Test", "User");

        mockMvc.perform(post("/users/signup")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(signupDTO)))
                .andExpect(status().isCreated());
    }

    @Test
    void testCreateUser_UserAlreadyExists() throws Exception {
        SignupDTO signupDTO = new SignupDTO("test@example.com", "password123", "testuser", "Test", "User");
    
        doThrow(new RuntimeException("User already exists")).when(userService)
                .createUser(signupDTO.email(), signupDTO.password(), signupDTO.username(), signupDTO.name(), signupDTO.surname());
    
        mockMvc.perform(post("/users/signup")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(signupDTO)))
                .andExpect(status().isConflict());
    }

    @Test
    void testLogIn() throws Exception {
        Mockito.when(userService.logIn("test@example.com", "password123")).thenReturn(123L);

        LoginDTO loginDTO = new LoginDTO("test@example.com", "password123");

        mockMvc.perform(post("/users/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginDTO)))
                .andExpect(status().isOk())
                .andExpect(content().string("123"));
    }

    @Test
    void testLogIn_InvalidCredentials() throws Exception {
        Mockito.when(userService.logIn("test@example.com", "wrongpassword"))
                .thenThrow(new RuntimeException("Invalid credentials"));
    
        LoginDTO loginDTO = new LoginDTO("test@example.com", "wrongpassword");
    
        mockMvc.perform(post("/users/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginDTO)))
                .andExpect(status().isForbidden());
    }

    @Test
    void testLogIn_UserNotFound() throws Exception {
        Mockito.when(userService.logIn("nonexistent@example.com", "password123"))
                .thenThrow(new RuntimeException("User not found"));
    
        LoginDTO loginDTO = new LoginDTO("nonexistent@example.com", "password123");
    
        mockMvc.perform(post("/users/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginDTO)))
                .andExpect(status().isNotFound());
    }

    @Test
    void testLogIn_InternalServerError() throws Exception {
        LoginDTO loginDTO = new LoginDTO("test@example.com", "password123");
    
        Mockito.when(userService.logIn(any(), any()))
                .thenThrow(new RuntimeException("Unexpected error"));
    
        mockMvc.perform(post("/users/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginDTO)))
                .andExpect(status().isInternalServerError());
    }
    

    @Test
    void testLogOut() throws Exception {
        mockMvc.perform(post("/users/logout")
                .param("token", "123"))
                .andExpect(status().isNoContent());
    }

    @Test
    void testLogOut_InvalidToken() throws Exception {
        doThrow(new RuntimeException("Token not found")).when(userService).LogOut(999L);
    
        mockMvc.perform(post("/users/logout")
                .param("token", "999"))
                .andExpect(status().isNotFound());
    }

    @Test
    void testDeleteUser() throws Exception {
        mockMvc.perform(delete("/users/delete")
                .param("token", "123"))
                .andExpect(status().isNoContent());
    }

    @Test
    void testEditUser() throws Exception {
        mockMvc.perform(put("/users/edit")
                .param("token", "123")
                .param("name", "NewName")
                .param("surname", "NewSurname")
                .param("description", "NewDescription")
                .contentType(MediaType.MULTIPART_FORM_DATA))
                .andExpect(status().isNoContent());
    }

    @Test
    void testGetUserProfile() throws Exception {
        Mockito.when(userService.getUserById(1L)).thenReturn(new User("test@example.com", "password123", "testuser", "Test", "User"));

        mockMvc.perform(get("/users/profile/1")
                .param("token", "123"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("testuser"))
                .andExpect(jsonPath("$.name").value("Test"))
                .andExpect(jsonPath("$.surname").value("User"));
    }

    @Test
    void testGetUserProfile_UserNotFound() throws Exception {
        Mockito.when(userService.getUserById(1L)).thenReturn(null);
    
        mockMvc.perform(get("/users/profile/1")
                .param("token", "123"))
                .andExpect(status().isNotFound());
    }

    @Test
    void testRateUser() throws Exception {
        User ratedUser = new User("rated@example.com", "password123", "ratedUser", "Rated", "User");
        ratedUser.setId(1L);
    
        User ratingUser = new User("rating@example.com", "password456", "ratingUser", "Rating", "User");
        ratingUser.setId(2L);
    
        Mockito.when(userService.getUserById(1L)).thenReturn(ratedUser);
        Mockito.when(userService.getUserById(2L)).thenReturn(ratingUser);
    
        Rating rating = new Rating();
        rating.setRatedUserId(1L);
        rating.setRatingUserId(2L);
        rating.setScore(5);
        rating.setComment("Great user!");
    
        mockMvc.perform(post("/users/rate")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(rating)))
                .andExpect(status().isOk());
    }

    @Test
    void testRateUser_Exception() throws Exception {
        Mockito.when(userService.getUserById(1L)).thenThrow(new RuntimeException("Database error"));
    
        Rating rating = new Rating();
        rating.setRatedUserId(1L);
        rating.setRatingUserId(2L);
        rating.setScore(5);
        rating.setComment("Great user!");
    
        mockMvc.perform(post("/users/rate")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(rating)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testRateUser_InvalidData() throws Exception {
        Rating rating = new Rating();
        rating.setRatedUserId(null);
        rating.setRatingUserId(2L);
        rating.setScore(5);
        rating.setComment("Great user!");
    
        mockMvc.perform(post("/users/rate")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(rating)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testGetUserRatings() throws Exception {
        RatingInfoDTO ratingInfoDTO = new RatingInfoDTO();
        ratingInfoDTO.setScore(5);
        ratingInfoDTO.setComment("Great user!");
        
        Mockito.when(userService.getRatingsForUser(1L)).thenReturn(Arrays.asList(ratingInfoDTO));

        mockMvc.perform(get("/users/1/ratings"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].score").value(5))
                .andExpect(jsonPath("$[0].comment").value("Great user!"));
    }

    @Test
    void testGetUserRatings_Exception() throws Exception {
        Mockito.when(userService.getRatingsForUser(1L)).thenThrow(new RuntimeException("Database error"));
    
        mockMvc.perform(get("/users/1/ratings"))
                .andExpect(status().isInternalServerError());
    }

    @Test
    void testGetUserRatings_EmptyList() throws Exception {
        Mockito.when(userService.getRatingsForUser(1L)).thenReturn(Arrays.asList());
    
        mockMvc.perform(get("/users/1/ratings"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0));
    }

    @Test
    void testGetUserRatings_InternalServerError() throws Exception {
        Mockito.when(userService.getRatingsForUser(anyLong()))
                .thenThrow(new RuntimeException("Unexpected error"));
    
        mockMvc.perform(get("/users/1/ratings"))
                .andExpect(status().isInternalServerError());
    }    

    @Test
    void testGetUserItems() throws Exception {
        Item item = new Item() {
            @Override
            public ItemDTO toDTO() {
                return null;
            }
        };
        item.setTitle("Test Item");

        Mockito.when(userService.getUserItems(1L, 123L)).thenReturn(Arrays.asList(item));

        mockMvc.perform(get("/users/1/items")
                .param("token", "123"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].title").value("Test Item"));
    }

    @Test
    void testGetUserItems_EmptyList() throws Exception {
        Mockito.when(userService.getUserItems(1L, 123L)).thenReturn(Arrays.asList());
    
        mockMvc.perform(get("/users/1/items")
                .param("token", "123"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0));
    }

    @Test
    void testGetUserItems_Exception() throws Exception {
        Mockito.when(userService.getUserItems(1L, 123L)).thenThrow(new RuntimeException("Database error"));
    
        mockMvc.perform(get("/users/1/items")
                .param("token", "123"))
                .andExpect(status().isInternalServerError());
    }

    @Test
    void testGetUserItems_InternalServerError() throws Exception {
        Mockito.when(userService.getUserItems(anyLong(), anyLong()))
                .thenThrow(new RuntimeException("Unexpected error"));
    
        mockMvc.perform(get("/users/1/items")
                .param("token", "123"))
                .andExpect(status().isInternalServerError());
    }    

    @Test
    void testSearchUser_Exception() throws Exception {
        Mockito.when(userService.getUserByUsername("testuser", 123L)).thenThrow(new RuntimeException("Database error"));
    
        mockMvc.perform(get("/users/search")
                .param("username", "testuser")
                .param("token", "123"))
                .andExpect(status().isInternalServerError());
    }

    @Test
    void testSearchUser_InternalServerError() throws Exception {
        Mockito.when(userService.getUserByUsername(any(), any()))
                .thenThrow(new RuntimeException("Unexpected error"));
    
        mockMvc.perform(get("/users/search")
                .param("username", "testuser")
                .param("token", "123"))
                .andExpect(status().isInternalServerError());
    }    

    @Test
    void testGetUserIdFromToken() throws Exception {
        Mockito.when(userService.getUserIdByToken(123L)).thenReturn(1L);

        mockMvc.perform(get("/users/userId")
                .param("token", "123"))
                .andExpect(status().isOk())
                .andExpect(content().string("1"));
    }

    @Test
    void testSearchUser_UserNotFound() throws Exception {
        Mockito.when(userService.getUserByUsername("nonexistentUser", 123L)).thenReturn(null);
    
        mockMvc.perform(get("/users/search")
                .param("username", "nonexistentUser")
                .param("token", "123"))
                .andExpect(status().isNotFound());
    }

    @Test
    void testGetUserIdFromToken_NotFound() throws Exception {
        Mockito.when(userService.getUserIdByToken(123L)).thenReturn(null);

        mockMvc.perform(get("/users/userId")
                .param("token", "123"))
                .andExpect(status().isNotFound());
    }

    @Test
    void testGetUserIdFromToken_InvalidToken() throws Exception {
        Mockito.when(userService.getUserIdByToken(999L)).thenReturn(null);
    
        mockMvc.perform(get("/users/userId")
                .param("token", "999"))
                .andExpect(status().isNotFound());
    }

    @Test
    void testGetUserIdFromToken_Exception() throws Exception {
        Mockito.when(userService.getUserIdByToken(123L)).thenThrow(new RuntimeException("Database error"));
    
        mockMvc.perform(get("/users/userId")
                .param("token", "123"))
                .andExpect(status().isInternalServerError());
    }

    @Test
    void testUpdateUserData() throws Exception {
        EditUserDTO editUserDTO = new EditUserDTO("NewName", "NewSurname", "NewDescription");

        mockMvc.perform(put("/users/editUserData")
                .param("token", "123")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(editUserDTO)))
                .andExpect(status().isNoContent());
    }

    @Test
    void testUpdateUserData_UserNotFound() throws Exception {
        EditUserDTO editUserDTO = new EditUserDTO("NewName", "NewSurname", "NewDescription");

        doThrow(new RuntimeException("User not found")).when(userService).editUserData(123L, "NewName", "NewSurname", "NewDescription");

        mockMvc.perform(put("/users/editUserData")
                .param("token", "123")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(editUserDTO)))
                .andExpect(status().isNotFound());
    }

    @Test
    void testDeleteUser_UserNotFound() throws Exception {
        doThrow(new RuntimeException("User not found")).when(userService).deleteUser(123L);

        mockMvc.perform(delete("/users/delete")
                .param("token", "123"))
                .andExpect(status().isNotFound());
    }

    @Test
    void testDeleteUser_Exception() throws Exception {
        doThrow(new RuntimeException("Database error")).when(userService).deleteUser(123L);
    
        mockMvc.perform(delete("/users/delete")
                .param("token", "123"))
                .andExpect(status().isInternalServerError());
    }

    @Test
    void testEditUser_MissingData() throws Exception {
        mockMvc.perform(put("/users/edit")
                .param("token", "123")
                .param("name", "NewName")
                .param("surname", "")
                .contentType(MediaType.MULTIPART_FORM_DATA))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testShowImagen_NotFound() throws Exception {
        mockMvc.perform(get("/users/profile/imagen/nonexistent.jpg"))
                .andExpect(status().isNotFound());
    }

    @Test
    void testUpdateProfileImage_Exception() throws Exception {
        MockMultipartFile profileImage = new MockMultipartFile(
                "profileImage", "image.jpg", MediaType.IMAGE_JPEG_VALUE, "test image".getBytes());
    
        doThrow(new RuntimeException("Database error")).when(userService).editProfileImage(123L, profileImage);
    
        mockMvc.perform(multipart("/users/editProfileImage")
                .file(profileImage)
                .param("token", "123")
                .with(request -> {
                    request.setMethod("PUT");
                    return request;
                }))
                .andExpect(status().isInternalServerError());
    }

    @Test
    void testCreateUser_UsernameAlreadyExists() throws Exception {
        SignupDTO signupDTO = new SignupDTO("test@example.com", "password123", "testuser", "Test", "User");
    
        doThrow(new RuntimeException("Username already exists")).when(userService)
                .createUser(any(), any(), any(), any(), any());
    
        mockMvc.perform(post("/users/signup")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(signupDTO)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testCreateUser_UnexpectedError() throws Exception {
        SignupDTO signupDTO = new SignupDTO("test@example.com", "password123", "testuser", "Test", "User");
    
        doThrow(new RuntimeException("Some unexpected error")).when(userService)
                .createUser(any(), any(), any(), any(), any());
    
        mockMvc.perform(post("/users/signup")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(signupDTO)))
                .andExpect(status().isInternalServerError());
    }

    @Test
    void testEditUser_UserNotFound() throws Exception {
        doThrow(new RuntimeException("User not found")).when(userService)
                .editUser(anyLong(), any(), any(), any(), any());

        mockMvc.perform(put("/users/edit")
                .param("token", "123")
                .param("name", "NewName")
                .param("surname", "NewSurname")
                .contentType(MediaType.MULTIPART_FORM_DATA))
                .andExpect(status().isNotFound());
    }

    @Test
    void testUpdateProfileImage_UserNotFound() throws Exception {
        MockMultipartFile profileImage = new MockMultipartFile(
                "profileImage", "image.jpg", MediaType.IMAGE_JPEG_VALUE, "test image".getBytes());
    
        doThrow(new RuntimeException("User not found")).when(userService).editProfileImage(123L, profileImage);
    
        mockMvc.perform(multipart("/users/editProfileImage")
                .file(profileImage)
                .param("token", "123")
                .with(request -> {
                    request.setMethod("PUT");
                    return request;
                }))
                .andExpect(status().isNotFound());
    }


    @Test
    void testSearchUsers_NoQuery() throws Exception {
        int page = 0;

        User user = new User();
        user.setId(1L);
        user.setUsername("testuser");

        List<User> users = List.of(user);
        Page<User> userPage = new PageImpl<>(users, PageRequest.of(page, 28), users.size());

        when(userService.searchUsers(null, null, page)).thenReturn(userPage);

        mockMvc.perform(get("/users/searchUsers")
                .param("page", String.valueOf(page))
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.content[0].username").value("testuser"))
            .andExpect(jsonPath("$.page").value(0))
            .andExpect(jsonPath("$.totalPages").value(1));
    }

    @Test
    void testSearchUsers_WithQuery() throws Exception {
        int page = 0;
        String query = "test";

        User user = new User();
        user.setId(1L);
        user.setUsername("testuser");

        List<User> users = List.of(user);
        Page<User> userPage = new PageImpl<>(users, PageRequest.of(page, 28), users.size());

        when(userService.searchUsers(null, query, page)).thenReturn(userPage);

        mockMvc.perform(get("/users/searchUsers")
                .param("search_text", query)
                .param("page", String.valueOf(page))
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.content[0].username").value("testuser"))
            .andExpect(jsonPath("$.page").value(0))
            .andExpect(jsonPath("$.totalPages").value(1));
    }

    @Test
    void testSearchUsers_WithToken() throws Exception {
        long token = 12345L;
        int page = 0;
        String query = "test";

        User user = new User();
        user.setId(1L);
        user.setUsername("testuser");

        List<User> users = List.of(user);
        Page<User> userPage = new PageImpl<>(users, PageRequest.of(page, 28), users.size());

        when(userService.searchUsers(token, query, page)).thenReturn(userPage);

        mockMvc.perform(get("/users/searchUsers")
                .param("token", String.valueOf(token))
                .param("search_text", query)
                .param("page", String.valueOf(page))
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.content[0].username").value("testuser"))
            .andExpect(jsonPath("$.page").value(0))
            .andExpect(jsonPath("$.totalPages").value(1));
    }

    @Test
    void testSearchUsers_NoResults() throws Exception {
        int page = 0;
        String query = "nonexistent";

        Page<User> emptyPage = new PageImpl<>(List.of(), PageRequest.of(page, 28), 0);

        when(userService.searchUsers(null, query, page)).thenReturn(emptyPage);

        mockMvc.perform(get("/users/searchUsers")
                .param("search_text", query)
                .param("page", String.valueOf(page))
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.content").isEmpty())
            .andExpect(jsonPath("$.page").value(0))
            .andExpect(jsonPath("$.totalPages").value(0));
    }

@Test
    void testSearchUsers_ExceptionHandling() throws Exception {
        int page = 0;
        String query = "test";

        when(userService.searchUsers(null, query, page)).thenThrow(new RuntimeException("Unexpected error"));

        mockMvc.perform(get("/users/searchUsers")
                .param("search_text", query)
                .param("page", String.valueOf(page))
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isBadRequest());
    }

        @Test
        void testFollowUser_Success() throws Exception {
        Long token = 1L;
        Long targetUserId = 2L;
        Long userId = 3L;

        when(userService.getUserIdByToken(token)).thenReturn(userId);
        doNothing().when(userService).followUser(userId, targetUserId);

        mockMvc.perform(post("/users/follow")
                .param("token", String.valueOf(token))
                .param("targetUserId", String.valueOf(targetUserId)))
                .andExpect(status().isOk())
                .andExpect(content().string("Usuario seguido con éxito"));
        }

        @Test
        void testFollowUser_InvalidToken() throws Exception {
        Long token = 1L;
        Long targetUserId = 2L;

        when(userService.getUserIdByToken(token)).thenReturn(null);

        mockMvc.perform(post("/users/follow")
                .param("token", String.valueOf(token))
                .param("targetUserId", String.valueOf(targetUserId)))
                .andExpect(status().isUnauthorized())
                .andExpect(content().string("Token inválido"));
        }

        @Test
        void testUnfollowUser_InvalidToken() throws Exception {
        Long token = 1L;
        Long targetUserId = 2L;

        when(userService.getUserIdByToken(token)).thenReturn(null);

        mockMvc.perform(post("/users/unfollow")
                .param("token", String.valueOf(token))
                .param("targetUserId", String.valueOf(targetUserId)))
                .andExpect(status().isUnauthorized())
                .andExpect(content().string("Token inválido"));
        }

        @Test
        void testUnfollowUser_Success() throws Exception {
        Long token = 1L;
        Long targetUserId = 2L;
        Long userId = 3L;

        when(userService.getUserIdByToken(token)).thenReturn(userId);
        doNothing().when(userService).unfollowUser(userId, targetUserId);

        mockMvc.perform(post("/users/unfollow")
                .param("token", String.valueOf(token))
                .param("targetUserId", String.valueOf(targetUserId)))
                .andExpect(status().isOk())
                .andExpect(content().string("Usuario dejado de seguir con éxito"));
        }

        @Test
        void testUnfollowUser_Exception() {
                Long token = 1L;
                Long targetUserId = 2L;
                Long userId = 3L;

                when(userService.getUserIdByToken(token)).thenReturn(userId);
                doThrow(new RuntimeException()).when(userService).unfollowUser(userId, targetUserId);

                ResponseEntity<String> response = userController.unfollowUser(token, targetUserId);

                assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
                assertEquals("Error al dejar de seguir al usuario", response.getBody());
        }

        @Test
        void testFollowUser_Exception() throws Exception {
        Long token = 1L;
        Long targetUserId = 2L;
        Long userId = 3L;

        when(userService.getUserIdByToken(token)).thenReturn(userId);
        doThrow(new RuntimeException()).when(userService).followUser(userId, targetUserId);

        mockMvc.perform(post("/users/follow")
                .param("token", String.valueOf(token))
                .param("targetUserId", String.valueOf(targetUserId)))
                .andExpect(status().isInternalServerError())
                .andExpect(content().string("Error al seguir al usuario"));
        }

         @Test
    void getFollowers_Success() throws Exception {
        User testUser = new User("test@example.com", "password", "testuser", "Test", "User");
        testUser.setId(1L);
        testUser.setDescription("Test Description");
        testUser.setProfileImage("profile.jpg");

        when(userService.getFollowers(1L)).thenReturn(List.of(testUser));


        mockMvc.perform(get("/users/followers")
                .param("targetUserId", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].username").value("testuser"))
                .andExpect(jsonPath("$[0].name").value("Test"))
                .andExpect(jsonPath("$[0].surname").value("User"))
                .andExpect(jsonPath("$[0].description").value("Test Description"))
                .andExpect(jsonPath("$[0].profileImage").value("profile.jpg"));
    }

    @Test
    void getFollowers_InternalError() throws Exception {

        when(userService.getFollowers(1L)).thenThrow(new RuntimeException("Database error"));


        mockMvc.perform(get("/users/followers")
                .param("targetUserId", "1"))
                .andExpect(status().isInternalServerError());
    }

    @Test
    void getFollowing_Success() throws Exception {
        User testUser = new User("test@example.com", "password", "testuser", "Test", "User");
        testUser.setId(1L);
        testUser.setDescription("Test Description");
        testUser.setProfileImage("profile.jpg");

        when(userService.getFollowing(1L)).thenReturn(List.of(testUser));

        mockMvc.perform(get("/users/following")
                .param("targetUserId", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].username").value("testuser"));
    }

    @Test
    void getFollowing_EmptyList() throws Exception {

        when(userService.getFollowing(1L)).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/users/following")
                .param("targetUserId", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$").isEmpty());
    }
}