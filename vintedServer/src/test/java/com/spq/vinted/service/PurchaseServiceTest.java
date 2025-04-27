package com.spq.vinted.service;

import com.spq.vinted.dto.PurchaseDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class PurchaseServiceTest {

    private PurchaseService purchaseService;

    @BeforeEach
    void setUp() {
        purchaseService = new PurchaseService();
    }

    @Test
    void testCreatePurchase() {
        long token = 123L;
        PurchaseDTO purchase = new PurchaseDTO();
        purchase.setItemId(1L);
        purchase.setPrice(100.0f);

        PurchaseDTO createdPurchase = purchaseService.createPurchase(token, purchase);

        assertNotNull(createdPurchase, "La compra creada no debería ser nula");
        assertEquals(1L, createdPurchase.getId(), "El ID de la compra debería ser 1");
        assertEquals("PENDING", createdPurchase.getStatus(), "El estado inicial debería ser PENDING");
        assertEquals(100.0, createdPurchase.getPrice(), "El precio debería coincidir");
    }

    @Test
    void testCreateMultiplePurchases() {
        long token = 123L;
        PurchaseDTO purchase1 = new PurchaseDTO();
        purchase1.setItemId(1L);
        purchase1.setPrice(100.0f);

        PurchaseDTO purchase2 = new PurchaseDTO();
        purchase2.setItemId(2L);
        purchase2.setPrice(200.0f);

        List<PurchaseDTO> purchases = Arrays.asList(purchase1, purchase2);
        List<PurchaseDTO> createdPurchases = purchaseService.createMultiplePurchases(token, purchases);

        assertEquals(2, createdPurchases.size(), "Deberían haberse creado 2 compras");
        assertEquals(1L, createdPurchases.get(0).getId(), "El ID de la primera compra debería ser 1");
        assertEquals(2L, createdPurchases.get(1).getId(), "El ID de la segunda compra debería ser 2");
    }

    @Test
    void testProcessPayment() {
        long token = 123L;
        PurchaseDTO purchase = new PurchaseDTO();
        purchase.setItemId(1L);
        purchase.setPrice(100.0f);

        PurchaseDTO createdPurchase = purchaseService.createPurchase(token, purchase);

        boolean paymentSuccess = purchaseService.processPayment(token, createdPurchase.getId(), "CREDIT_CARD");

        assertTrue(paymentSuccess, "El pago debería ser exitoso");
        assertEquals("COMPLETED", createdPurchase.getStatus(), "El estado de la compra debería ser COMPLETED");
        assertEquals("CREDIT_CARD", createdPurchase.getPaymentMethod(), "El método de pago debería ser CREDIT_CARD");
    }

    @Test
    void testGetPurchaseById() {
        long token = 123L;
        PurchaseDTO purchase = new PurchaseDTO();
        purchase.setItemId(1L);
        purchase.setPrice(100.0f);

        PurchaseDTO createdPurchase = purchaseService.createPurchase(token, purchase);

        PurchaseDTO fetchedPurchase = purchaseService.getPurchaseById(token, createdPurchase.getId());

        assertNotNull(fetchedPurchase, "La compra obtenida no debería ser nula");
        assertEquals(createdPurchase.getId(), fetchedPurchase.getId(), "El ID de la compra debería coincidir");
    }

    @Test
    void testGetPurchaseById_NotAuthorized() {
        long token = 123L;
        long otherToken = 456L;
        PurchaseDTO purchase = new PurchaseDTO();
        purchase.setItemId(1L);
        purchase.setPrice(100.0f);

        PurchaseDTO createdPurchase = purchaseService.createPurchase(token, purchase);

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            purchaseService.getPurchaseById(otherToken, createdPurchase.getId());
        });

        assertEquals("Purchase not found or not authorized.", exception.getMessage());
    }

    @Test
    void testGetUserPurchases() {
        long token = 123L;
        PurchaseDTO purchase1 = new PurchaseDTO();
        purchase1.setItemId(1L);
        purchase1.setPrice(100.0f);

        PurchaseDTO purchase2 = new PurchaseDTO();
        purchase2.setItemId(2L);
        purchase2.setPrice(200.0f);

        purchaseService.createPurchase(token, purchase1);
        purchaseService.createPurchase(token, purchase2);

        List<PurchaseDTO> userPurchases = purchaseService.getUserPurchases(token);

        assertEquals(2, userPurchases.size(), "El usuario debería tener 2 compras");
    }

    @Test
    void testCancelPurchase() {
        long token = 123L;
        PurchaseDTO purchase = new PurchaseDTO();
        purchase.setItemId(1L);
        purchase.setPrice(100.0f);

        PurchaseDTO createdPurchase = purchaseService.createPurchase(token, purchase);

        purchaseService.cancelPurchase(token, createdPurchase.getId());

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            purchaseService.getPurchaseById(token, createdPurchase.getId());
        });

        assertEquals("Purchase not found or not authorized.", exception.getMessage());
    }

    @Test
    void testCancelPurchase_NotAuthorized() {
        long token = 123L;
        long otherToken = 456L;
        PurchaseDTO purchase = new PurchaseDTO();
        purchase.setItemId(1L);
        purchase.setPrice(100.0f);

        PurchaseDTO createdPurchase = purchaseService.createPurchase(token, purchase);

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            purchaseService.cancelPurchase(otherToken, createdPurchase.getId());
        });

        assertEquals("Purchase not found or not authorized.", exception.getMessage());
    }
}