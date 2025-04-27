package com.spq.vinted.controller;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.spq.vinted.dto.OfferDTO;
import com.spq.vinted.dto.OfferReturnerDTO;
import com.spq.vinted.model.Offer;
import com.spq.vinted.model.User;
import com.spq.vinted.service.OfferService;

@RestController
@RequestMapping("/offers")
public class OfferController {
    
    @Autowired
    private OfferService offerService;

    


    @PostMapping("/create")
    public ResponseEntity<Void> createOffer(@RequestBody OfferDTO request, @RequestParam Long token) {
        Boolean isSaved = offerService.createOffer(token, request.getReceiverId(), request.getItemId(), request.getChatRoomId(), request.getPrice());
        return ResponseEntity.status(isSaved ? 200 : 400).build();
    }
/*     
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
    } */

    @GetMapping("/item/{itemId}/offers")
    public ResponseEntity<List<OfferReturnerDTO>> getOffersByItem(@PathVariable Long itemId, @RequestParam Long token) {
        List<Offer> offers = offerService.getOffersByItem(itemId, token);
        List<OfferReturnerDTO> offerReturnerDTOs = new ArrayList<>();
        for (Offer offer : offers) {
            offerReturnerDTOs.add(convertToOfferReturnerDTO(offer));
        }
        return ResponseEntity.ok(offerReturnerDTOs);
    }

    private OfferReturnerDTO convertToOfferReturnerDTO(Offer offer) {
        return new OfferReturnerDTO(offer.getId(), offer.getPrice(), offer.getStatus().toString(), offer.getSender().getId(), offer.getReceiver().getId(), offer.getItem().getId(), offer.getChat().getId());
    }
}


    