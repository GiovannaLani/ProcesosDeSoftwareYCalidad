package com.spq.vinted.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.multipart.MultipartFile;

import com.spq.vinted.dto.RatingDTO;
import com.spq.vinted.model.Item;
import com.spq.vinted.model.Rating;
import com.spq.vinted.model.User;
import com.spq.vinted.repository.ItemRepository;
import com.spq.vinted.repository.RatingRepository;
import com.spq.vinted.repository.UserRepository;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private ItemRepository itemRepository;

    @Mock
    private RatingRepository ratingRepository;

    @InjectMocks
    private UserService userService;

    private User testUser;
    private MultipartFile mockFile;

    @BeforeEach
    void setUp() {
        testUser = new User("test@example.com", "password123", "testuser", "Test", "User");
        testUser.setId(1L);
        
        mockFile = mock(MultipartFile.class);
    }

    @Test
    void testCreateUser_Success() {
        // Arrange
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.empty());
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.empty());
        when(userRepository.save(any(User.class))).thenReturn(testUser);

        // Act
        userService.createUser("test@example.com", "password123", "testuser", "Test", "User");

        // Assert
        verify(userRepository).save(any(User.class));
    }

    @Test
    void testCreateUser_EmailAlreadyExists() {
        // Arrange
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(testUser));

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            userService.createUser("test@example.com", "password123", "testuser", "Test", "User");
        });
        assertEquals("User already exists", exception.getMessage());
    }

    @Test
    void testCreateUser_UsernameAlreadyExists() {
        // Arrange
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.empty());
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(testUser));

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            userService.createUser("test@example.com", "password123", "testuser", "Test", "User");
        });
        assertEquals("Username already exists", exception.getMessage());
    }

    @Test
    void testLogIn_Success() {
        // Arrange
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(testUser));

        // Act
        long token = userService.logIn("test@example.com", "password123");

        // Assert
        assertTrue(token > 0);
        assertEquals(testUser, userService.getUserByToken(token));
    }

    @Test
    void testLogIn_UserNotFound() {
        // Arrange
        when(userRepository.findByEmail("nonexistent@example.com")).thenReturn(Optional.empty());

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            userService.logIn("nonexistent@example.com", "password123");
        });
        assertEquals("User not found", exception.getMessage());
    }

    @Test
    void testLogIn_InvalidPassword() {
        // Arrange
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(testUser));

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            userService.logIn("test@example.com", "wrongpassword");
        });
        assertEquals("Invalid credentials", exception.getMessage());
    }

    @Test
    void testLogOut_Success() {
        // Arrange
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(testUser));
        long token = userService.logIn("test@example.com", "password123");

        // Act
        userService.LogOut(token);

        // Assert
        assertNull(userService.getUserByToken(token));
    }

    @Test
    void testLogOut_TokenNotFound() {
        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            userService.LogOut(999999L);
        });
        assertEquals("Token not found in active users list", exception.getMessage());
    }

    @Test
    void testGetUserById_Success() {
        // Arrange
        when(userRepository.findById("1")).thenReturn(Optional.of(testUser));

        // Act
        User result = userService.getUserById(1L);

        // Assert
        assertEquals(testUser, result);
    }

    @Test
    void testGetUserById_NotFound() {
        // Arrange
        when(userRepository.findById("999")).thenReturn(Optional.empty());

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            userService.getUserById(999L);
        });
        assertEquals("User not found", exception.getMessage());
    }

    @Test
    void testDeleteUser_Success() {
        // Arrange
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(testUser));
        long token = userService.logIn("test@example.com", "password123");

        // Act
        userService.deleteUser(token);

        // Assert
        verify(userRepository).delete(testUser);
        assertNull(userService.getUserByToken(token));
    }

    @Test
    void testDeleteUser_TokenNotFound() {
        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            userService.deleteUser(999999L);
        });
        assertEquals("Token not found in active users list", exception.getMessage());
    }

    @Test
    void testEditUserData_Success() {
        // Arrange
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(testUser));
        long token = userService.logIn("test@example.com", "password123");

        // Act
        userService.editUserData(token, "NewName", "NewSurname", "New description");

        // Assert
        assertEquals("NewName", testUser.getName());
        assertEquals("NewSurname", testUser.getSurname());
        assertEquals("New description", testUser.getDescription());
        verify(userRepository).save(testUser);
    }

    @Test
    void testEditUserData_UserNotFound() {
        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            userService.editUserData(999999L, "NewName", "NewSurname", "New description");
        });
        assertEquals("User not found", exception.getMessage());
    }

    @Test
    void testEditProfileImage_Success() throws IOException {
        // Arrange
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(testUser));
        long token = userService.logIn("test@example.com", "password123");
        
        when(mockFile.getOriginalFilename()).thenReturn("profile.jpg");
        when(mockFile.getInputStream()).thenReturn(new ByteArrayInputStream("test image content".getBytes()));

        // Act
        userService.editProfileImage(token, mockFile);

        // Assert
        assertNotNull(testUser.getProfileImage());
        verify(userRepository).save(testUser);
    }

    @Test
    void testEditProfileImage_UserNotFound() {
        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            userService.editProfileImage(999999L, mockFile);
        });
        assertEquals("User not found", exception.getMessage());
    }

    @Test
    void testGetUserByUsername_Success() {
        // Arrange
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(testUser));

        // Act
        User result = userService.getUserByUsername("testuser", 123L);

        // Assert
        assertEquals(testUser, result);
    }

    @Test
    void testGetUserByUsername_NotFound() {
        // Arrange
        when(userRepository.findByUsername("nonexistent")).thenReturn(Optional.empty());

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            userService.getUserByUsername("nonexistent", 123L);
        });
        assertEquals("User not found", exception.getMessage());
    }

    @Test
    void testGetUserItems_Success() {
        // Arrange
        List<Item> items = new ArrayList<>();
        Item item1 = mock(Item.class);
        Item item2 = mock(Item.class);
        items.add(item1);
        items.add(item2);
        
        testUser.setItemsForSale(items);
        when(userRepository.findById("1")).thenReturn(Optional.of(testUser));

        // Act
        List<Item> result = userService.getUserItems(1L, 123L);

        // Assert
        assertEquals(2, result.size());
        assertEquals(items, result);
    }

    @Test
    void testGetUserItems_UserNotFound() {
        // Arrange
        when(userRepository.findById("999")).thenReturn(Optional.empty());

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            userService.getUserItems(999L, 123L);
        });
        assertEquals("User not found", exception.getMessage());
    }

    @Test
    void testAddRating_Success() {
        // Arrange
        RatingDTO ratingDTO = new RatingDTO(1L, 2L, 5, "Great seller!");

        // Act
        userService.addRating(ratingDTO);

        // Assert
        verify(ratingRepository).save(any(Rating.class));
    }

    @Test
    void testGetRatingsForUser_Success() {
        // Arrange
        Rating rating1 = new Rating();
        rating1.setRatedUserId(1L);
        rating1.setRatingUserId(2L);
        rating1.setScore(5);
        rating1.setComment("Excellent!");

        Rating rating2 = new Rating();
        rating2.setRatedUserId(1L);
        rating2.setRatingUserId(3L);
        rating2.setScore(4);
        rating2.setComment("Good!");

        List<Rating> ratings = Arrays.asList(rating1, rating2);
        when(ratingRepository.findByRatedUserId(1L)).thenReturn(ratings);

        // Act
        List<RatingDTO> result = userService.getRatingsForUser(1L);

        // Assert
        assertEquals(2, result.size());
        assertEquals(5, result.get(0).getScore());
        assertEquals(4, result.get(1).getScore());
    }

    @Test
    void testGetUserIdByToken_Success() {
        // Arrange
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(testUser));
        long token = userService.logIn("test@example.com", "password123");

        // Act
        Long userId = userService.getUserIdByToken(token);

        // Assert
        assertEquals(1L, userId);
    }

    @Test
    void testGetUserIdByToken_TokenNotFound() {
        // Act
        Long userId = userService.getUserIdByToken(999999L);

        // Assert
        assertNull(userId);
    }

    @Test
    void testSaveUser() {
        // Act
        userService.saveUser(testUser);

        // Assert
        verify(userRepository).save(testUser);
    }

    @Test
    void testEditUser_WithProfileImage_Success() throws IOException {
        // Arrange
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(testUser));
        long token = userService.logIn("test@example.com", "password123");
        
        when(mockFile.isEmpty()).thenReturn(false);
        when(mockFile.getOriginalFilename()).thenReturn("profile.jpg");
        when(mockFile.getInputStream()).thenReturn(new ByteArrayInputStream("test image content".getBytes()));

        // Act
        userService.editUser(token, "NewName", "NewSurname", "New description", mockFile);

        // Assert
        assertEquals("NewName", testUser.getName());
        assertEquals("NewSurname", testUser.getSurname());
        assertEquals("New description", testUser.getDescription());
        assertNotNull(testUser.getProfileImage());
        verify(userRepository).save(testUser);
    }

    @Test
    void testEditUser_WithoutProfileImage_Success() {
        // Arrange
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(testUser));
        long token = userService.logIn("test@example.com", "password123");

        // Act
        userService.editUser(token, "NewName", "NewSurname", "New description", null);

        // Assert
        assertEquals("NewName", testUser.getName());
        assertEquals("NewSurname", testUser.getSurname());
        assertEquals("New description", testUser.getDescription());
        verify(userRepository).save(testUser);
    }

    @Test
    void testEditUser_UserNotFound() {
        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            userService.editUser(999999L, "NewName", "NewSurname", "New description", null);
        });
        assertEquals("User not found", exception.getMessage());
    }
}