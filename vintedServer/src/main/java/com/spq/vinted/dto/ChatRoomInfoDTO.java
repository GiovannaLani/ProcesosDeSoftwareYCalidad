package com.spq.vinted.dto;

public class ChatRoomInfoDTO {
    private long id;
    private long buyerId;
    private String buyerName;
    private long sellerId;
    private String sellerName;
    private long itemId;
    private String itemName;
    private String itemImage;
    private float itemPrice;
    
    public ChatRoomInfoDTO() {
    }

    public ChatRoomInfoDTO(long id, long buyerId, String buyerName, long sellerId, String sellerName, long itemId, String itemName, String itemImage, float itemPrice) {
        this.id = id;
        this.buyerId = buyerId;
        this.buyerName = buyerName;
        this.sellerId = sellerId;
        this.sellerName = sellerName;
        this.itemId = itemId;
        this.itemName = itemName;
        this.itemImage = itemImage;
        this.itemPrice = itemPrice;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getBuyerId() {
        return buyerId;
    }

    public void setBuyerId(long buyerId) {
        this.buyerId = buyerId;
    }

    public String getBuyerName() {
        return buyerName;
    }

    public void setBuyerName(String buyerName) {
        this.buyerName = buyerName;
    }

    public long getSellerId() {
        return sellerId;
    }

    public void setSellerId(long sellerId) {
        this.sellerId = sellerId;
    }

    public String getSellerName() {
        return sellerName;
    }

    public void setSellerName(String sellerName) {
        this.sellerName = sellerName;
    }

    public long getItemId() {
        return itemId;
    }

    public void setItemId(long itemId) {
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
    public float getItemPrice() {
        return itemPrice;
    }
    public void setItemPrice(float itemPrice) {
        this.itemPrice = itemPrice;
    }
}
