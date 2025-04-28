package com.spq.vinted.model;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class RatingTest {

    private Rating rating;

    @BeforeEach
    void setUp() {
        rating = new Rating();
    }

    @Test
    void testSettersAndGetters() {
        rating.setId(1L);
        rating.setRatedUserId(2L);
        rating.setRatingUserId(3L);
        rating.setScore(5);
        rating.setComment("Excelente servicio");

        assertEquals(1L, rating.getId(), "El ID debería ser 1");
        assertEquals(2L, rating.getRatedUserId(), "El ID del usuario calificado debería ser 2");
        assertEquals(3L, rating.getRatingUserId(), "El ID del usuario que califica debería ser 3");
        assertEquals(5, rating.getScore(), "La puntuación debería ser 5");
        assertEquals("Excelente servicio", rating.getComment(), "El comentario debería coincidir");
    }

    @Test
    void testDefaultValues() {
        assertNull(rating.getId(), "El ID debería ser nulo por defecto");
        assertNull(rating.getRatedUserId(), "El ID del usuario calificado debería ser nulo por defecto");
        assertNull(rating.getRatingUserId(), "El ID del usuario que califica debería ser nulo por defecto");
        assertEquals(0, rating.getScore(), "La puntuación debería ser 0 por defecto");
        assertNull(rating.getComment(), "El comentario debería ser nulo por defecto");
    }

    @Test
    void testSetInvalidScore() {
        rating.setScore(-1);

        assertEquals(-1, rating.getScore(), "La puntuación debería permitir valores negativos si no hay validación");
    }

    @Test
    void testSetEmptyComment() {
        rating.setComment("");

        assertEquals("", rating.getComment(), "El comentario debería permitir valores vacíos");
    }

    @Test
    void testSetLongComment() {
        String longComment = "a".repeat(500);
        rating.setComment(longComment);

        assertEquals(longComment, rating.getComment(), "El comentario debería permitir hasta 500 caracteres");
    }
}