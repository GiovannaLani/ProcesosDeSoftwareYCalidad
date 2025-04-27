package com.spq.vinted.dto;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class OfferDTOTest {

    @Test
    public void testNoArgsConstructor() {
        OfferDTO offerDTO = new OfferDTO();
        
        assertEquals(0.0, offerDTO.getPrice());
        assertNull(offerDTO.getStatus());
        assertEquals(0L, offerDTO.getSenderId());
        assertEquals(0L, offerDTO.getReceiverId());
        assertEquals(0L, offerDTO.getItemId());
        assertEquals(0L, offerDTO.getChatRoomId());
    }

    @Test
    public void testAllArgsConstructor() {
        double price = 50.99;
        String status = "PENDING";
        long senderId = 100L;
        long receiverId = 200L;
        long itemId = 300L;
        long chatRoomId = 400L;

        OfferDTO offerDTO = new OfferDTO(price, status, senderId, receiverId, itemId, chatRoomId);

        assertEquals(price, offerDTO.getPrice());
        assertEquals(status, offerDTO.getStatus());
        assertEquals(senderId, offerDTO.getSenderId());
        assertEquals(receiverId, offerDTO.getReceiverId());
        assertEquals(itemId, offerDTO.getItemId());
        assertEquals(chatRoomId, offerDTO.getChatRoomId());
    }

    @Test
    public void testSettersAndGetters() {
        OfferDTO offerDTO = new OfferDTO();

        double price = 75.50;
        String status = "ACCEPTED";
        long senderId = 150L;
        long receiverId = 250L;
        long itemId = 350L;
        long chatRoomId = 450L;

        offerDTO.setPrice(price);
        offerDTO.setStatus(status);
        offerDTO.setSenderId(senderId);
        offerDTO.setReceiverId(receiverId);
        offerDTO.setItemId(itemId);
        offerDTO.setChatRoomId(chatRoomId);

        assertEquals(price, offerDTO.getPrice());
        assertEquals(status, offerDTO.getStatus());
        assertEquals(senderId, offerDTO.getSenderId());
        assertEquals(receiverId, offerDTO.getReceiverId());
        assertEquals(itemId, offerDTO.getItemId());
        assertEquals(chatRoomId, offerDTO.getChatRoomId());
    }
}