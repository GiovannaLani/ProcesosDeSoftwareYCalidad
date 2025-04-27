package com.spq.vinted.dto;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class LoginDTOTest {

    @Test
    public void testRecord() {
        String email = "test@example.com";
        String password = "password123";

        LoginDTO loginDTO = new LoginDTO(email, password);

        assertEquals(email, loginDTO.email());
        assertEquals(password, loginDTO.password());
    }

    @Test
    public void testRecordWithNullValues() {
        LoginDTO loginDTO = new LoginDTO(null, null);

        assertNull(loginDTO.email());
        assertNull(loginDTO.password());
    }

    @Test
    public void testRecordEquality() {
        LoginDTO dto1 = new LoginDTO("test@example.com", "password123");
        LoginDTO dto2 = new LoginDTO("test@example.com", "password123");
        LoginDTO dto3 = new LoginDTO("other@example.com", "password456");

        assertEquals(dto1, dto2);
        assertNotEquals(dto1, dto3);
    }

    @Test
    public void testRecordToString() {
        LoginDTO loginDTO = new LoginDTO("test@example.com", "password123");
        String toString = loginDTO.toString();

        assertTrue(toString.contains("test@example.com"));
        assertTrue(toString.contains("password123"));
    }
}