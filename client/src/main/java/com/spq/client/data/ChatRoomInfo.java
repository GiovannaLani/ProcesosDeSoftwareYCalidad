package com.spq.client.data;

public record ChatRoomInfo (
    Long id,
    Long buyerId,
    String buyerName,
    Long sellerId,
    String sellerName,
    Long itemId,
    String itemName,
    String itemImage,
    float itemPrice
){}
