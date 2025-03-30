package com.spq.vinted.repository;

import com.spq.vinted.model.Purchase;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PurchaseRepository extends JpaRepository<Purchase, Long> {
    List<Purchase> findByBuyerUsername(String buyerUsername);
    List<Purchase> findBySellerUsername(String sellerUsername);
}
