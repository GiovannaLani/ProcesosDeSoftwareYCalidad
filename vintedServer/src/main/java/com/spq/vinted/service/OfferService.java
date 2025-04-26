package com.spq.vinted.service;

import com.spq.vinted.model.Offer;
import com.spq.vinted.repository.OfferRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class OfferService {
    @Autowired
    private OfferRepository offerRepository;
    
    public Offer createOffer(Offer offer) {
        return offerRepository.save(offer);
    }
    
    public Offer getOfferById(Long id) {
        return offerRepository.findById(id).orElse(null);
    }
    
    public Offer updateOfferStatus(Long id, String status) {
        Offer offer = getOfferById(id);
        if (offer != null) {
            offer.setStatus(status);
            return offerRepository.save(offer);
        }
        return null;
    }
}