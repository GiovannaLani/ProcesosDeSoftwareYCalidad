package com.spq.vinted.dto;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class ChatRoomDTOTest {

    @Test
    public void testNoArgsConstructor() {
        ChatRoomDTO chatRoomDTO = new ChatRoomDTO();
        
        assertEquals(0L, chatRoomDTO.getBuyerId());
        assertEquals(0L, chatRoomDTO.getSellerId());
        assertEquals(0L, chatRoomDTO.getItemId());
    }

    @Test
    public void testAllArgsConstructor() {
        long buyerId = 100L;
        long sellerId = 200L;
        long itemId = 1L;

        ChatRoomDTO chatRoomDTO = new ChatRoomDTO(buyerId, sellerId, itemId);

        assertEquals(buyerId, chatRoomDTO.getBuyerId());
        assertEquals(sellerId, chatRoomDTO.getSellerId());
        assertEquals(itemId, chatRoomDTO.getItemId());
    }

    @Test
    public void testSettersAndGetters() {
        ChatRoomDTO chatRoomDTO = new ChatRoomDTO();

        long buyerId = 300L;
        long sellerId = 400L;
        long itemId = 2L;

        chatRoomDTO.setBuyerId(buyerId);
        chatRoomDTO.setSellerId(sellerId);
        chatRoomDTO.setItemId(itemId);

        assertEquals(buyerId, chatRoomDTO.getBuyerId());
        assertEquals(sellerId, chatRoomDTO.getSellerId());
        assertEquals(itemId, chatRoomDTO.getItemId());
    }
}