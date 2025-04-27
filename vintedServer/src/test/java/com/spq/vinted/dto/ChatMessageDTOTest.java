package com.spq.vinted.dto;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import static org.junit.jupiter.api.Assertions.*;

public class ChatMessageDTOTest {

    @Test
    public void testNoArgsConstructor() {
        ChatMessageDTO chatMessageDTO = new ChatMessageDTO();
        
        assertNull(chatMessageDTO.getContent());
        assertEquals(0L, chatMessageDTO.getChatRoomId());
        assertEquals(0L, chatMessageDTO.getSenderId());
        assertNull(chatMessageDTO.getTimestamp());
        assertNull(chatMessageDTO.getType());
        assertNull(chatMessageDTO.getOffer());
    }

    @Test
    public void testAllArgsConstructor() {
        String content = "Test message";
        long chatRoomId = 1L;
        long senderId = 100L;
        LocalDateTime timestamp = LocalDateTime.now();

        ChatMessageDTO chatMessageDTO = new ChatMessageDTO(content, chatRoomId, senderId, timestamp);

        assertEquals(content, chatMessageDTO.getContent());
        assertEquals(chatRoomId, chatMessageDTO.getChatRoomId());
        assertEquals(senderId, chatMessageDTO.getSenderId());
        assertEquals(timestamp, chatMessageDTO.getTimestamp());
        assertNull(chatMessageDTO.getType());
        assertNull(chatMessageDTO.getOffer());
    }

    @Test
    public void testSettersAndGetters() {
        ChatMessageDTO chatMessageDTO = new ChatMessageDTO();

        String content = "Updated message";
        long chatRoomId = 2L;
        long senderId = 200L;
        LocalDateTime timestamp = LocalDateTime.now();
        String type = "OFFER";
        OfferDTO offer = new OfferDTO();

        chatMessageDTO.setContent(content);
        chatMessageDTO.setChatRoomId(chatRoomId);
        chatMessageDTO.setSenderId(senderId);
        chatMessageDTO.setTimestamp(timestamp);
        chatMessageDTO.setType(type);
        chatMessageDTO.setOffer(offer);

        assertEquals(content, chatMessageDTO.getContent());
        assertEquals(chatRoomId, chatMessageDTO.getChatRoomId());
        assertEquals(senderId, chatMessageDTO.getSenderId());
        assertEquals(timestamp, chatMessageDTO.getTimestamp());
        assertEquals(type, chatMessageDTO.getType());
        assertEquals(offer, chatMessageDTO.getOffer());
    }
}