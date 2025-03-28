package com.spq.client.data;

public record Pet(
    String title,
    String description,
    float price,
    Species species
    ) {}
