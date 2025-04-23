package com.spq.client.data;

import java.time.LocalDateTime;

public record ChatMessage (
    String content,
    long chatRoomId,
    long senderId,
    LocalDateTime timestamp
){}
