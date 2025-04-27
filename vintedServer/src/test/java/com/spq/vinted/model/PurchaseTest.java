package com.spq.vinted.model;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class PurchaseTest {

    private Purchase purchase;
    private User buyer;
    private User seller;

    @BeforeEach
    void setUp() {
        buyer = new User("buyer@example.com", "password123", "buyerUser", "Buyer", "User");
        seller = new User("seller@example.com", "password456", "sellerUser", "Seller", "User");

        purchase = new Purchase(1L, buyer, seller, 100.0f, "CREDIT_CARD", "PENDING");
    }

    @Test
    void testConstructor() {
        assertEquals(1L, purchase.getItemId(), "El ID del ítem debería ser 1");
        assertEquals(buyer, purchase.getBuyer(), "El comprador debería coincidir");
        assertEquals(seller, purchase.getSeller(), "El vendedor debería coincidir");
        assertEquals(100.0f, purchase.getPrice(), "El precio debería ser 100.0");
        assertEquals("CREDIT_CARD", purchase.getPaymentMethod(), "El método de pago debería ser CREDIT_CARD");
        assertEquals("PENDING", purchase.getStatus(), "El estado debería ser PENDING");
    }

    @Test
    void testSetStatus() {
        purchase.setStatus("COMPLETED");
        assertEquals("COMPLETED", purchase.getStatus(), "El estado debería ser COMPLETED");

        purchase.setStatus("CANCELLED");
        assertEquals("CANCELLED", purchase.getStatus(), "El estado debería ser CANCELLED");
    }

    @Test
    void testGetters() {
        assertNull(purchase.getId(), "El ID debería ser nulo inicialmente (no asignado por la base de datos)");
        assertEquals(1L, purchase.getItemId(), "El ID del ítem debería ser 1");
        assertEquals(buyer, purchase.getBuyer(), "El comprador debería coincidir");
        assertEquals(seller, purchase.getSeller(), "El vendedor debería coincidir");
        assertEquals(100.0f, purchase.getPrice(), "El precio debería ser 100.0");
        assertEquals("CREDIT_CARD", purchase.getPaymentMethod(), "El método de pago debería ser CREDIT_CARD");
        assertEquals("PENDING", purchase.getStatus(), "El estado debería ser PENDING");
    }

    @Test
    void testSetters() {
        User newBuyer = new User("newbuyer@example.com", "password789", "newBuyerUser", "NewBuyer", "User");
        User newSeller = new User("newseller@example.com", "password987", "newSellerUser", "NewSeller", "User");

        purchase.setStatus("COMPLETED");
        assertEquals("COMPLETED", purchase.getStatus(), "El estado debería ser COMPLETED");

        purchase = new Purchase(2L, newBuyer, newSeller, 200.0f, "PAYPAL", "PENDING");
        assertEquals(2L, purchase.getItemId(), "El ID del ítem debería ser 2");
        assertEquals(newBuyer, purchase.getBuyer(), "El comprador debería coincidir");
        assertEquals(newSeller, purchase.getSeller(), "El vendedor debería coincidir");
        assertEquals(200.0f, purchase.getPrice(), "El precio debería ser 200.0");
        assertEquals("PAYPAL", purchase.getPaymentMethod(), "El método de pago debería ser PAYPAL");
    }
}