/**
 * @file PurchaseService.java
 * @brief Clase de servicio para la gestión de compras.
 */
package com.spq.vinted.service;

import com.spq.vinted.dto.PurchaseDTO;
import com.spq.vinted.model.Purchase;
import com.spq.vinted.model.User;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * @class PurchaseService
 * @brief Clase de servicio para la gestión de compras.
 */
@Service
public class PurchaseService {

    private UserService userService;
    private final Map<Long, PurchaseDTO> purchases = new HashMap<>();
    private final Map<Long, List<Long>> userPurchases = new HashMap<>();
    private long purchaseCounter = 1;

    /**
     * @brief Constructor de la clase PurchaseService.
     * @param userService Servicio de usuario.
     */
    @Autowired
    public PurchaseService(UserService userService) {
        this.userService = userService;
    }

    /**
     * @brief Método para crear una compra.
     * @param token Token del usuario.
     * @param purchase Compra a crear.
     * @return Compra creada.
     */
    public List<PurchaseDTO> createMultiplePurchases(long token, List<PurchaseDTO> purchases) {
        List<PurchaseDTO> createdPurchases = new ArrayList<>();
        for (PurchaseDTO purchase : purchases) {
            PurchaseDTO createdPurchase = createPurchase(token, purchase);
            createdPurchases.add(createdPurchase);
        }
        return createdPurchases;
    }

    /**
     * @brief Método para crear una compra.
     * @param token Token del usuario.
     * @param purchase Compra a crear.
     * @return Compra creada.
     */
    public PurchaseDTO createPurchase(long token, PurchaseDTO purchase) {
        purchase.setStatus("PENDING");
        purchase.setId(purchaseCounter);
        purchases.put(purchaseCounter, purchase);
        userPurchases.computeIfAbsent(token, k -> new ArrayList<>()).add(purchaseCounter);
        purchaseCounter++;
        return purchase;
    }

    /**
     * @brief Método para procesar un pago.
     * @param token Token del usuario.
     * @param purchaseId ID de la compra.
     * @param paymentMethod Método de pago.
     * @return true si el pago se procesa correctamente, false en caso contrario.
     */
    public boolean processPayment(long token, long purchaseId, String paymentMethod) {
        PurchaseDTO purchase = purchases.get(purchaseId);
        if (purchase != null && userPurchases.getOrDefault(token, Collections.emptyList()).contains(purchaseId)) {
            purchase.setPaymentMethod(paymentMethod);
            purchase.setStatus("COMPLETED");
            purchases.put(purchaseId, purchase);
            return true;
        }
        return false;
    }

    /**
     * @brief Método para obtener una compra por su ID.
     * @param token Token del usuario.
     * @param purchaseId ID de la compra.
     * @return Compra encontrada.
     * @throws RuntimeException Excepción lanzada si la compra no se encuentra o no está autorizada.
     */
    public PurchaseDTO getPurchaseById(long token, long purchaseId) {
        System.out.println("Token: " + token);
        System.out.println("Purchase ID: " + purchaseId);
        System.out.println("Purchases: " + purchases);
        PurchaseDTO purchase = purchases.get(purchaseId);
        if (purchase != null && userPurchases.getOrDefault(token, Collections.emptyList()).contains(purchaseId)) {
            System.out.println("Purchase found: " + purchase);
            return purchase;
        }
        throw new RuntimeException("Purchase not found or not authorized.");
    }

    /**
     * @brief Método para obtener todas las compras de un usuario.
     * @param token Token del usuario.
     * @return Lista de compras del usuario.
     */
    public List<PurchaseDTO> getUserPurchases(long token) {
        List<Long> userPurchaseIds = userPurchases.getOrDefault(token, Collections.emptyList());
        List<PurchaseDTO> userPurchasesList = new ArrayList<>();
        for (Long purchaseId : userPurchaseIds) {
            userPurchasesList.add(purchases.get(purchaseId));
        }
        return userPurchasesList;
    }

    /**
     * @brief Método para cancelar una compra.
     * @param token Token del usuario.
     * @param purchaseId ID de la compra.
     * @throws RuntimeException Excepción lanzada si la compra no se encuentra o no está autorizada.
     */
    public void cancelPurchase(long token, long purchaseId) {
        if (purchases.containsKey(purchaseId) && userPurchases.getOrDefault(token, Collections.emptyList()).contains(purchaseId)) {
            purchases.remove(purchaseId);
            userPurchases.get(token).remove(purchaseId);
        } else {
            throw new RuntimeException("Purchase not found or not authorized.");
        }
    }

    /**
     * @brief Método para convertir un objeto PurchaseDTO a un objeto Purchase.
     * @param purchaseDTO Objeto PurchaseDTO a convertir.
     * @param token Token del usuario.
     * @return Objeto Purchase convertido.
     */
    public Purchase fromDTO(PurchaseDTO purchaseDTO, Long token) {
        System.out.println("PurchaseDTO2: " + purchaseDTO+ "buyer: " + purchaseDTO.getBuyerUsername() + " seller: " + purchaseDTO.getSellerUsername());
        User buyer = userService.getUserByUsername(purchaseDTO.getBuyerUsername(),token);
        System.out.println("Buyer: " + buyer);
        User seller = userService.getUserByUsername(purchaseDTO.getSellerUsername(),token);
        System.out.println("Seller: " + seller);
        Purchase purchase = new Purchase();
        purchase.setId(purchaseDTO.getId());
        purchase.setItemId(purchaseDTO.getItemId());
        purchase.setStatus(purchaseDTO.getStatus());
        purchase.setBuyer(buyer);
        purchase.setSeller(seller);
        purchase.setPrice(purchaseDTO.getPrice());
        purchase.setPaymentMethod(purchaseDTO.getPaymentMethod());
        System.out.println("Purchase2: " + purchase);
        return purchase;
    }

}
