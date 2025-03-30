package com.spq.vinted.controller;

import com.spq.vinted.dto.PurchaseDTO;
import com.spq.vinted.service.PurchaseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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
    public ResponseEntity<String> processPayment(@RequestParam long token, @RequestParam long purchaseId, @RequestParam String paymentMethod) {
        boolean success = purchaseService.processPayment(token, purchaseId, paymentMethod);
        if (success) {
            return ResponseEntity.ok("Payment successful.");
        } else {
            return ResponseEntity.badRequest().body("Payment failed.");
        }
    }

    @GetMapping("/{purchaseId}")
    public ResponseEntity<PurchaseDTO> getPurchaseById(@RequestParam long token, @PathVariable long purchaseId) {
        try {
            PurchaseDTO purchase = purchaseService.getPurchaseById(token, purchaseId);
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
