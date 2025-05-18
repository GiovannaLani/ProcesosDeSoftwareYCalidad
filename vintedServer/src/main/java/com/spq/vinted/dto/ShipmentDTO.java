package com.spq.vinted.dto;

import java.time.LocalDateTime;

import com.spq.vinted.model.ShipmentStatus;


public class ShipmentDTO {
    

    private Long id;
    private ShipmentStatus status;
    
    private Long buyerId;
    
    private ItemDTO item;
    private UserDTO buyer;

    private LocalDateTime createdDate;

    public ShipmentDTO() {}
    

    public ShipmentDTO(Long id, ShipmentStatus status, UserDTO buyer, ItemDTO item, LocalDateTime createdDate) {
        this.id = id;
        this.status = status;
        this.buyer = buyer;
        this.item = item;
        this.createdDate = createdDate;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public ShipmentStatus getStatus() { return status; }
    public void setStatus(ShipmentStatus status) { this.status = status; }
    
    public Long getBuyerId() { return buyerId; }
    public void setBuyerId(Long buyerId) { this.buyerId = buyerId; }
    
    public ItemDTO getItem() { return item; }
    public void setItem(ItemDTO item) { this.item = item; }
    
    public UserDTO getBuyer() { return buyer; }
    public void setBuyer(UserDTO buyer) { this.buyer = buyer; }

    public LocalDateTime getCreatedDate() { return createdDate; }
    public void setCreatedDate(LocalDateTime createdDate) { this.createdDate = createdDate; }
}