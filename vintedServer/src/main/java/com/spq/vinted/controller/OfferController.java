package com.spq.vinted.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.spq.vinted.model.Offer;
import com.spq.vinted.model.User;
import com.spq.vinted.service.OfferService;

import io.swagger.v3.oas.annotations.parameters.RequestBody;

@RestController
@RequestMapping("/api/offers")
public class OfferController {
    
    @Autowired
    private OfferService offerService;
    
    @PostMapping
    public ResponseEntity<Offer> createOffer(@RequestBody Offer request, @AuthenticationPrincipal User userDetails) {
        Long userId = userDetails.getId();
        Offer offer = offerService.createOffer(
                userId,
                request.getReceiver().getId(),
                request.getItem().getId(),
                request.getChat().getId(),
                request.getPrice()
        );
        return ResponseEntity.ok(offer);
    }
    
    @PutMapping("/{id}/accept")
    public ResponseEntity<Offer> acceptOffer(@PathVariable Long id, @AuthenticationPrincipal User userDetails) {
        try {
            Long userId = userDetails.getId();
            Offer offer = offerService.acceptOffer(id, userId);
            return ResponseEntity.ok(offer);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    @PutMapping("/{id}/reject")
    public ResponseEntity<Offer> rejectOffer(@PathVariable Long id, @AuthenticationPrincipal User userDetails) {
        try {
            Long userId = userDetails.getId();
            Offer offer = offerService.rejectOffer(id, userId);
            return ResponseEntity.ok(offer);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
}


    