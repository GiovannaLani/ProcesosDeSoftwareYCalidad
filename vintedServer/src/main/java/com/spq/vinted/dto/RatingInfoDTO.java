package com.spq.vinted.dto;

public class RatingInfoDTO {
    private int score;
    private String comment;
    private Long ratingUserId;
    private String ratingUserProfileImage;
    private String ratingUsername;

    public RatingInfoDTO() {}

    public RatingInfoDTO(int score, String comment, Long ratingUserId, String ratingUserProfileImage, String ratingUsername) {
        this.score = score;
        this.comment = comment;
        this.ratingUserId = ratingUserId;
        this.ratingUserProfileImage = ratingUserProfileImage;
        this.ratingUsername = ratingUsername;
    }
    public int getScore() {
        return score;
    }
    public void setScore(int score) {
        this.score = score;
    }
    public String getComment() {
        return comment;
    }
    public void setComment(String comment) {
        this.comment = comment;
    }
    public Long getRatingUserId() {
        return ratingUserId;
    }
    public void setRatingUserId(Long ratingUserId) {
        this.ratingUserId = ratingUserId;
    }
    public String getRatingUserProfileImage() {
        return ratingUserProfileImage;
    }
    public void setRatingUserProfileImage(String ratingUserProfileImage) {
        this.ratingUserProfileImage = ratingUserProfileImage;
    }
    public String getRatingUsername() {
        return ratingUsername;
    }
    public void setRatingUsername(String ratingUsername) {
        this.ratingUsername = ratingUsername;
    }
}
