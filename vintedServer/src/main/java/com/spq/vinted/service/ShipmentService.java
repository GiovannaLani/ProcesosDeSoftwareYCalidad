package com.spq.vinted.service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.spq.vinted.dto.ItemDTO;
import com.spq.vinted.dto.PurchaseDTO;
import com.spq.vinted.dto.ShipmentDTO;
import com.spq.vinted.model.Item;
import com.spq.vinted.model.Purchase;
import com.spq.vinted.model.Shipment;
import com.spq.vinted.model.ShipmentStatus;
import com.spq.vinted.model.User;
import com.spq.vinted.repository.ItemRepository;
import com.spq.vinted.repository.PurchaseRepository;
import com.spq.vinted.repository.ShipmentRepository;
import com.spq.vinted.repository.UserRepository;

import org.springframework.transaction.annotation.Transactional;


@Service
public class ShipmentService {
;
    private final UserService userService;
    private final UserRepository userRepository;
    private final ShipmentRepository shipmentRepository;
    private final ItemService itemService;
    private final PurchaseRepository purchaseRepository;
    private final PurchaseService purchaseService;
    

    @Autowired
    public ShipmentService(UserService userService,
                           ShipmentRepository shipmentRepository, UserRepository userRepository,
                           ItemService itemService, PurchaseRepository purchaseRepository, PurchaseService purchaseService) {
        this.userRepository = userRepository;
        this.userService = userService;
        this.shipmentRepository = shipmentRepository;
        this.itemService = itemService;
        this.purchaseRepository = purchaseRepository;
        this.purchaseService = purchaseService;
    }

    @Transactional
    public Shipment createShipment(Long purchaseIds, ShipmentStatus status,Long token) {
        System.out.println("purchaseIds: " + purchaseIds);
        PurchaseDTO purchaseDTO = purchaseService.getPurchaseById(token, purchaseIds);
        System.out.println("purchaseDTO: " + purchaseDTO);
        Purchase purchase = purchaseService.fromDTO(purchaseDTO,token);
        System.out.println("purchases: " + purchase);
        User user = userService.getUserByToken(token);
        Item item = itemService.getItemById(purchase.getItemId());
        
        // Crear envío
        Shipment shipment = new Shipment();
        shipment.setItem(item);
        System.out.println("item: " + item);
        shipment.setBuyer(user);
        System.out.println("buyer: " + user);
        shipment.setStatus(status);
        System.out.println("shipment: " + shipment.getBuyer() + " " + shipment.getItem() + " " + shipment.getStatus());
        return shipmentRepository.save(shipment);
    }

    public ShipmentDTO convertToDTO(Shipment shipment) {
        ShipmentDTO dto = new ShipmentDTO();
        dto.setId(shipment.getId());
        dto.setStatus(shipment.getStatus());
        ItemDTO itemDTO = ItemService.convertToDTO(shipment.getItem());
        dto.setItem(itemDTO);
        dto.setBuyerId(shipment.getBuyer().getId());
        dto.setBuyer(shipment.getBuyer().toDTO());
        return dto;
    }

    public List<Shipment> getShipmentsbyBuyerId(Long buyerId, Long token) {
        System.out.println("buyerId: " + buyerId);
        System.out.println("token: " + token);
        User user = userService.getUserById(buyerId);
        if (user == null) {
            throw new IllegalArgumentException("El usuario no existe");
        }
        System.out.println("user id: " + user.getId());
        if (!user.getId().equals(buyerId)) {
            throw new IllegalArgumentException("El usuario no tiene permiso para ver este envío");
        }
        List<Shipment> shipments = new ArrayList<>();
        shipments = shipmentRepository.findAllByBuyerId(buyerId);
        System.out.println("shipments: " + shipments);
        return shipments;
    }
}