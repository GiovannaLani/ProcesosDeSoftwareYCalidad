package com.spq.vinted.dto;

public class OfferDTO {
    private double price;
    private String status;
    private long senderId;
    private long receiverId;
    private long chatRoomId;
    private long itemId;
    

    
    public OfferDTO() {
    }

    public OfferDTO(double price, String status, long senderId, long receiverId, long itemId, long chatRoomId) {
        this.price = price;
        this.status = status;
        this.senderId = senderId;
        this.receiverId = receiverId;
        this.itemId = itemId;
        this.chatRoomId = chatRoomId;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public long getSenderId() {
        return senderId;
    }

    public void setSenderId(long senderId) {
        this.senderId = senderId;
    }

    public long getReceiverId() {
        return receiverId;
    }

    public void setReceiverId(long receiverId) {
        this.receiverId = receiverId;
    }

    public long getItemId() {
        return itemId;
    }

    public void setItemId(long itemId) {
        this.itemId = itemId;
    }

    public long getChatRoomId() {
        return chatRoomId;
    }
    public void setChatRoomId(long chatRoomId) {
        this.chatRoomId = chatRoomId;
    }

}