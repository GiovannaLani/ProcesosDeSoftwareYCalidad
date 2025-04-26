package com.spq.vinted.dto;

public class OfferDTO {
    private Long id;
    private Double price;
    private String status;
    private Long senderId;
    private Long receiverId;
    private ItemDTO item;
    

    
    public OfferDTO() {
    }

    public OfferDTO(Long id, Double price, String status, Long senderId, Long receiverId, ItemDTO item) {
        this.id = id;
        this.price = price;
        this.status = status;
        this.senderId = senderId;
        this.receiverId = receiverId;
        this.item = item;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Long getSenderId() {
        return senderId;
    }

    public void setSenderId(Long senderId) {
        this.senderId = senderId;
    }

    public Long getReceiverId() {
        return receiverId;
    }

    public void setReceiverId(Long receiverId) {
        this.receiverId = receiverId;
    }

    public ItemDTO getItem() {
        return item;
    }

    public void setItem(ItemDTO item) {
        this.item = item;
    }

}