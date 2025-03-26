package com.spq.client.data;

public record Item (
    long id,
    String title,
    String description,
    float price,
    String image
    ) {}
