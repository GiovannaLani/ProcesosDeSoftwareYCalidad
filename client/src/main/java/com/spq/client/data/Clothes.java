package com.spq.client.data;

public record Clothes (
    long id,
    String title,
    String description,
    float price,
    String image,
    Category category,
    String size,
    String brand
    ) {}