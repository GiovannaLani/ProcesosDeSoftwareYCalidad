package com.spq.vinted.dto;

public class PurchaseDTO {
    private Long id;
    private long itemId;
    private String buyerUsername;
    private String sellerUsername;
    private float price;
    private String paymentMethod;
    private String status;

    public PurchaseDTO() {}

    public PurchaseDTO(Long id, long itemId, String buyerEmail, String sellerEmail, float price, String paymentMethod, String status) {
        this.id = id;
        this.itemId = itemId;
        this.buyerUsername = buyerEmail;
        this.sellerUsername = sellerEmail;
        this.price = price;
        this.paymentMethod = paymentMethod;
        this.status = status;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public long getItemId() {
        return itemId;
    }

    public void setItemId(long itemId) {
        this.itemId = itemId;
    }

    public String getBuyerUsername() {
        return buyerUsername;
    }

    public void setBuyerUsername(String buyerEmail) {
        this.buyerUsername = buyerEmail;
    }

    public String getSellerUsername() {
        return sellerUsername;
    }

    public void setSellerUsername(String sellerEmail) {
        this.sellerUsername = sellerEmail;
    }

    public float getPrice() {
        return price;
    }

    public void setPrice(float price) {
        this.price = price;
    }

    public String getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}