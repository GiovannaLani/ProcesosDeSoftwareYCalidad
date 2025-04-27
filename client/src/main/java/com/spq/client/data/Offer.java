package com.spq.client.data;

public record Offer (
    Long id,
    Double price,
    Double originalPrice,
    String status,
    Long senderId,
    Long receiverId,
    Long itemId,
    Long chatRoomId
){}
