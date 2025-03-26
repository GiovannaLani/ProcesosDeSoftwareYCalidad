package com.spq.client.data;

public record Purchase(
    long id,
    long itemId,
    String buyerEmail,
    String sellerEmail,
    float price,
    String paymentMethod,
    String status // "PENDING", "COMPLETED", "CANCELLED"
) {}
