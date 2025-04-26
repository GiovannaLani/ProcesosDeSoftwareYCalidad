package com.spq.vinted.controller;

import com.spq.vinted.dto.OfferDTO;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/offers")
public class OfferController {
    

    //para almacenar las ofertas
    private Map<Long, OfferDTO> offers = new HashMap<>();
    private Long offerCounter = 1L;
    
    @PostMapping("/create")
    public ResponseEntity<Map<String, Object>> createOffer(@RequestBody OfferDTO offerData) {
        offerData.setOfferId(offerCounter++);
        
        offers.put(offerData.getOfferId(), offerData);
        
        Map<String, Object> response = new HashMap<>();
        response.put("status", "success");
        response.put("offerId", offerData.getOfferId());
        
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<OfferDTO> getOffer(@PathVariable Long id) {
        OfferDTO offer = offers.get(id);
        if (offer != null) {
            return ResponseEntity.ok(offer);
        }
        return ResponseEntity.notFound().build();
    }
    
    @PostMapping("/{id}/status")
    public ResponseEntity<Map<String, Object>> updateOfferStatus(
            @PathVariable Long id, 
            @RequestParam String status) {
        
        OfferDTO offer = offers.get(id);
        if (offer != null) {
            offer.setStatus(status);
            
            Map<String, Object> response = new HashMap<>();
            response.put("status", "success");
            response.put("offer", offer);
            
            return ResponseEntity.ok(response);
        }
        
        return ResponseEntity.notFound().build();
    }
}