package com.spq.client.data;


public record Shipment(
    Long id,
    Long buyerId,
    Long sellerId,
    ShipmentStatus status,
    Item item
) {
}