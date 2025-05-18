package com.spq.vinted.service;

import com.spq.vinted.dto.PurchaseDTO;
import com.spq.vinted.model.Purchase;
import com.spq.vinted.model.User;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class PurchaseService {
    private UserService userService;
    private final Map<Long, PurchaseDTO> purchases = new HashMap<>();
    private final Map<Long, List<Long>> userPurchases = new HashMap<>();
    private long purchaseCounter = 1;

    @Autowired
    public PurchaseService(UserService userService) {
        this.userService = userService;
    }

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
            purchases.put(purchaseId, purchase);
            return true;
        }
        return false;
    }

    public PurchaseDTO getPurchaseById(long token, long purchaseId) {
        System.out.println("Token: " + token);
        System.out.println("Purchase ID: " + purchaseId);
        System.out.println("Purchases: " + purchases);
        PurchaseDTO purchase = purchases.get(purchaseId);
        if (purchase != null && userPurchases.getOrDefault(token, Collections.emptyList()).contains(purchaseId)) {
            System.out.println("Purchase found: " + purchase);
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

    public Purchase fromDTO(PurchaseDTO purchaseDTO, Long token) {
        System.out.println("PurchaseDTO2: " + purchaseDTO+ "buyer: " + purchaseDTO.getBuyerUsername() + " seller: " + purchaseDTO.getSellerUsername());
        User buyer = userService.getUserByUsername(purchaseDTO.getBuyerUsername(),token);
        System.out.println("Buyer: " + buyer);
        User seller = userService.getUserByUsername(purchaseDTO.getSellerUsername(),token);
        System.out.println("Seller: " + seller);
        Purchase purchase = new Purchase();
        purchase.setId(purchaseDTO.getId());
        purchase.setItemId(purchaseDTO.getItemId());
        purchase.setStatus(purchaseDTO.getStatus());
        purchase.setBuyer(buyer);
        purchase.setSeller(seller);
        purchase.setPrice(purchaseDTO.getPrice());
        purchase.setPaymentMethod(purchaseDTO.getPaymentMethod());
        System.out.println("Purchase2: " + purchase);
        return purchase;
    }

    // public List<PurchaseDTO> convertToDTO(List<Purchase> purchases) {
    //     List<PurchaseDTO> purchaseDTOs = new ArrayList<>();
    //     for (Purchase purchase : purchases) {
    //         PurchaseDTO dto = new PurchaseDTO();
    //         dto.setId(purchase.getId());
    //         dto.setItemId(purchase.getItemId());
    //         dto.setBuyerUsername(purchase.getBuyer().getUsername());
    //         dto.setSellerUsername(purchase.getSeller().getUsername());
    //         dto.setStatus(purchase.getStatus());
    //         dto.setPaymentMethod(purchase.getPaymentMethod());
    //         purchaseDTOs.add(dto);
    //     }
    //     return purchaseDTOs;
    // }

}
