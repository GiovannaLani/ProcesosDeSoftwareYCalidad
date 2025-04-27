package com.spq.vinted.dto;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class PurchaseDTOTest {

    @Test
    public void testNoArgsConstructor() {
        PurchaseDTO purchaseDTO = new PurchaseDTO();
        
        assertNull(purchaseDTO.getId());
        assertEquals(0L, purchaseDTO.getItemId());
        assertNull(purchaseDTO.getBuyerUsername());
        assertNull(purchaseDTO.getSellerUsername());
        assertEquals(0.0f, purchaseDTO.getPrice());
        assertNull(purchaseDTO.getPaymentMethod());
        assertNull(purchaseDTO.getStatus());
    }

    @Test
    public void testAllArgsConstructor() {
        Long id = 1L;
        long itemId = 100L;
        String buyerUsername = "buyer123";
        String sellerUsername = "seller456";
        float price = 49.99f;
        String paymentMethod = "CREDIT_CARD";
        String status = "COMPLETED";

        PurchaseDTO purchaseDTO = new PurchaseDTO(id, itemId, buyerUsername, sellerUsername, price, paymentMethod, status);

        assertEquals(id, purchaseDTO.getId());
        assertEquals(itemId, purchaseDTO.getItemId());
        assertEquals(buyerUsername, purchaseDTO.getBuyerUsername());
        assertEquals(sellerUsername, purchaseDTO.getSellerUsername());
        assertEquals(price, purchaseDTO.getPrice());
        assertEquals(paymentMethod, purchaseDTO.getPaymentMethod());
        assertEquals(status, purchaseDTO.getStatus());
    }

    @Test
    public void testSettersAndGetters() {
        PurchaseDTO purchaseDTO = new PurchaseDTO();

        Long id = 2L;
        long itemId = 200L;
        String buyerUsername = "buyerUpdated";
        String sellerUsername = "sellerUpdated";
        float price = 99.99f;
        String paymentMethod = "PAYPAL";
        String status = "PENDING";

        purchaseDTO.setId(id);
        purchaseDTO.setItemId(itemId);
        purchaseDTO.setBuyerUsername(buyerUsername);
        purchaseDTO.setSellerUsername(sellerUsername);
        purchaseDTO.setPrice(price);
        purchaseDTO.setPaymentMethod(paymentMethod);
        purchaseDTO.setStatus(status);

        assertEquals(id, purchaseDTO.getId());
        assertEquals(itemId, purchaseDTO.getItemId());
        assertEquals(buyerUsername, purchaseDTO.getBuyerUsername());
        assertEquals(sellerUsername, purchaseDTO.getSellerUsername());
        assertEquals(price, purchaseDTO.getPrice());
        assertEquals(paymentMethod, purchaseDTO.getPaymentMethod());
        assertEquals(status, purchaseDTO.getStatus());
    }
}