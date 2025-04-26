package com.spq.vinted.service;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.spq.vinted.dto.RatingDTO;
import com.spq.vinted.dto.UserDTO;
import com.spq.vinted.model.Item;
import com.spq.vinted.model.Rating;
import com.spq.vinted.model.User;
import com.spq.vinted.repository.ItemRepository;
import com.spq.vinted.repository.RatingRepository;
import com.spq.vinted.repository.UserRepository;

@Service
public class UserService {
    private Map<Long, User> activeUsers;
	private UserRepository userRepository;
	private final ItemRepository itemRepository;
	private final RatingRepository ratingRepository;
	
	public UserService(UserRepository userRepository, ItemRepository itemRepository, RatingRepository ratingRepository) {
		activeUsers = new HashMap<>();
		this.userRepository = userRepository;
		this.itemRepository = itemRepository;
		this.ratingRepository = ratingRepository;
	}
	
	public void createUser(String email, String password, String username, String name, String surname) {
		User newUser = new User(email, password, username, name, surname);
		userRepository.findByEmail(email).ifPresent(u -> {throw new RuntimeException("User already exists");});
		userRepository.findByUsername(username).ifPresent(u -> {throw new RuntimeException("Username already exists");});
		userRepository.save(newUser);
	}
	
	public long logIn(String email, String password) {
		User user = userRepository.findByEmail(email).orElseThrow(() -> new RuntimeException("User not found"));
		if (!user.getPassword().equals(password)) {
			throw new RuntimeException("Invalid credentials");
		}		
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
	
	public User getUserByToken(long token) {
		System.out.println("Active users: ");
		for (Map.Entry<Long, User> entry : activeUsers.entrySet()) {
			System.out.println("Token: " + entry.getKey() + ", User: " + entry.getValue());
		}
		User user = activeUsers.get(token);
		return user;
	}
	public User getUserById(long id) {
		User user = userRepository.findById(String.valueOf(id)).orElseThrow(() -> new RuntimeException("User not found"));
		return user;
	}

	public void deleteUser(long token) {
		User user = activeUsers.get(token);
		if (user == null) {
			throw new RuntimeException("Token not found in active users list");
		}
		userRepository.delete(user);
		activeUsers.remove(token);
	}
	public void editUser(long token, String name, String surname, String description, MultipartFile profileImage) {
		User user = activeUsers.get(token);
		if (user == null) {
			throw new RuntimeException("User not found");
		}
	
		user.setName(name);
		user.setSurname(surname);
		user.setDescription(description);
	
		if (profileImage != null && !profileImage.isEmpty()) {
			try {
				System.out.println("imag: "+profileImage.getOriginalFilename());
				String oldImage = user.getProfileImage();
			if (oldImage != null && !oldImage.isEmpty()) {
				File oldFile = new File("uploads/profiles/" + oldImage);
				if (oldFile.exists()) {
					oldFile.delete();
				}
			}
				String profileImageUrl = saveProfileImage(profileImage);
				user.setProfileImage(profileImageUrl);
			} catch (IOException e) {
				System.out.println("Error al subir la imagen de perfil");
				throw new RuntimeException("Error al subir la imagen de perfil", e);
			}
		}
	
		userRepository.save(user);
	}
	public void editUserData(long token, String name, String surname, String description) {
		User user = activeUsers.get(token);
		if (user == null) {
			throw new RuntimeException("User not found");
		}
	
		user.setName(name);
		user.setSurname(surname);
		user.setDescription(description);
	
		userRepository.save(user);
	}
	public void editProfileImage(long token, MultipartFile profileImage) {
		User user = activeUsers.get(token);
		if (user == null) {
			throw new RuntimeException("User not found");
		}
	
		try {
			String oldImage = user.getProfileImage();
			if (oldImage != null && !oldImage.isEmpty()) {
				File oldFile = new File("uploads/profiles/" + oldImage);
				if (oldFile.exists()) {
					oldFile.delete();
				}
			}
			String profileImageUrl = saveProfileImage(profileImage);
			user.setProfileImage(profileImageUrl);
		} catch (IOException e) {
			System.out.println("Error al subir la imagen de perfil");
			throw new RuntimeException("Error al subir la imagen de perfil", e);
		}
	
		userRepository.save(user);
	}
	
	private String saveProfileImage(MultipartFile profileImage) throws IOException {
		String uploadDir = "uploads/profiles/";
		
		File uploadPath = new File(uploadDir);
		if (!uploadPath.exists()) {
			uploadPath.mkdirs();
		}
	
		String uniqueFileName = UUID.randomUUID().toString() + "_" + profileImage.getOriginalFilename();
	
		Path filePath = Paths.get(uploadDir).resolve(uniqueFileName).toAbsolutePath();
	
		Files.copy(profileImage.getInputStream(), filePath);
	
		return uniqueFileName;
	}

	public Long getUserIdByToken(Long token) {
		User user = activeUsers.get(token);
		return user != null ? user.getId() : null;
	}

	//cambiar
	public void saveUser(User user) {
        userRepository.save(user);
    }

	public User getUserByUsername(String username, Long token) {
		return userRepository.findByUsername(username)
				.orElseThrow(() -> new RuntimeException("User not found"));
	}

	public List<Item> getUserItems(Long userId, Long token) {
		User user = userRepository.findById(String.valueOf(userId)).orElseThrow(() -> new RuntimeException("User not found"));
		List<Item> items = user.getItemsForSale();
		return items;
	}

	public void addRating(RatingDTO ratingDTO) {
		User ratedUser = userRepository.findById(String.valueOf(ratingDTO.getRatedUserId()))
				.orElseThrow(() -> new RuntimeException("Rated user not found"));
		User ratingUser = userRepository.findById(String.valueOf(ratingDTO.getRatingUserId()))
				.orElseThrow(() -> new RuntimeException("Rating user not found"));

		Rating rating = new Rating();
		rating.setRatedUser(ratedUser);
		rating.setRatingUser(ratingUser);
		rating.setScore(ratingDTO.getScore());
		rating.setComment(ratingDTO.getComment());

		ratingRepository.save(rating);
	}

	public List<RatingDTO> getRatingsForUser(long userId) {
		User user = userRepository.findById(String.valueOf(userId))
				.orElseThrow(() -> new RuntimeException("User not found"));

		return user.getRatingsReceived().stream()
				.map(rating -> new RatingDTO(
						rating.getRatedUser().getId(),
						rating.getRatingUser().getId(),
						rating.getScore(),
						rating.getComment()
				))
				.collect(Collectors.toList());
	}

}
