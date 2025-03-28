package com.spq.client.data;

public record User(
    long id,
    String username,
    String name,
    String surname,
    String description,
    String profileImage
) {}
