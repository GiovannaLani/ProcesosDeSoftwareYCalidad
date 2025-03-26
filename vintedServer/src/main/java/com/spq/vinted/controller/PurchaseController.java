package com.spq.vinted.controller;

import com.spq.vinted.dto.PurchaseDTO;
import com.spq.vinted.service.PurchaseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/purchases")
public class PurchaseController {

    @Autowired
    private PurchaseService purchaseService;

    @PostMapping("/create")
    public ResponseEntity<String> createPurchase(@RequestBody PurchaseDTO purchase) {
        purchaseService.createPurchase(purchase);
        return ResponseEntity.ok("Purchase created successfully.");
    }

    @PostMapping("/pay")
    public ResponseEntity<String> processPayment(@RequestParam long purchaseId, @RequestParam String paymentMethod) {
        boolean success = purchaseService.processPayment(purchaseId, paymentMethod);
        if (success) {
            return ResponseEntity.ok("Payment successful.");
        } else {
            return ResponseEntity.badRequest().body("Payment failed.");
        }
    }
}
