package com.spq.client.data;

public record RatingInfo(
    int score,
    String comment,
    Long ratingUserId,
    String ratingUserProfileImage,
    String ratingUsername
) {}
