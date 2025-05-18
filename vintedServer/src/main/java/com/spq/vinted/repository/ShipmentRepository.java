package com.spq.vinted.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.spq.vinted.model.Shipment;
import com.spq.vinted.model.ShipmentStatus;

@Repository
public interface ShipmentRepository extends JpaRepository<Shipment, Long> {
    List<Shipment> findAllByBuyerId(Long buyerId);
    List<Shipment> findByStatusNot(ShipmentStatus delivered);
}
