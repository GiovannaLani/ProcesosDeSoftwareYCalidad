package com.spq.client.data;

public record Home(
    long id,
    String title,
    String description,
    float price,
    String image
) {}
