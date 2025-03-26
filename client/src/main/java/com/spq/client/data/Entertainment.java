package com.spq.client.data;

public record Entertainment(
    long id,
    String title,
    String description,
    float price,
    String image
) {}
