package com.spq.client.data;

import java.util.List;

public record User(
    long id,
    String username,
    String name,
    String surname,
    String description,
    String profileImage,
    List<User> followers, 
    List<User> following
) {}
