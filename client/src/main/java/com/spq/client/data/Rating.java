package com.spq.client.data;

public record Rating(
    Long id,
    Long ratedUserId,
    Long ratingUserId,
    int score,
    String comment
) {}