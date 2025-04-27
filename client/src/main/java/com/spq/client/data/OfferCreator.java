package com.spq.client.data;

public record OfferCreator (
    double price,
    String status,
    long senderId,
    long receiverId,
    long chatRoomId,
    long itemId
){}
