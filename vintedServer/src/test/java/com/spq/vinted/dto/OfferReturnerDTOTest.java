package com.spq.vinted.dto;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class OfferReturnerDTOTest {

    @Test
    public void testNoArgsConstructor() {
        OfferReturnerDTO offerReturnerDTO = new OfferReturnerDTO();
        
        assertEquals(0L, offerReturnerDTO.getId());
        assertEquals(0.0, offerReturnerDTO.getPrice());
        assertNull(offerReturnerDTO.getStatus());
        assertEquals(0L, offerReturnerDTO.getSenderId());
        assertEquals(0L, offerReturnerDTO.getReceiverId());
        assertEquals(0L, offerReturnerDTO.getItemId());
        assertEquals(0L, offerReturnerDTO.getChatRoomId());
    }

    @Test
    public void testAllArgsConstructor() {
        long id = 1L;
        double price = 50.99;
        double originalPrice = 70;
        String status = "PENDING";
        long senderId = 100L;
        long receiverId = 200L;
        long itemId = 300L;
        long chatRoomId = 400L;

        OfferReturnerDTO offerReturnerDTO = new OfferReturnerDTO(id, price,originalPrice, status, senderId, receiverId, itemId, chatRoomId);

        assertEquals(1L, offerReturnerDTO.getId());
        assertEquals(price, offerReturnerDTO.getPrice());
        assertEquals(originalPrice, offerReturnerDTO.getOriginalPrice());
        assertEquals(status, offerReturnerDTO.getStatus());
        assertEquals(senderId, offerReturnerDTO.getSenderId());
        assertEquals(receiverId, offerReturnerDTO.getReceiverId());
        assertEquals(itemId, offerReturnerDTO.getItemId());
        assertEquals(chatRoomId, offerReturnerDTO.getChatRoomId());
    }

    @Test
    public void testSettersAndGetters() {
        OfferReturnerDTO offerReturnerDTO = new OfferReturnerDTO();

        double price = 75.50;
        double originalPrice = 80;
        String status = "ACCEPTED";
        long senderId = 150L;
        long receiverId = 250L;
        long itemId = 350L;
        long chatRoomId = 450L;

        // El DTO no tiene setter para ID
        offerReturnerDTO.setPrice(price);
        offerReturnerDTO.setOriginalPrice(originalPrice);
        offerReturnerDTO.setStatus(status);
        offerReturnerDTO.setSenderId(senderId);
        offerReturnerDTO.setReceiverId(receiverId);
        offerReturnerDTO.setItemId(itemId);
        offerReturnerDTO.setChatRoomId(chatRoomId);

        assertEquals(price, offerReturnerDTO.getPrice());
        assertEquals(originalPrice, offerReturnerDTO.getOriginalPrice());
        assertEquals(status, offerReturnerDTO.getStatus());
        assertEquals(senderId, offerReturnerDTO.getSenderId());
        assertEquals(receiverId, offerReturnerDTO.getReceiverId());
        assertEquals(itemId, offerReturnerDTO.getItemId());
        assertEquals(chatRoomId, offerReturnerDTO.getChatRoomId());
    }
}