package com.spq.vinted.dto;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class UserDTOTest {

    @Test
    public void testRecord() {
        long id = 1L;
        String username = "testuser";
        String name = "John";
        String surname = "Doe";
        String description = "Test user description";
        String profileImage = "profile.jpg";

        UserDTO userDTO = new UserDTO(id, username, name, surname, description, profileImage);

        assertEquals(id, userDTO.id());
        assertEquals(username, userDTO.username());
        assertEquals(name, userDTO.name());
        assertEquals(surname, userDTO.surname());
        assertEquals(description, userDTO.description());
        assertEquals(profileImage, userDTO.profileImage());
    }

    @Test
    public void testRecordWithNullValues() {
        UserDTO userDTO = new UserDTO(0L, null, null, null, null, null);

        assertEquals(0L, userDTO.id());
        assertNull(userDTO.username());
        assertNull(userDTO.name());
        assertNull(userDTO.surname());
        assertNull(userDTO.description());
        assertNull(userDTO.profileImage());
    }

    @Test
    public void testRecordEquality() {
        UserDTO dto1 = new UserDTO(1L, "user1", "John", "Doe", "desc1", "pic1.jpg");
        UserDTO dto2 = new UserDTO(1L, "user1", "John", "Doe", "desc1", "pic1.jpg");
        UserDTO dto3 = new UserDTO(2L, "user2", "Jane", "Smith", "desc2", "pic2.jpg");

        assertEquals(dto1, dto2);
        assertNotEquals(dto1, dto3);
    }

    @Test
    public void testRecordToString() {
        UserDTO userDTO = new UserDTO(1L, "user1", "John", "Doe", "desc1", "pic1.jpg");
        String toString = userDTO.toString();

        assertTrue(toString.contains("1"));
        assertTrue(toString.contains("user1"));
        assertTrue(toString.contains("John"));
        assertTrue(toString.contains("Doe"));
        assertTrue(toString.contains("desc1"));
        assertTrue(toString.contains("pic1.jpg"));
    }
}