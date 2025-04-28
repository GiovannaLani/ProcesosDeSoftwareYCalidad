package com.spq.vinted.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.spq.vinted.dto.PurchaseDTO;
import com.spq.vinted.service.PurchaseService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(PurchaseController.class)
class PurchaseControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private PurchaseService purchaseService;

    @Autowired
    private ObjectMapper objectMapper;

    private PurchaseDTO purchaseDTO;

    @BeforeEach
    void setUp() {
        purchaseDTO = new PurchaseDTO();
        purchaseDTO.setId(1L);
        purchaseDTO.setItemId(100L);
        purchaseDTO.setPrice(50.0f);
        purchaseDTO.setStatus("PENDING");
    }

    @Test
    void testCreatePurchase() throws Exception {
        Mockito.when(purchaseService.createPurchase(eq(123L), any(PurchaseDTO.class))).thenReturn(purchaseDTO);

        mockMvc.perform(post("/purchases/create")
                .param("token", "123")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(purchaseDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.itemId").value(100L))
                .andExpect(jsonPath("$.price").value(50.0))
                .andExpect(jsonPath("$.status").value("PENDING"));
    }

    @Test
    void testCreatePurchase_Error() throws Exception {
        Mockito.when(purchaseService.createPurchase(eq(123L), any(PurchaseDTO.class)))
                .thenThrow(new RuntimeException("Error creating purchase"));
    
        mockMvc.perform(post("/purchases/create")
                .param("token", "123")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(purchaseDTO)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testCreateMultiplePurchases() throws Exception {
        List<PurchaseDTO> purchases = Arrays.asList(purchaseDTO);
        Mockito.when(purchaseService.createMultiplePurchases(eq(123L), any(List.class))).thenReturn(purchases);

        mockMvc.perform(post("/purchases/multipleCreate")
                .param("token", "123")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(purchases)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].id").value(1L));
    }

    @Test
    void testProcessPayment() throws Exception {
        Map<String, String> response = new HashMap<>();
        response.put("status", "success");
        response.put("message", "Payment successful.");

        Mockito.when(purchaseService.processPayment(123L, 1L, "CREDIT_CARD")).thenReturn(true);

        mockMvc.perform(post("/purchases/pay")
                .param("token", "123")
                .param("purchaseId", "1")
                .param("paymentMethod", "CREDIT_CARD"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("success"))
                .andExpect(jsonPath("$.message").value("Payment successful."));
    }

    @Test
    void testProcessPayment_Failure() throws Exception {
        Mockito.when(purchaseService.processPayment(123L, 1L, "CREDIT_CARD")).thenReturn(false);
    
        mockMvc.perform(post("/purchases/pay")
                .param("token", "123")
                .param("purchaseId", "1")
                .param("paymentMethod", "CREDIT_CARD"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value("error"))
                .andExpect(jsonPath("$.message").value("Payment failed."));
    }

    @Test
    void testGetPurchaseById() throws Exception {
        Mockito.when(purchaseService.getPurchaseById(123L, 1L)).thenReturn(purchaseDTO);

        mockMvc.perform(get("/purchases/1")
                .param("token", "123"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.itemId").value(100L))
                .andExpect(jsonPath("$.price").value(50.0))
                .andExpect(jsonPath("$.status").value("PENDING"));
    }

    @Test
    void testGetPurchaseById_InvalidToken() throws Exception {
        Mockito.when(purchaseService.getPurchaseById(999L, 1L))
                .thenThrow(new RuntimeException("Unauthorized"));
    
        mockMvc.perform(get("/purchases/1")
                .param("token", "999"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testGetUserPurchases() throws Exception {
        List<PurchaseDTO> purchases = Arrays.asList(purchaseDTO);
        Mockito.when(purchaseService.getUserPurchases(123L)).thenReturn(purchases);

        mockMvc.perform(get("/purchases/user")
                .param("token", "123"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].id").value(1L));
    }

    @Test
    void testCancelPurchase() throws Exception {
        Mockito.doNothing().when(purchaseService).cancelPurchase(123L, 1L);

        mockMvc.perform(delete("/purchases/cancel")
                .param("token", "123")
                .param("purchaseId", "1"))
                .andExpect(status().isOk())
                .andExpect(content().string("Purchase canceled successfully."));
    }

    @Test
    void testCancelPurchase_Error() throws Exception {
        Mockito.doThrow(new RuntimeException("Purchase not found or not authorized."))
                .when(purchaseService).cancelPurchase(123L, 1L);
    
        mockMvc.perform(delete("/purchases/cancel")
                .param("token", "123")
                .param("purchaseId", "1"))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Purchase not found or not authorized."));
    }

    @Test
    void testProcessMultiplePayments_Success() throws Exception {
        Mockito.when(purchaseService.processPayment(123L, 1L, "CREDIT_CARD")).thenReturn(true);
        Mockito.when(purchaseService.processPayment(123L, 2L, "CREDIT_CARD")).thenReturn(true);
    
        mockMvc.perform(post("/purchases/payMultiple")
                .param("token", "123")
                .param("purchaseIds", "1,2")
                .param("paymentMethod", "CREDIT_CARD"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("success"))
                .andExpect(jsonPath("$.message").value("All payments processed successfully."));
    }
    
    @Test
    void testProcessMultiplePayments_Failure() throws Exception {
        Mockito.when(purchaseService.processPayment(123L, 1L, "CREDIT_CARD")).thenReturn(true);
        Mockito.when(purchaseService.processPayment(123L, 2L, "CREDIT_CARD")).thenReturn(false);
    
        mockMvc.perform(post("/purchases/payMultiple")
                .param("token", "123")
                .param("purchaseIds", "1,2")
                .param("paymentMethod", "CREDIT_CARD"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value("error"))
                .andExpect(jsonPath("$.message").value("Payment failed for purchase ID 2"));
    }
}