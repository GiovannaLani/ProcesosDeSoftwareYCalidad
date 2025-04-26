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
    public ResponseEntity<Offer> createOffer(@RequestBody CreateOfferRequest request,
                                           @AuthenticationPrincipal User userDetails) {
        Long userId = userDetails.getId();
        Offer offer = offerService.createOffer(
                userId,
                request.getReceiverId(),
                request.getProductId(),
                request.getChatId(),
                request.getPrice()
        );
        return ResponseEntity.ok(offer);
    }
    
    @PutMapping("/{id}/accept")
    public ResponseEntity<Offer> acceptOffer(@PathVariable Long id,
                                           @AuthenticationPrincipal User userDetails) throws Exception {
        Long userId = userDetails.getId();
        Offer offer = offerService.acceptOffer(id, userId);
        return ResponseEntity.ok(offer);
    }
    
    @PutMapping("/{id}/reject")
    public ResponseEntity<Offer> rejectOffer(@PathVariable Long id,
                                           @AuthenticationPrincipal User userDetails) throws Exception {
        Long userId = userDetails.getId();
        Offer offer = offerService.rejectOffer(id, userId);
        return ResponseEntity.ok(offer);
    }
}

class CreateOfferRequest {
    private Long receiverId;
    private Long productId;
    private Long chatId;
    private Double price;
    
    
    public Long getReceiverId() {
        return receiverId;
    }
    public void setReceiverId(Long receiverId) {
        this.receiverId = receiverId;
    }
    public Long getProductId() {
        return productId;
    }
    public void setProductId(Long productId) {
        this.productId = productId;
    }
    public Long getChatId() {
        return chatId;
    }
    public void setChatId(Long chatId) {
        this.chatId = chatId;
    }
    public Double getPrice() {
        return price;
    }
    public void setPrice(Double price) {
        this.price = price;
    }
    
}