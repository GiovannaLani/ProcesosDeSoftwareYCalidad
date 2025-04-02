package com.spq.vinted.controller;

import com.spq.vinted.dto.PurchaseDTO;
import com.spq.vinted.service.PurchaseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/purchases")
public class PurchaseController {

    @Autowired
    private PurchaseService purchaseService;

    @PostMapping("/create")
    public ResponseEntity<PurchaseDTO> createPurchase(@RequestParam long token, @RequestBody PurchaseDTO purchase) {
        try {
            PurchaseDTO createdPurchase = purchaseService.createPurchase(token, purchase);
            return ResponseEntity.ok(createdPurchase);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(null);
        }
    }

    @PostMapping("/pay")
    public ResponseEntity<Map<String, String>> processPayment(@RequestParam long token, @RequestParam long purchaseId, @RequestParam String paymentMethod) {
        boolean success = purchaseService.processPayment(token, purchaseId, paymentMethod);
        Map<String, String> response = new HashMap<>();
        if (success) {
            response.put("status", "success");
            response.put("message", "Payment successful.");
            return ResponseEntity.ok(response);
        } else {
            response.put("status", "error");
            response.put("message", "Payment failed.");
            return ResponseEntity.badRequest().body(response);
        }
    }

    @PostMapping("/payMultiple")
    public ResponseEntity<Map<String, String>> processMultiplePayments(
            @RequestParam long token,
            @RequestParam List<Long> purchaseIds,
            @RequestParam String paymentMethod) {
        Map<String, String> response = new HashMap<>();
        boolean allPaymentsSuccessful = true;
    
        for (Long purchaseId : purchaseIds) {
            boolean success = purchaseService.processPayment(token, purchaseId, paymentMethod);
            if (!success) {
                allPaymentsSuccessful = false;
                response.put("status", "error");
                response.put("message", "Payment failed for purchase ID " + purchaseId);
                return ResponseEntity.badRequest().body(response);
            }
        }
    
        if (allPaymentsSuccessful) {
            response.put("status", "success");
            response.put("message", "All payments processed successfully.");
            return ResponseEntity.ok(response);
        } else {
            response.put("status", "error");
            response.put("message", "Some payments failed.");
            return ResponseEntity.badRequest().body(response);
        }
    }

    @GetMapping("/{purchaseId}")
    public ResponseEntity<PurchaseDTO> getPurchaseById(@RequestParam long token, @PathVariable long purchaseId) {
        try {
            System.out.println("Token: " + token);
            PurchaseDTO purchase = purchaseService.getPurchaseById(token, purchaseId);
            System.out.println("Purchase: " + purchase);
            return ResponseEntity.ok(purchase);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(null);
        }
    }

    @GetMapping("/user")
    public ResponseEntity<List<PurchaseDTO>> getUserPurchases(@RequestParam long token) {
        List<PurchaseDTO> purchases = purchaseService.getUserPurchases(token);
        return ResponseEntity.ok(purchases);
    }

    @DeleteMapping("/cancel")
    public ResponseEntity<String> cancelPurchase(@RequestParam long token, @RequestParam long purchaseId) {
        try {
            purchaseService.cancelPurchase(token, purchaseId);
            return ResponseEntity.ok("Purchase canceled successfully.");
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body("Purchase not found or not authorized.");
        }
    }
}
