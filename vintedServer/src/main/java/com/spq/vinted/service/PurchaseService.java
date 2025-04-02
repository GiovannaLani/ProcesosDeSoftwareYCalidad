package com.spq.vinted.service;

import com.spq.vinted.dto.PurchaseDTO;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class PurchaseService {
    private final Map<Long, PurchaseDTO> purchases = new HashMap<>();
    private final Map<Long, List<Long>> userPurchases = new HashMap<>();
    private long purchaseCounter = 1;

    public List<PurchaseDTO> createMultiplePurchases(long token, List<PurchaseDTO> purchases) {
        List<PurchaseDTO> createdPurchases = new ArrayList<>();
        for (PurchaseDTO purchase : purchases) {
            PurchaseDTO createdPurchase = createPurchase(token, purchase);
            createdPurchases.add(createdPurchase);
        }
        return createdPurchases;
    }

    public PurchaseDTO createPurchase(long token, PurchaseDTO purchase) {
        purchase.setStatus("PENDING");
        purchase.setId(purchaseCounter);
        purchases.put(purchaseCounter, purchase);
        userPurchases.computeIfAbsent(token, k -> new ArrayList<>()).add(purchaseCounter);
        purchaseCounter++;
        return purchase;
    }

    public boolean processPayment(long token, long purchaseId, String paymentMethod) {
        PurchaseDTO purchase = purchases.get(purchaseId);
        if (purchase != null && userPurchases.getOrDefault(token, Collections.emptyList()).contains(purchaseId)) {
            purchase.setPaymentMethod(paymentMethod);
            purchase.setStatus("COMPLETED");
            return true;
        }
        return false;
    }

    public PurchaseDTO getPurchaseById(long token, long purchaseId) {
        System.out.println("Token: " + token);
        PurchaseDTO purchase = purchases.get(purchaseId);
        if (purchase != null && userPurchases.getOrDefault(token, Collections.emptyList()).contains(purchaseId)) {
            return purchase;
        }
        throw new RuntimeException("Purchase not found or not authorized.");
    }

    public List<PurchaseDTO> getUserPurchases(long token) {
        List<Long> userPurchaseIds = userPurchases.getOrDefault(token, Collections.emptyList());
        List<PurchaseDTO> userPurchasesList = new ArrayList<>();
        for (Long purchaseId : userPurchaseIds) {
            userPurchasesList.add(purchases.get(purchaseId));
        }
        return userPurchasesList;
    }

    public void cancelPurchase(long token, long purchaseId) {
        if (purchases.containsKey(purchaseId) && userPurchases.getOrDefault(token, Collections.emptyList()).contains(purchaseId)) {
            purchases.remove(purchaseId);
            userPurchases.get(token).remove(purchaseId);
        } else {
            throw new RuntimeException("Purchase not found or not authorized.");
        }
    }
}
