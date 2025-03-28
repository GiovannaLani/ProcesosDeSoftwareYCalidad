package com.spq.vinted.controller;

import com.spq.vinted.dto.EditUserDTO;
import com.spq.vinted.dto.LoginDTO;
import com.spq.vinted.dto.SignupDTO;
import com.spq.vinted.dto.UserDTO;
import com.spq.vinted.model.User;
import com.spq.vinted.service.UserService;

import io.swagger.v3.oas.annotations.tags.Tag;

import java.net.MalformedURLException;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
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
    private UserService authService;
	
	@PostMapping("/signup")
    public ResponseEntity<Void> createUser(@RequestBody SignupDTO user) {
        try {
            authService.createUser(user.email(),  user.password(), user.username(), user.name(), user.surname());
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
			long token = authService.logIn(loginData.email(), loginData.password());
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
			authService.LogOut(token);
			return new ResponseEntity<>(HttpStatus.NO_CONTENT);
		} catch (RuntimeException e) {
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		}
	}

	@DeleteMapping("/delete")
	public ResponseEntity<Void> deleteUser(@RequestParam("token") long token) {
		try {
			authService.deleteUser(token);
			return new ResponseEntity<>(HttpStatus.NO_CONTENT);
		} catch (RuntimeException e) {
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
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
			authService.editUser(token, name, surname, description, profileImage);
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
			authService.editUserData(token, userEditDTO.name(), userEditDTO.surname(), userEditDTO.description());
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
			authService.editProfileImage(token, profileImage);
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
        User user = authService.getUserById(userId);
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
            Long userId = authService.getUserIdByToken(token);
            if (userId == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
            }
            return ResponseEntity.ok(userId);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(null);
        }
    }

}
