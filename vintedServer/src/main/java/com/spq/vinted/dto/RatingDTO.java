package com.spq.vinted.dto;

public class RatingDTO {
    private Long ratedUserId;
    private Long ratingUserId;
    private int score;
    private String comment;

    public RatingDTO() {}

    public RatingDTO(Long ratedUserId, Long ratingUserId, int score, String comment) {
        this.ratedUserId = ratedUserId;
        this.ratingUserId = ratingUserId;
        this.score = score;
        this.comment = comment;
    }

    public Long getRatedUserId() {
        return ratedUserId;
    }

    public void setRatedUserId(Long ratedUserId) {
        this.ratedUserId = ratedUserId;
    }

    public Long getRatingUserId() {
        return ratingUserId;
    }

    public void setRatingUserId(Long ratingUserId) {
        this.ratingUserId = ratingUserId;
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
}