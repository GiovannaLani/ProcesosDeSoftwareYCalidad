package com.spq.vinted.dto;

import com.spq.vinted.model.ShipmentStatus;
import java.util.List;

public class ShipmentDTO {
    

    private Long id;
    private ShipmentStatus status;
    private Long sellerId;
    private Long buyerId;
    
    private ItemDTO item;
    private UserDTO buyer;

    public ShipmentDTO() {}
    

    public ShipmentDTO(Long id, ShipmentStatus status, UserDTO buyer, ItemDTO item, Long buyerId, Long sellerId) {
        this.id = id;
        this.status = status;
        this.buyer = buyer;
        this.item = item;
        this.buyerId = buyerId;
        this.sellerId = sellerId;
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

    public Long getSellerId() { return sellerId; }
    public void setSellerId(Long sellerId) { this.sellerId = sellerId; }
}