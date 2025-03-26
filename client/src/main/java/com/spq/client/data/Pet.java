package com.spq.client.data;

public record Pet(
    long id,
    String title,
    String description,
    float price,
    String image
    ) {}
