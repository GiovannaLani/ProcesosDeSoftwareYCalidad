package com.spq.vinted.dto;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class ChatRoomInfoDTOTest {

    @Test
    public void testNoArgsConstructor() {
        ChatRoomInfoDTO chatRoomInfoDTO = new ChatRoomInfoDTO();
        
        assertEquals(0L, chatRoomInfoDTO.getId());
        assertEquals(0L, chatRoomInfoDTO.getBuyerId());
        assertNull(chatRoomInfoDTO.getBuyerName());
        assertEquals(0L, chatRoomInfoDTO.getSellerId());
        assertNull(chatRoomInfoDTO.getSellerName());
        assertEquals(0L, chatRoomInfoDTO.getItemId());
        assertNull(chatRoomInfoDTO.getItemName());
        assertNull(chatRoomInfoDTO.getItemImage());
        assertEquals(0.0f, chatRoomInfoDTO.getItemPrice());
    }

    @Test
    public void testAllArgsConstructor() {
        long id = 1L;
        long buyerId = 100L;
        String buyerName = "John Buyer";
        long sellerId = 200L;
        String sellerName = "Jane Seller";
        long itemId = 300L;
        String itemName = "Test Item";
        String itemImage = "test.jpg";
        float itemPrice = 29.99f;

        ChatRoomInfoDTO chatRoomInfoDTO = new ChatRoomInfoDTO(id, buyerId, buyerName, sellerId, 
                sellerName, itemId, itemName, itemImage, itemPrice);

        assertEquals(id, chatRoomInfoDTO.getId());
        assertEquals(buyerId, chatRoomInfoDTO.getBuyerId());
        assertEquals(buyerName, chatRoomInfoDTO.getBuyerName());
        assertEquals(sellerId, chatRoomInfoDTO.getSellerId());
        assertEquals(sellerName, chatRoomInfoDTO.getSellerName());
        assertEquals(itemId, chatRoomInfoDTO.getItemId());
        assertEquals(itemName, chatRoomInfoDTO.getItemName());
        assertEquals(itemImage, chatRoomInfoDTO.getItemImage());
        assertEquals(itemPrice, chatRoomInfoDTO.getItemPrice());
    }

    @Test
    public void testSettersAndGetters() {
        ChatRoomInfoDTO chatRoomInfoDTO = new ChatRoomInfoDTO();

        long id = 2L;
        long buyerId = 150L;
        String buyerName = "Updated Buyer";
        long sellerId = 250L;
        String sellerName = "Updated Seller";
        long itemId = 350L;
        String itemName = "Updated Item";
        String itemImage = "updated.jpg";
        float itemPrice = 39.99f;

        chatRoomInfoDTO.setId(id);
        chatRoomInfoDTO.setBuyerId(buyerId);
        chatRoomInfoDTO.setBuyerName(buyerName);
        chatRoomInfoDTO.setSellerId(sellerId);
        chatRoomInfoDTO.setSellerName(sellerName);
        chatRoomInfoDTO.setItemId(itemId);
        chatRoomInfoDTO.setItemName(itemName);
        chatRoomInfoDTO.setItemImage(itemImage);
        chatRoomInfoDTO.setItemPrice(itemPrice);

        assertEquals(id, chatRoomInfoDTO.getId());
        assertEquals(buyerId, chatRoomInfoDTO.getBuyerId());
        assertEquals(buyerName, chatRoomInfoDTO.getBuyerName());
        assertEquals(sellerId, chatRoomInfoDTO.getSellerId());
        assertEquals(sellerName, chatRoomInfoDTO.getSellerName());
        assertEquals(itemId, chatRoomInfoDTO.getItemId());
        assertEquals(itemName, chatRoomInfoDTO.getItemName());
        assertEquals(itemImage, chatRoomInfoDTO.getItemImage());
        assertEquals(itemPrice, chatRoomInfoDTO.getItemPrice());
    }
}