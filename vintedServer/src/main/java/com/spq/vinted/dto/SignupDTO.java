package com.spq.vinted.dto;

public record SignupDTO(
    String email,
    String password,
    String username,
    String name,
    String surname
) {} 