package com.spq.client.data;

public record Purchase(
    Long id,
    long itemId,
    String buyerUsername,
    String sellerUsername,
    float price,
    String paymentMethod,
    String status // "PENDING", "COMPLETED", "CANCELLED"
) {}
