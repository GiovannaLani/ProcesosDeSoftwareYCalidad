package com.spq.vinted.dto;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class SignupDTOTest {

    @Test
    public void testRecord() {
        String email = "test@example.com";
        String password = "password123";
        String username = "testuser";
        String name = "John";
        String surname = "Doe";

        SignupDTO signupDTO = new SignupDTO(email, password, username, name, surname);

        assertEquals(email, signupDTO.email());
        assertEquals(password, signupDTO.password());
        assertEquals(username, signupDTO.username());
        assertEquals(name, signupDTO.name());
        assertEquals(surname, signupDTO.surname());
    }

    @Test
    public void testRecordWithNullValues() {
        SignupDTO signupDTO = new SignupDTO(null, null, null, null, null);

        assertNull(signupDTO.email());
        assertNull(signupDTO.password());
        assertNull(signupDTO.username());
        assertNull(signupDTO.name());
        assertNull(signupDTO.surname());
    }

    @Test
    public void testRecordEquality() {
        SignupDTO dto1 = new SignupDTO("test@example.com", "pass123", "user1", "John", "Doe");
        SignupDTO dto2 = new SignupDTO("test@example.com", "pass123", "user1", "John", "Doe");
        SignupDTO dto3 = new SignupDTO("other@example.com", "pass456", "user2", "Jane", "Smith");

        assertEquals(dto1, dto2);
        assertNotEquals(dto1, dto3);
    }

    @Test
    public void testRecordToString() {
        SignupDTO signupDTO = new SignupDTO("test@example.com", "pass123", "user1", "John", "Doe");
        String toString = signupDTO.toString();

        assertTrue(toString.contains("test@example.com"));
        assertTrue(toString.contains("pass123"));
        assertTrue(toString.contains("user1"));
        assertTrue(toString.contains("John"));
        assertTrue(toString.contains("Doe"));
    }
}