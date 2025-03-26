package com.spq.vinted.service;

import com.spq.vinted.dto.PurchaseDTO;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class PurchaseService {
    private final Map<Long, PurchaseDTO> purchases = new HashMap<>();
    private long purchaseCounter = 1;

    public void createPurchase(PurchaseDTO purchase) {
        purchase.setStatus("PENDING");
        purchases.put(purchaseCounter, purchase);
        purchaseCounter++;
    }

    public boolean processPayment(long purchaseId, String paymentMethod) {
        PurchaseDTO purchase = purchases.get(purchaseId);
        if (purchase != null) {
            purchase.setPaymentMethod(paymentMethod);
            purchase.setStatus("COMPLETED");
            return true;
        }
        return false;
    }
}
