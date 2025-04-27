package com.spq.client.data;

import java.time.LocalDateTime;

public record Offer (
    Long id,
    Double price,
    String status,
    Long senderId,
    Long receiverId,
    Long itemId,
    Long chatRoomId
){}
