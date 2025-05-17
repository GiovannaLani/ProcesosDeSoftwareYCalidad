package com.spq.vinted.controller;

import com.spq.vinted.dto.EditUserDTO;
import com.spq.vinted.dto.ItemDTO;
import com.spq.vinted.dto.LoginDTO;
import com.spq.vinted.dto.PaginatedResponseDTO;
import com.spq.vinted.dto.RatingDTO;
import com.spq.vinted.dto.RatingInfoDTO;
import com.spq.vinted.dto.SignupDTO;
import com.spq.vinted.dto.UserDTO;
import com.spq.vinted.model.Item;
import com.spq.vinted.model.Rating;
import com.spq.vinted.model.User;
import com.spq.vinted.service.UserService;

import io.swagger.v3.oas.annotations.tags.Tag;

import java.net.MalformedURLException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;


@RestController
@RequestMapping("/users")
@Tag(name = "Authorization Controller", description = "Login and logout operations")
public class UserController {
    @Autowired
    private UserService userService;
	
	@PostMapping("/signup")
    public ResponseEntity<Void> createUser(@RequestBody SignupDTO user) {
        try {
            userService.createUser(user.email(),  user.password(), user.username(), user.name(), user.surname());
            return new ResponseEntity<>(HttpStatus.CREATED);
        } catch (RuntimeException e) {
            if (e.getMessage().equals("User already exists")) {
                return new ResponseEntity<>(HttpStatus.CONFLICT);
            } else if (e.getMessage().equals("Username already exists")) {
                return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
            }else if (e.getMessage().equals("Invalid credentials")) {
                return new ResponseEntity<>(HttpStatus.FORBIDDEN);
            } else {
                return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
            }
        }
    }
    
	
	@PostMapping("/login")
	public ResponseEntity<Long> logIn(@RequestBody LoginDTO loginData) {
		try {
			long token = userService.logIn(loginData.email(), loginData.password());
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
	public ResponseEntity<Void> logOut(@RequestParam("token") long token) {
		try {
			userService.LogOut(token);
			return new ResponseEntity<>(HttpStatus.NO_CONTENT);
		} catch (RuntimeException e) {
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		}
	}

	@DeleteMapping("/delete")
	public ResponseEntity<Void> deleteUser(@RequestParam("token") long token) {
		try {
			userService.deleteUser(token);
			return new ResponseEntity<>(HttpStatus.NO_CONTENT);
		} catch (RuntimeException e) {
			if (e.getMessage().equals("User not found")) {
				return new ResponseEntity<>(HttpStatus.NOT_FOUND);
			}
			return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@PutMapping(value = "/edit", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	public ResponseEntity<Void> editUser(
			@RequestParam("token") long token,
			@RequestParam("name") String name,
			@RequestParam("surname") String surname,
			@RequestParam(value = "description", required = false) String description,
			@RequestPart(value = "profileImage", required = false) MultipartFile profileImage) {
		try {
			// Validar parámetros requeridos
			if (name == null || name.trim().isEmpty() || surname == null || surname.trim().isEmpty()) {
				return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
			}
			
			userService.editUser(token, name, surname, description, profileImage);
			return new ResponseEntity<>(HttpStatus.NO_CONTENT);
		} catch (RuntimeException e) {
			if ("User not found".equals(e.getMessage())) {
				return new ResponseEntity<>(HttpStatus.NOT_FOUND);
			}
			e.printStackTrace();
			return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@PutMapping("/editUserData")
	public ResponseEntity<Void> updateUserData(
			@RequestParam("token") long token,
			@RequestBody EditUserDTO userEditDTO) {

		try {
			userService.editUserData(token, userEditDTO.name(), userEditDTO.surname(), userEditDTO.description());
			return new ResponseEntity<>(HttpStatus.NO_CONTENT);
		} catch (RuntimeException e) {
			if ("User not found".equals(e.getMessage())) {
				return new ResponseEntity<>(HttpStatus.NOT_FOUND);
			}
			e.printStackTrace();
			return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	@PutMapping(value = "/editProfileImage", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	public ResponseEntity<Void> updateProfileImage(
			@RequestParam("token") long token,
			@RequestPart(value = "profileImage") MultipartFile profileImage) {

		try {
			userService.editProfileImage(token, profileImage);
			return new ResponseEntity<>(HttpStatus.NO_CONTENT);
		} catch (RuntimeException e) {
			if ("User not found".equals(e.getMessage())) {
				return new ResponseEntity<>(HttpStatus.NOT_FOUND);
			}
			e.printStackTrace();
			return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}


	@GetMapping("/profile/{userId}")
    public ResponseEntity<UserDTO> getUserProfile(@RequestParam("token") Long token, @PathVariable("userId") long userId) {
        User user = userService.getUserById(userId);
        if (user == null) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
		UserDTO userDTO = user.toDTO();

        return ResponseEntity.ok(userDTO);
    }

	@GetMapping("/profile/imagen/{nombreImagen}")
	@ResponseBody
	public ResponseEntity<Resource> showImagen(@PathVariable String nombreImagen) throws MalformedURLException {
		Path rutaArchivo = Paths.get("uploads/profiles").resolve(nombreImagen).toAbsolutePath();
		Resource recurso = new UrlResource(rutaArchivo.toUri());

		if (recurso.exists() && recurso.isReadable()) {
			return ResponseEntity.ok()
				.contentType(MediaType.IMAGE_JPEG)
				.body(recurso);
		} else {
			return ResponseEntity.notFound().build();
		}
	}
	
	@GetMapping("/userId")
    public ResponseEntity<Long> getUserIdFromToken(@RequestParam("token") Long token) {
        try {
            Long userId = userService.getUserIdByToken(token);
            if (userId == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
            }
            return ResponseEntity.ok(userId);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(null);
        }
    }

	@PostMapping("/follow")
	public ResponseEntity<String> followUser(@RequestParam("token") Long token, @RequestParam Long targetUserId) {
		try {
			Long userId = userService.getUserIdByToken(token);
			if (userId == null) {
				return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Token inválido");
			}
			userService.followUser(userId, targetUserId);
			return new ResponseEntity<>("Usuario seguido con éxito", HttpStatus.OK);
		} catch (RuntimeException e) {
			e.printStackTrace();
			return new ResponseEntity<>("Error al seguir al usuario", HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@PostMapping("/unfollow")
	public ResponseEntity<String> unfollowUser(@RequestParam("token") Long token, @RequestParam Long targetUserId) {
		try {
			Long userId = userService.getUserIdByToken(token);
			if (userId == null) {
				return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Token inválido");
			}
			userService.unfollowUser(userId, targetUserId);
			return new ResponseEntity<>("Usuario dejado de seguir con éxito", HttpStatus.OK);
		} catch (RuntimeException e) {
			e.printStackTrace();
			return new ResponseEntity<>("Error al dejar de seguir al usuario", HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@GetMapping("/followers")
	public ResponseEntity<List<UserDTO>> getFollowers(@RequestParam Long targetUserId) {
		try {
			List<User> followers = userService.getFollowers(targetUserId);

			List<UserDTO> followersDTO = followers.stream()
					.map(user -> user.toDTO())
					.collect(Collectors.toList());
			return ResponseEntity.ok(followersDTO);
		} catch (RuntimeException e) {
			e.printStackTrace();
			return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@GetMapping("/following")
	public ResponseEntity<List<UserDTO>> getFollowing(@RequestParam Long targetUserId) {
		try {
			List<User> following = userService.getFollowing(targetUserId);

			List<UserDTO> followingDTO = following.stream()
					.map(user -> user.toDTO())
					.collect(Collectors.toList());

			return ResponseEntity.ok(followingDTO);
		} catch (RuntimeException e) {
			e.printStackTrace();
			return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@GetMapping("/search")
	public ResponseEntity<UserDTO> searchUser(
			@RequestParam("username") String username,
			@RequestParam("token") Long token) {
		try {
			User user = userService.getUserByUsername(username, token);
			if (user != null) {
				return ResponseEntity.ok(user.toDTO());
			} else {
				return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
			}
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
		}
	}

    @GetMapping("/{userId}/items")
    public ResponseEntity<List<Item>> getUserItems(
            @PathVariable Long userId,
            @RequestParam("token") Long token) {
        try {
            List<Item> items = userService.getUserItems(userId, token);
            return ResponseEntity.ok(items);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

	@PostMapping("/rate")
	public ResponseEntity<Void> rateUser(@RequestBody RatingDTO rating) {
		try {
			if (rating.getRatedUserId() == null || rating.getRatingUserId() == null) {
				return ResponseEntity.badRequest().build();
			}
			
			User ratedUser = userService.getUserById(rating.getRatedUserId());
			User ratingUser = userService.getUserById(rating.getRatingUserId());
			
			if (ratedUser == null || ratingUser == null) {
				return ResponseEntity.badRequest().build();
			}
			

			userService.addRating(rating);
			return ResponseEntity.ok().build();
		} catch (Exception e) {
			return ResponseEntity.badRequest().build();
		}
	}

	@GetMapping("/{userId}/ratings")
	public ResponseEntity<List<RatingInfoDTO>> getUserRatings(@PathVariable long userId) {
		try {
			List<RatingInfoDTO> ratings = userService.getRatingsForUser(userId);
			return ResponseEntity.ok(ratings);
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body(null);
		}
	}
	
	@GetMapping("/searchUsers")
    public ResponseEntity<PaginatedResponseDTO<UserDTO>> searchUsers(
        @RequestParam(value = "token", required = false) Long token,
        @RequestParam(value = "search_text",required = false) String query,
        @RequestParam(value = "page", defaultValue = "0") int page) {
        try {
            Page<User> users = userService.searchUsers(token, query, page);
            List<UserDTO> usersDTOs = users.getContent().stream()
				.map(user -> user.toDTO())
				.collect(Collectors.toList());
            PaginatedResponseDTO<UserDTO> result = new PaginatedResponseDTO<>(
                usersDTOs,
                users.getNumber(),
                users.getTotalPages()
            );
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
}