package com.spq.client.data;

public record Electronics (
    long id,
    String title,
    String description,
    float price,
    String image
    ) {}