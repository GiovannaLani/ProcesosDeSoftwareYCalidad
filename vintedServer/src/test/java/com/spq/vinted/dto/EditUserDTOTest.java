package com.spq.vinted.dto;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class EditUserDTOTest {

    @Test
    public void testRecord() {
        String name = "John";
        String surname = "Doe";
        String description = "Test user description";

        EditUserDTO editUserDTO = new EditUserDTO(name, surname, description);

        assertEquals(name, editUserDTO.name());
        assertEquals(surname, editUserDTO.surname());
        assertEquals(description, editUserDTO.description());
    }

    @Test
    public void testRecordWithNullValues() {
        EditUserDTO editUserDTO = new EditUserDTO(null, null, null);

        assertNull(editUserDTO.name());
        assertNull(editUserDTO.surname());
        assertNull(editUserDTO.description());
    }

    @Test
    public void testRecordEquality() {
        EditUserDTO dto1 = new EditUserDTO("John", "Doe", "Test");
        EditUserDTO dto2 = new EditUserDTO("John", "Doe", "Test");
        EditUserDTO dto3 = new EditUserDTO("Jane", "Smith", "Another test");

        assertEquals(dto1, dto2);
        assertNotEquals(dto1, dto3);
    }

    @Test
    public void testRecordToString() {
        EditUserDTO editUserDTO = new EditUserDTO("John", "Doe", "Test");
        String toString = editUserDTO.toString();

        assertTrue(toString.contains("John"));
        assertTrue(toString.contains("Doe"));
        assertTrue(toString.contains("Test"));
    }
}