package com.spq.vinted.controller;


import com.spq.vinted.dto.ShipmentDTO;
import com.spq.vinted.model.Shipment;
import com.spq.vinted.model.ShipmentStatus;
import com.spq.vinted.model.User;
import com.spq.vinted.service.ShipmentService;
import com.spq.vinted.service.UserService;

import jakarta.websocket.server.PathParam;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/shipments")
public class ShipmentController {

    private ShipmentService shipmentService;   
    private UserService userService;
    
    @Autowired
    public ShipmentController(ShipmentService shipmentService, UserService userService) {
        this.userService = userService;
        this.shipmentService = shipmentService;
    }

    @GetMapping("/{buyerId}")
    public ResponseEntity<List<ShipmentDTO>> getShipmentsByBuyerId(
        @PathVariable Long buyerId, @RequestParam(value = "token") Long token) {
        System.out.println("buyerId: " + buyerId);
        List<Shipment> shipments = shipmentService.getShipmentsbyBuyerId(buyerId, token);
        System.out.println("shipments: " + shipments);
        if (shipments.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        List<ShipmentDTO> shipmentDTOs = new ArrayList<>();
        System.out.println("shipmentDTOs: " + shipmentDTOs);
        for (Shipment shipment : shipments) {
            shipmentDTOs.add(shipmentService.convertToDTO(shipment));
            System.out.println("shipmentDTO: " + shipmentDTOs);
        }
        return ResponseEntity.ok(shipmentDTOs);
    }
}