package com.spq.vinted.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import com.spq.vinted.dto.RatingDTO;
import com.spq.vinted.dto.RatingInfoDTO;
import com.spq.vinted.model.Category;
import com.spq.vinted.model.Clothes;
import com.spq.vinted.model.ClothesSize;
import com.spq.vinted.model.ClothesType;
import com.spq.vinted.model.Item;
import com.spq.vinted.model.Rating;
import com.spq.vinted.model.User;
import com.spq.vinted.repository.RatingRepository;
import com.spq.vinted.repository.UserRepository;

public class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private RatingRepository ratingRepository;

    @InjectMocks
    private UserService userService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testCreateUser() {
        String email = "test@example.com";
        String username = "testuser";
        String name = "Test";
        String surname = "User";
        String password = "password";

        when(userRepository.findByEmail(email)).thenReturn(Optional.empty());
        when(userRepository.findByUsername(username)).thenReturn(Optional.empty());

        userService.createUser(email, password, username, name, surname);

        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    void testLogIn() {
        String email = "test@example.com";
        String password = "password";
        User user = new User(email, password, "testuser", "Test", "User");

        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));

        long token = userService.logIn(email, password);

        assertNotNull(token);
        assertTrue(userService.getUserByToken(token) != null);
    }

    @Test
    void testLogInInvalidCredentials() {
        String email = "test@example.com";
        String password = "wrongpassword";
        User user = new User(email, "password", "testuser", "Test", "User");

        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));

        Exception exception = assertThrows(RuntimeException.class, () -> userService.logIn(email, password));
        assertEquals("Invalid credentials", exception.getMessage());
    }

    @Test
    void testDeleteUser() throws Exception {
        long token = 12345L;
        String email = "test@example.com";
        String password = "password";
        User user = new User(email, password, "testuser", "Test", "User");

        Field activeUsersField = UserService.class.getDeclaredField("activeUsers");
        activeUsersField.setAccessible(true);
        @SuppressWarnings("unchecked")
        Map<Long, User> activeUsers = (Map<Long, User>) activeUsersField.get(userService);

        activeUsers.put(token, user);

        userService.deleteUser(token);

        verify(userRepository, times(1)).delete(user);
        assertNull(activeUsers.get(token));
    }

    @Test
    void testDeleteUserTokenNotFound() throws Exception {
        long invalidToken = 99999L;

        Field activeUsersField = UserService.class.getDeclaredField("activeUsers");
        activeUsersField.setAccessible(true);
        @SuppressWarnings("unchecked")
        Map<Long, User> activeUsers = (Map<Long, User>) activeUsersField.get(userService);

        activeUsers.clear();

        Exception exception = assertThrows(RuntimeException.class, () -> userService.deleteUser(invalidToken));
        assertEquals("Token not found in active users list", exception.getMessage());
        verify(userRepository, times(0)).delete(any(User.class));
    }

    @Test
    void testEditUserData() throws Exception {
        long token = 12345L;
        String name = "Updated Name";
        String surname = "Updated Surname";
        String description = "Updated Description";

        User user = new User("test@example.com", "password", "testuser", "Test", "User");

        Field activeUsersField = UserService.class.getDeclaredField("activeUsers");
        activeUsersField.setAccessible(true);
        @SuppressWarnings("unchecked")
        Map<Long, User> activeUsers = (Map<Long, User>) activeUsersField.get(userService);

        activeUsers.put(token, user);

        userService.editUserData(token, name, surname, description);

        assertEquals(name, user.getName()); 
        assertEquals(surname, user.getSurname()); 
        assertEquals(description, user.getDescription()); 
        verify(userRepository, times(1)).save(user); 
    }

    @Test
    void testEditUserDataUserNotFound() throws Exception {
        long invalidToken = 99999L;
        String name = "Updated Name";
        String surname = "Updated Surname";
        String description = "Updated Description";

        Field activeUsersField = UserService.class.getDeclaredField("activeUsers");
        activeUsersField.setAccessible(true);
        @SuppressWarnings("unchecked")
        Map<Long, User> activeUsers = (Map<Long, User>) activeUsersField.get(userService);

        activeUsers.clear();

        Exception exception = assertThrows(RuntimeException.class, () -> userService.editUserData(invalidToken, name, surname, description));
        assertEquals("User not found", exception.getMessage()); 
        verify(userRepository, times(0)).save(any(User.class)); 
    }

    @Test
    void testEditUserWithProfileImage() throws Exception {
        long token = 12345L;
        String name = "Updated Name";
        String surname = "Updated Surname";
        String description = "Updated Description";
        String originalFilename = "profile.jpg";
        MultipartFile profileImage = mock(MultipartFile.class);

        when(profileImage.getOriginalFilename()).thenReturn(originalFilename);
        when(profileImage.getInputStream()).thenReturn(new ByteArrayInputStream("dummy content".getBytes()));

        User user = new User("test@example.com", "password", "testuser", "Test", "User");
        user.setProfileImage("oldImage.jpg");

        Field activeUsersField = UserService.class.getDeclaredField("activeUsers");
        activeUsersField.setAccessible(true);
        @SuppressWarnings("unchecked")
        Map<Long, User> activeUsers = (Map<Long, User>) activeUsersField.get(userService);

        activeUsers.put(token, user);

        Path filePath = mock(Path.class);

        try (MockedStatic<Paths> mockedPaths = mockStatic(Paths.class);
            MockedStatic<Files> mockedFiles = mockStatic(Files.class)) {

            mockedPaths.when(() -> Paths.get("uploads/profiles/")).thenReturn(filePath);
            when(filePath.resolve(anyString())).thenReturn(filePath);
            when(filePath.toAbsolutePath()).thenReturn(filePath);

            mockedFiles.when(() -> Files.copy(any(InputStream.class), eq(filePath))).thenReturn(1L);

            userService.editUser(token, name, surname, description, profileImage);

            assertEquals(name, user.getName());
            assertEquals(surname, user.getSurname());
            assertEquals(description, user.getDescription());
            assertNotNull(user.getProfileImage());
            assertTrue(user.getProfileImage().contains(originalFilename));
            mockedFiles.verify(() -> Files.copy(any(InputStream.class), eq(filePath)), times(1));
            verify(userRepository, times(1)).save(user);
        }
    }

    @Test
    void testEditUserWithoutProfileImage() throws Exception {
        long token = 12345L;
        String name = "Updated Name";
        String surname = "Updated Surname";
        String description = "Updated Description";

        User user = new User("test@example.com", "password", "testuser", "Test", "User");

        Field activeUsersField = UserService.class.getDeclaredField("activeUsers");
        activeUsersField.setAccessible(true);
        @SuppressWarnings("unchecked")
        Map<Long, User> activeUsers = (Map<Long, User>) activeUsersField.get(userService);

        activeUsers.put(token, user);

        userService.editUser(token, name, surname, description, null);

        assertEquals(name, user.getName());
        assertEquals(surname, user.getSurname());
        assertEquals(description, user.getDescription());
        assertNull(user.getProfileImage());
        verify(userRepository, times(1)).save(user);
    }

    @Test
    void testEditUserUserNotFound() {
        long invalidToken = 99999L;
        String name = "Updated Name";
        String surname = "Updated Surname";
        String description = "Updated Description";
        MultipartFile profileImage = mock(MultipartFile.class);

        Exception exception = assertThrows(RuntimeException.class, () -> userService.editUser(invalidToken, name, surname, description, profileImage));
        assertEquals("User not found", exception.getMessage());
    }

    @Test
    void testEditUserProfileImageUploadError() throws Exception {
        long token = 12345L;
        String name = "Updated Name";
        String surname = "Updated Surname";
        String description = "Updated Description";
        MultipartFile profileImage = mock(MultipartFile.class);

        when(profileImage.getOriginalFilename()).thenReturn("profile.jpg");
        when(profileImage.getInputStream()).thenThrow(new IOException("Error al leer el archivo"));

        User user = new User("test@example.com", "password", "testuser", "Test", "User");

        Field activeUsersField = UserService.class.getDeclaredField("activeUsers");
        activeUsersField.setAccessible(true);
        @SuppressWarnings("unchecked")
        Map<Long, User> activeUsers = (Map<Long, User>) activeUsersField.get(userService);

        activeUsers.put(token, user);

        Exception exception = assertThrows(RuntimeException.class, () -> userService.editUser(token, name, surname, description, profileImage));
        assertEquals("Error al subir la imagen de perfil", exception.getMessage());
        verify(userRepository, times(0)).save(user);
    }

    @Test
    void testLogOut() throws Exception {
        String email = "test@example.com";
        String password = "password";
        User user = new User(email, password, "testuser", "Test", "User");

        Field activeUsersField = UserService.class.getDeclaredField("activeUsers");
        activeUsersField.setAccessible(true);
        @SuppressWarnings("unchecked")
        Map<Long, User> activeUsers = (Map<Long, User>) activeUsersField.get(userService);

        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));
        long token = userService.logIn(email, password);

        assertNotNull(activeUsers.get(token));

        userService.LogOut(token);

        assertNull(activeUsers.get(token));
    }

    @Test
    void testLogOutTokenNotFound() throws Exception {
        long invalidToken = 99999L;

        Field activeUsersField = UserService.class.getDeclaredField("activeUsers");
        activeUsersField.setAccessible(true);
        @SuppressWarnings("unchecked")
        Map<Long, User> activeUsers = (Map<Long, User>) activeUsersField.get(userService);

        activeUsers.clear();

        Exception exception = assertThrows(RuntimeException.class, () -> userService.LogOut(invalidToken));
        assertEquals("Token not found in active users list", exception.getMessage());
    }

    @Test
    void testGetUserById() {
        long userId = 1L;
        User user = new User("test@example.com", "password", "testuser", "Test", "User");
        user.setId(userId);

        when(userRepository.findById(String.valueOf(userId))).thenReturn(Optional.of(user));

        User result = userService.getUserById(userId);

        assertEquals(userId, result.getId());
        assertEquals("testuser", result.getUsername());
    }

    @Test
    void testEditProfileImageNoPreviousImage() throws Exception {
        long token = 12345L;
        String originalFilename = "profile.jpg";
        MultipartFile profileImage = mock(MultipartFile.class);

        when(profileImage.getOriginalFilename()).thenReturn(originalFilename);
        when(profileImage.getInputStream()).thenReturn(new ByteArrayInputStream("dummy content".getBytes()));

        User user = new User("test@example.com", "password", "testuser", "Test", "User");
        user.setProfileImage(null); 
        Field activeUsersField = UserService.class.getDeclaredField("activeUsers");
        activeUsersField.setAccessible(true);
        @SuppressWarnings("unchecked")
        Map<Long, User> activeUsers = (Map<Long, User>) activeUsersField.get(userService);

        activeUsers.put(token, user);

        Path filePath = mock(Path.class);

        try (MockedStatic<Paths> mockedPaths = mockStatic(Paths.class);
            MockedStatic<Files> mockedFiles = mockStatic(Files.class)) {

            mockedPaths.when(() -> Paths.get("uploads/profiles/")).thenReturn(filePath);
            when(filePath.resolve(anyString())).thenReturn(filePath);
            when(filePath.toAbsolutePath()).thenReturn(filePath);

            mockedFiles.when(() -> Files.copy(any(InputStream.class), eq(filePath))).thenReturn(1L);

            userService.editProfileImage(token, profileImage);

            assertNotNull(user.getProfileImage());
            assertTrue(user.getProfileImage().contains(originalFilename));
            mockedFiles.verify(() -> Files.copy(any(InputStream.class), eq(filePath)), times(1));
            verify(userRepository, times(1)).save(user);
        }
    }

    @Test
    void testEditProfileImageErrorDeletingOldImage() throws Exception {
        long token = 12345L;
        String originalFilename = "profile.jpg";
        MultipartFile profileImage = mock(MultipartFile.class);

        when(profileImage.getOriginalFilename()).thenReturn(originalFilename);
        when(profileImage.getInputStream()).thenReturn(new ByteArrayInputStream("dummy content".getBytes()));

        User user = new User("test@example.com", "password", "testuser", "Test", "User");
        user.setProfileImage("oldImage.jpg");

        Field activeUsersField = UserService.class.getDeclaredField("activeUsers");
        activeUsersField.setAccessible(true);
        @SuppressWarnings("unchecked")
        Map<Long, User> activeUsers = (Map<Long, User>) activeUsersField.get(userService);

        activeUsers.put(token, user);

        File oldFile = mock(File.class);
        Path filePath = mock(Path.class);

        try (MockedStatic<Paths> mockedPaths = mockStatic(Paths.class);
            MockedStatic<Files> mockedFiles = mockStatic(Files.class)) {

            mockedPaths.when(() -> Paths.get("uploads/profiles/")).thenReturn(filePath);
            when(filePath.resolve(anyString())).thenReturn(filePath);
            when(filePath.toAbsolutePath()).thenReturn(filePath);

            when(oldFile.exists()).thenReturn(true);
            when(oldFile.delete()).thenReturn(false);

            mockedFiles.when(() -> Files.copy(any(InputStream.class), eq(filePath))).thenReturn(1L);

            userService.editProfileImage(token, profileImage);

            assertNotNull(user.getProfileImage());
            assertTrue(user.getProfileImage().contains(originalFilename));
            mockedFiles.verify(() -> Files.copy(any(InputStream.class), eq(filePath)), times(1));
            verify(userRepository, times(1)).save(user);
        }
    }

    @Test
    void testGetUserIdByToken() throws Exception {
        Long token = 12345L;
        Long userId = 1L;
        User user = new User("test@example.com", "password", "testuser", "Test", "User");
        user.setId(userId);

        Field activeUsersField = UserService.class.getDeclaredField("activeUsers");
        activeUsersField.setAccessible(true);
        @SuppressWarnings("unchecked")
        Map<Long, User> activeUsers = (Map<Long, User>) activeUsersField.get(userService);

        activeUsers.put(token, user);

        Long result = userService.getUserIdByToken(token);

        assertEquals(userId, result);
    }

    @Test
    void testGetUserIdByTokenInvalidToken() throws Exception {
        Long invalidToken = 99999L;

        Field activeUsersField = UserService.class.getDeclaredField("activeUsers");
        activeUsersField.setAccessible(true);
        @SuppressWarnings("unchecked")
        Map<Long, User> activeUsers = (Map<Long, User>) activeUsersField.get(userService);

        activeUsers.clear();

        Long result = userService.getUserIdByToken(invalidToken);

        assertNull(result);
    }

    @Test
    void testGetUserByUsername() {
        String username = "testuser";
        Long token = 12345L;

        User user = new User("test@example.com", "password", username, "Test", "User");

        when(userRepository.findByUsername(username)).thenReturn(Optional.of(user));

        User result = userService.getUserByUsername(username, token);

        assertNotNull(result);
        assertEquals(username, result.getUsername());
        assertEquals("test@example.com", result.getEmail());
        verify(userRepository, times(1)).findByUsername(username);
    }

    @Test
    void testGetUserByUsernameNotFound() {
        String username = "nonexistentuser";
        Long token = 12345L;

        when(userRepository.findByUsername(username)).thenReturn(Optional.empty());

        Exception exception = assertThrows(RuntimeException.class, () -> userService.getUserByUsername(username, token));
        assertEquals("User not found", exception.getMessage());
        verify(userRepository, times(1)).findByUsername(username);
    }

    @Test
    void testSaveUser() {
        User user = new User("test@example.com", "password", "testuser", "Test", "User");

        userService.saveUser(user);

        verify(userRepository, times(1)).save(user);
    }

    @Test
    void testFollowUser() {
        long userId = 1L;
        long targetUserId = 2L;

        User user = new User("user1@example.com", "password", "user1", "User", "One");
        user.setId(userId);

        User targetUser = new User("user2@example.com", "password", "user2", "User", "Two");
        targetUser.setId(targetUserId);

        when(userRepository.findById(String.valueOf(userId))).thenReturn(Optional.of(user));
        when(userRepository.findById(String.valueOf(targetUserId))).thenReturn(Optional.of(targetUser));

        userService.followUser(userId, targetUserId);

        assertTrue(user.getFollowing().contains(targetUser));
        verify(userRepository, times(1)).save(user);
    }

    @Test
    void testUnfollowUserUserNotFound() {
        long userId = 1L;
        long targetUserId = 2L;
    
        when(userRepository.findById(String.valueOf(userId))).thenReturn(Optional.empty());
    
        Exception exception = assertThrows(RuntimeException.class, () -> userService.unfollowUser(userId, targetUserId));
        assertEquals("Usuario no encontrado", exception.getMessage()); 
        verify(userRepository, times(0)).save(any(User.class));
    }
    
    @Test
    void testUnfollowUserTargetUserNotFound() {
        long userId = 1L;
        long targetUserId = 2L;
    
        User user = new User("user1@example.com", "password", "user1", "User", "One");
        user.setId(userId);
    
        when(userRepository.findById(String.valueOf(userId))).thenReturn(Optional.of(user));
        when(userRepository.findById(String.valueOf(targetUserId))).thenReturn(Optional.empty());
    
        Exception exception = assertThrows(RuntimeException.class, () -> userService.unfollowUser(userId, targetUserId));
        assertEquals("Usuario objetivo no encontrado", exception.getMessage()); 
        verify(userRepository, times(0)).save(any(User.class));
    }

    @Test
    void testUnfollowUser_RemovesFollowingAndSaves() {
        long userId = 1L;
        long targetUserId = 2L;

        User user = new User("user1@example.com", "password", "user1", "User", "One");
        user.setId(userId);

        User targetUser = new User("user2@example.com", "password", "user2", "User", "Two");
        targetUser.setId(targetUserId);

        user.getFollowing().add(targetUser);

        when(userRepository.findById(String.valueOf(userId))).thenReturn(Optional.of(user));
        when(userRepository.findById(String.valueOf(targetUserId))).thenReturn(Optional.of(targetUser));

        userService.unfollowUser(userId, targetUserId);

        assertFalse(user.getFollowing().contains(targetUser));
        verify(userRepository, times(1)).save(user);
    }

    @Test
    void testGetFollowers() {
        long userId = 1L;
        User user = new User("user1@example.com", "password", "user1", "User", "One");
        User follower = new User("user2@example.com", "password", "user2", "User", "Two");

        user.getFollowers().add(follower);

        when(userRepository.findById(String.valueOf(userId))).thenReturn(Optional.of(user));

        var followers = userService.getFollowers(userId);

        assertEquals(1, followers.size());
        assertEquals(follower, followers.get(0));
    }

    @Test
    void testGetFollowing() {
        long userId = 1L;
        User user = new User("user1@example.com", "password", "user1", "User", "One");
        User following = new User("user2@example.com", "password", "user2", "User", "Two");

        user.getFollowing().add(following);

        when(userRepository.findById(String.valueOf(userId))).thenReturn(Optional.of(user));

        var followingList = userService.getFollowing(userId);

        assertEquals(1, followingList.size());
        assertEquals(following, followingList.get(0));
    }

    @Test
    void testGetUserItems() {
        Long userId = 1L;
        Long token = 12345L;

        User user = new User("test@example.com", "password", "testuser", "Test", "User");

        Item item1 = new Clothes("Item 1", "Description 1", 10.0f, ClothesSize.M, ClothesType.TSHIRT, Category.WOMAN, user);
        Item item2 = new Clothes("Item 2", "Description 2", 20.0f, ClothesSize.L, ClothesType.PANTS, Category.MAN, user);

        List<Item> itemsForSale = Arrays.asList(item1, item2);
        user.setItemsForSale(itemsForSale);

        when(userRepository.findById(String.valueOf(userId))).thenReturn(Optional.of(user));

        List<Item> result = userService.getUserItems(userId, token);

        assertEquals(2, result.size());
        assertEquals("Item 1", result.get(0).getTitle());
        assertEquals("Item 2", result.get(1).getTitle());
        verify(userRepository, times(1)).findById(String.valueOf(userId));
    }

    @Test
    void testAddRating() {
        RatingDTO ratingDTO = new RatingDTO(1L, 2L, 5, "Excelente usuario");

        userService.addRating(ratingDTO);

        verify(ratingRepository, times(1)).save(any(Rating.class));
    }

    @Test
    void testGetRatingsForUser() {
        long userId = 1L;
        Rating rating1 = new Rating();
        rating1.setRatedUserId(userId);
        rating1.setRatingUserId(2L);
        rating1.setScore(5);
        rating1.setComment("Excelente usuario");

        Rating rating2 = new Rating();
        rating2.setRatedUserId(userId);
        rating2.setRatingUserId(3L);
        rating2.setScore(4);
        rating2.setComment("Buen usuario");

        List<Rating> ratings = Arrays.asList(rating1, rating2);

        when(ratingRepository.findByRatedUserId(userId)).thenReturn(ratings);

        List<RatingInfoDTO> result = userService.getRatingsForUser(userId);

        assertEquals(2, result.size());
        assertEquals(5, result.get(0).getScore());
        assertEquals("Excelente usuario", result.get(0).getComment());
        assertEquals(4, result.get(1).getScore());
        assertEquals("Buen usuario", result.get(1).getComment());
    }
    @Test
    void testSearchUsers_EmptyQuery() {
        int page = 0;
        Pageable pageable = PageRequest.of(page, 28);

        User user = new User();
        user.setId(1L);
        user.setUsername("testuser");

        List<User> users = List.of(user);
        Page<User> userPage = new PageImpl<>(users, pageable, users.size());

        when(userRepository.findAll(pageable)).thenReturn(userPage);

        Page<User> result = userService.searchUsers(null, "", page);

        assertEquals(1, result.getTotalElements());
        assertEquals("testuser", result.getContent().get(0).getUsername());
    }

    @Test
    void testSearchUsers_WithQuery() {
        int page = 0;
        Pageable pageable = PageRequest.of(page, 28);
        String query = "testuser";

        User user = new User();
        user.setId(1L);
        user.setUsername("testuser");

        List<User> users = List.of(user);
        Page<User> userPage = new PageImpl<>(users, pageable, users.size());

        when(userRepository.searchByQuery(query.toLowerCase(), pageable)).thenReturn(userPage);

        Page<User> result = userService.searchUsers(null, query, page);

        assertEquals(1, result.getTotalElements());
        assertEquals("testuser", result.getContent().get(0).getUsername());
    }

    @Test
    void testSearchUsers_NullQuery() {
        int page = 0;
        Pageable pageable = PageRequest.of(page, 28);

        User user = new User();
        user.setId(1L);
        user.setUsername("defaultuser");

        List<User> users = List.of(user);
        Page<User> userPage = new PageImpl<>(users, pageable, users.size());

        when(userRepository.findAll(pageable)).thenReturn(userPage);

        Page<User> result = userService.searchUsers(null, null, page);

        assertEquals(1, result.getTotalElements());
        assertEquals("defaultuser", result.getContent().get(0).getUsername());
    }

    @Test
    void testSearchUsers_NoResults() {
        int page = 0;
        Pageable pageable = PageRequest.of(page, 28);
        String query = "nonexistent";

        Page<User> emptyPage = new PageImpl<>(List.of(), pageable, 0);

        when(userRepository.searchByQuery(query.toLowerCase(), pageable)).thenReturn(emptyPage);

        Page<User> result = userService.searchUsers(null, query, page);

        assertTrue(result.isEmpty());
    }

}