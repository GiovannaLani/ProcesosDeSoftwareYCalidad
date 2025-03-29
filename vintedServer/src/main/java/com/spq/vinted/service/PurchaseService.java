package com.spq.vinted.service;

import com.spq.vinted.dto.PurchaseDTO;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class PurchaseService {
    private final Map<Long, PurchaseDTO> purchases = new HashMap<>();
    private final Map<Long, List<Long>> userPurchases = new HashMap<>();
    private long purchaseCounter = 1;

    public void createPurchase(long token, PurchaseDTO purchase) {
        purchase.setStatus("PENDING");
        purchases.put(purchaseCounter, purchase);
        userPurchases.computeIfAbsent(token, k -> new ArrayList<>()).add(purchaseCounter);
        
        purchaseCounter++;
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
