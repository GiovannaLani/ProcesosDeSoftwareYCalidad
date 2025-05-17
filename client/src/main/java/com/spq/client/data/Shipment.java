package com.spq.client.data;


public record Shipment(
    Long id,
    Long buyerId,
    ShipmentStatus status,
    Item item
) {
}