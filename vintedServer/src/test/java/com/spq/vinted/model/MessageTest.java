package com.spq.vinted.model;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDateTime;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class MessageTest {

    private Message message;
    private ChatRoom chatRoom;
    private User sender;

    @BeforeEach
    void setUp() {
        chatRoom = new ChatRoom();
        sender = new User("sender@example.com", "password123", "senderUser", "Sender", "User");

        message = new Message(chatRoom, sender, "Hola, ¿cómo estás?");
    }

    @Test
    void testConstructor() {
        assertEquals(chatRoom, message.getChatRoom(), "La sala de chat debería coincidir");
        assertEquals(sender, message.getSender(), "El remitente debería coincidir");
        assertEquals("Hola, ¿cómo estás?", message.getContent(), "El contenido debería coincidir");
        assertNotNull(message.getTimestamp(), "La marca de tiempo no debería ser nula");
    }

    @Test
    void testSettersAndGetters() {
        ChatRoom newChatRoom = new ChatRoom();
        User newSender = new User("newSender@example.com", "password456", "newSenderUser", "NewSender", "User");
        String newContent = "Este es un nuevo mensaje";
        LocalDateTime newTimestamp = LocalDateTime.of(2025, 4, 27, 12, 0);

        message.setChatRoom(newChatRoom);
        message.setSender(newSender);
        message.setContent(newContent);
        message.setTimestamp(newTimestamp);

        assertEquals(newChatRoom, message.getChatRoom(), "La nueva sala de chat debería coincidir");
        assertEquals(newSender, message.getSender(), "El nuevo remitente debería coincidir");
        assertEquals(newContent, message.getContent(), "El nuevo contenido debería coincidir");
        assertEquals(newTimestamp, message.getTimestamp(), "La nueva marca de tiempo debería coincidir");
    }

    @Test
    void testSetContent() {
        String newContent = "Este es un mensaje actualizado";
        message.setContent(newContent);

        assertEquals(newContent, message.getContent(), "El contenido debería actualizarse correctamente");
    }

    @Test
    void testSetTimestamp() {
        LocalDateTime newTimestamp = LocalDateTime.of(2025, 4, 27, 15, 30);
        message.setTimestamp(newTimestamp);

        assertEquals(newTimestamp, message.getTimestamp(), "La marca de tiempo debería actualizarse correctamente");
    }
}