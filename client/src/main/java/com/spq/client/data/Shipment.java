package com.spq.client.data;

import java.time.LocalDateTime;

public record Shipment(
    Long id,
    Long buyerId,
    ShipmentStatus status,
    Item item,
    LocalDateTime createdDate
) {
}