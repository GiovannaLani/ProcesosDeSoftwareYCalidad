package com.spq.vinted.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.spq.vinted.model.Offer;

@Repository
public interface OfferRepository extends JpaRepository<Offer, Long> {
    List<Offer> findByChatIdAndStatus(Long chatId, Offer.OfferStatus status);
}
