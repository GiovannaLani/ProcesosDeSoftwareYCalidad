package com.spq.vinted.dto;

import java.util.List;

import com.spq.vinted.model.User;

public record UserDTO(
    long id,
    String username,
    String name,
    String surname,
    String description,
    String profileImage,
    List<User> followers, 
    List<User> following
){}
