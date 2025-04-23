package com.spq.vinted.dto;

public class ChatRoomDTO {
    private long buyerId;
    private long sellerId;
    private long itemId;

    public ChatRoomDTO() {
    }

    public ChatRoomDTO(long buyerId, long sellerId, long itemId) {
        this.buyerId = buyerId;
        this.sellerId = sellerId;
        this.itemId = itemId;
    }

    public long getBuyerId() {
        return buyerId;
    }

    public void setBuyerId(long buyerId) {
        this.buyerId = buyerId;
    }

    public long getSellerId() {
        return sellerId;
    }

    public void setSellerId(long sellerId) {
        this.sellerId = sellerId;
    }

    public long getItemId() {
        return itemId;
    }

    public void setItemId(long itemId) {
        this.itemId = itemId;
    }
    
}
