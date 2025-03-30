package com.spq.vinted.dto;

public class PurchaseDTO {
    private Long id;
    private long itemId;
    private String buyerEmail;
    private String sellerEmail;
    private float price;
    private String paymentMethod;
    private String status;

    public PurchaseDTO() {}

    public PurchaseDTO(Long id, long itemId, String buyerEmail, String sellerEmail, float price, String paymentMethod, String status) {
        this.id = id;
        this.itemId = itemId;
        this.buyerEmail = buyerEmail;
        this.sellerEmail = sellerEmail;
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

    public String getBuyerEmail() {
        return buyerEmail;
    }

    public void setBuyerEmail(String buyerEmail) {
        this.buyerEmail = buyerEmail;
    }

    public String getSellerEmail() {
        return sellerEmail;
    }

    public void setSellerEmail(String sellerEmail) {
        this.sellerEmail = sellerEmail;
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