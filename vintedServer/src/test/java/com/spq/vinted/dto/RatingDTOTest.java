package com.spq.vinted.dto;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class RatingDTOTest {

    @Test
    public void testNoArgsConstructor() {
        RatingDTO ratingDTO = new RatingDTO();
        
        assertNull(ratingDTO.getRatedUserId());
        assertNull(ratingDTO.getRatingUserId());
        assertEquals(0, ratingDTO.getScore());
        assertNull(ratingDTO.getComment());
    }

    @Test
    public void testAllArgsConstructor() {
        Long ratedUserId = 100L;
        Long ratingUserId = 200L;
        int score = 5;
        String comment = "Great seller!";

        RatingDTO ratingDTO = new RatingDTO(ratedUserId, ratingUserId, score, comment);

        assertEquals(ratedUserId, ratingDTO.getRatedUserId());
        assertEquals(ratingUserId, ratingDTO.getRatingUserId());
        assertEquals(score, ratingDTO.getScore());
        assertEquals(comment, ratingDTO.getComment());
    }

    @Test
    public void testSettersAndGetters() {
        RatingDTO ratingDTO = new RatingDTO();

        Long ratedUserId = 150L;
        Long ratingUserId = 250L;
        int score = 4;
        String comment = "Good experience";

        ratingDTO.setRatedUserId(ratedUserId);
        ratingDTO.setRatingUserId(ratingUserId);
        ratingDTO.setScore(score);
        ratingDTO.setComment(comment);

        assertEquals(ratedUserId, ratingDTO.getRatedUserId());
        assertEquals(ratingUserId, ratingDTO.getRatingUserId());
        assertEquals(score, ratingDTO.getScore());
        assertEquals(comment, ratingDTO.getComment());
    }
}