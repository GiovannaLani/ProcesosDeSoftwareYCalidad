package com.spq.vinted.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.spq.vinted.dto.ItemDTO;
import com.spq.vinted.dto.ShipmentDTO;
import com.spq.vinted.dto.UserDTO;
import com.spq.vinted.model.Clothes;
import com.spq.vinted.model.Item;
import com.spq.vinted.model.Shipment;
import com.spq.vinted.model.ShipmentStatus;
import com.spq.vinted.model.User;
import com.spq.vinted.service.ShipmentService;
import com.spq.vinted.service.UserService;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

class ShipmentControllerTest {

    @Mock
    private ShipmentService shipmentService;

    @Mock
    private UserService userService;

    @InjectMocks
    private ShipmentController shipmentController;

    private final Long validBuyerId = 1L;
    private final Long validToken = 12345L;
    private final Long invalidToken = 99999L;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGetShipmentsByBuyerId_Success() {
        List<Shipment> mockShipments = new ArrayList<>();
        mockShipments.add(createMockShipment(1L));
        mockShipments.add(createMockShipment(2L));

        List<ShipmentDTO> mockDTOs = new ArrayList<>();
        mockDTOs.add(createMockShipmentDTO(1L));
        mockDTOs.add(createMockShipmentDTO(2L));


        when(shipmentService.getShipmentsbyBuyerId(validBuyerId, validToken))
            .thenReturn(mockShipments);
        when(shipmentService.convertToDTO(any(Shipment.class)))
            .thenReturn(mockDTOs.get(0), mockDTOs.get(1));

        // Ejecutar método
        ResponseEntity<List<ShipmentDTO>> response = 
            shipmentController.getShipmentsByBuyerId(validBuyerId, validToken);

        // Verificar resultados
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(2, response.getBody().size());
        verify(shipmentService, times(2)).convertToDTO(any(Shipment.class));
    }

    @Test
    void testGetShipmentsByBuyerId_NotFound() {
        when(shipmentService.getShipmentsbyBuyerId(validBuyerId, validToken))
            .thenReturn(new ArrayList<>());

        ResponseEntity<List<ShipmentDTO>> response = 
            shipmentController.getShipmentsByBuyerId(validBuyerId, validToken);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        verify(shipmentService, never()).convertToDTO(any());
    }

    @Test
    void testGetShipmentsByBuyerId_Unauthorized() {
        when(shipmentService.getShipmentsbyBuyerId(validBuyerId, invalidToken))
            .thenThrow(new SecurityException("Acceso no autorizado"));

        assertThrows(SecurityException.class, () -> {
            shipmentController.getShipmentsByBuyerId(validBuyerId, invalidToken);
        });
    }

    private Shipment createMockShipment(Long id) {
        Shipment shipment = new Shipment();
        shipment.setId(id);
        shipment.setStatus(ShipmentStatus.SHIPPED);
        return shipment;
    }

    private ShipmentDTO createMockShipmentDTO(Long id) {
        User buyer = new User();
        buyer.setId(id);
        buyer.setUsername("buyer" + id);
        UserDTO buyerDTO = buyer.toDTO();
        Item item = new Clothes();
        item.setId(id);
        item.setTitle("item" + id);
        ItemDTO itemDTO = item.toDTO();
        return new ShipmentDTO(
            id,
            ShipmentStatus.SHIPPED,
            buyerDTO,
            itemDTO
        );
    }
}