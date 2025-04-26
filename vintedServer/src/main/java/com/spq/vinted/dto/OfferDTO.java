
package com.spq.vinted.dto;

public class OfferDTO {
    private Long offerId;
    private Long chatRoomId;
    private Long senderId;
    private Long itemId;
    private String itemName;
    private String itemImage;
    private float originalPrice;
    private float offerPrice;
    private String status;
    
    public OfferDTO() {}

    public OfferDTO(Long offerId, Long chatRoomId, Long senderId, Long itemId, String itemName, String itemImage, float originalPrice, float offerPrice, String status) {
        this.offerId = offerId;
        this.chatRoomId = chatRoomId;
        this.senderId = senderId;
        this.itemId = itemId;
        this.itemName = itemName;
        this.itemImage = itemImage;
        this.originalPrice = originalPrice;
        this.offerPrice = offerPrice;
        this.status = status;
    }
    
    public Long getOfferId() {
        return offerId;
    }
    
    public void setOfferId(Long offerId) {
        this.offerId = offerId;
    }
    
    public Long getChatRoomId() {
        return chatRoomId;
    }
    
    public void setChatRoomId(Long chatRoomId) {
        this.chatRoomId = chatRoomId;
    }
    
    public Long getSenderId() {
        return senderId;
    }
    
    public void setSenderId(Long senderId) {
        this.senderId = senderId;
    }
    
    public Long getItemId() {
        return itemId;
    }
    
    public void setItemId(Long itemId) {
        this.itemId = itemId;
    }
    
    public String getItemName() {
        return itemName;
    }
    
    public void setItemName(String itemName) {
        this.itemName = itemName;
    }
    
    public String getItemImage() {
        return itemImage;
    }
    
    public void setItemImage(String itemImage) {
        this.itemImage = itemImage;
    }
    
    public float getOriginalPrice() {
        return originalPrice;
    }
    
    public void setOriginalPrice(float originalPrice) {
        this.originalPrice = originalPrice;
    }
    
    public float getOfferPrice() {
        return offerPrice;
    }
    
    public void setOfferPrice(float offerPrice) {
        this.offerPrice = offerPrice;
    }
    
    public String getStatus() {
        return status;
    }
    
    public void setStatus(String status) {
        this.status = status;
    }
}