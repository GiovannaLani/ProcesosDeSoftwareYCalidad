package com.spq.client.data;

public record Rating(
    long id,
    long ratedUserId,
    long ratingUserId,
    int score,
    String comment
) {}