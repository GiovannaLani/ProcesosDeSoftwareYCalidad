package com.spq.vinted.dto;

public record UserDTO(
    long id,
    String username,
    String name,
    String surname,
    String description,
    String profileImage
){}
