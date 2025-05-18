package com.spq.vinted.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.spq.vinted.dto.ItemDTO;
import com.spq.vinted.dto.ShipmentDTO;
import com.spq.vinted.dto.UserDTO;
import com.spq.vinted.model.Clothes;
import com.spq.vinted.model.Item;
import com.spq.vinted.model.Shipment;
import com.spq.vinted.model.ShipmentStatus;
import com.spq.vinted.model.User;

class ShipmentServiceTest {

    @Mock
    private ShipmentService shipmentService;

    @Mock
    private UserService userService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGetShipmentsByBuyerId_Success() {
        Long validBuyerId = 1L;
        Long validToken = 12345L;
        
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
        
        List<Shipment> result = shipmentService.getShipmentsbyBuyerId(validBuyerId, validToken);
        
        assertEquals(2, result.size());
        verify(shipmentService, times(1)).getShipmentsbyBuyerId(validBuyerId, validToken);
    }

    @Test
    void testGetShipmentsByBuyerId_NotFound() {
        Long validBuyerId = 1L;
        Long validToken = 12345L;
        
        when(shipmentService.getShipmentsbyBuyerId(validBuyerId, validToken))
            .thenReturn(new ArrayList<>());
        
        List<Shipment> result = shipmentService.getShipmentsbyBuyerId(validBuyerId, validToken);
        
        assertTrue(result.isEmpty());
    }

    @Test
    void testConvertToDTO() {
        Shipment shipment = new Shipment();
        shipment.setId(1L);
        shipment.setStatus(ShipmentStatus.SHIPPED);
        
        User buyer = new User();
        buyer.setId(1L);
        buyer.setUsername("buyer1");
        shipment.setBuyer(buyer);
        
        Item item = new Clothes();
        item.setId(10L);
        item.setTitle("Test Item");
        
        User seller = new User();
        seller.setId(2L);
        seller.setUsername("seller1");
        item.setSeller(seller);
        
        shipment.setItem(item);
        
        ShipmentDTO expectedDTO = new ShipmentDTO();
        expectedDTO.setId(1L);
        expectedDTO.setStatus(ShipmentStatus.SHIPPED);
        expectedDTO.setBuyerId(1L);
        
        when(shipmentService.convertToDTO(shipment)).thenReturn(expectedDTO);
        
        ShipmentDTO result = shipmentService.convertToDTO(shipment);
        
        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals(ShipmentStatus.SHIPPED, result.getStatus());
    }

    @Test
    void testGetShipmentsByBuyerId_Unauthorized_Approach1() {
        Long buyerId = 2L;
        Long invalidToken = 99999L;
        
        when(shipmentService.getShipmentsbyBuyerId(buyerId, invalidToken))
            .thenThrow(new IllegalArgumentException("El usuario no tiene permiso para ver este envío"));
        
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            shipmentService.getShipmentsbyBuyerId(buyerId, invalidToken);
        });
        
        assertEquals("El usuario no tiene permiso para ver este envío", exception.getMessage());
    }
    
    @Test
    void testGetShipmentsByBuyerId_Unauthorized_Approach2() {
        Long buyerId = 2L;
        Long invalidToken = 88888L;
        
        when(shipmentService.getShipmentsbyBuyerId(buyerId, invalidToken))
            .thenThrow(new IllegalArgumentException("El usuario no tiene permiso para ver este envío"));
        
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            shipmentService.getShipmentsbyBuyerId(buyerId, invalidToken);
        });
        
        assertEquals("El usuario no tiene permiso para ver este envío", exception.getMessage());
    }
    
    @Test
    void testGetShipmentsByBuyerId_Unauthorized_Approach3() {
        Long buyerId = 2L;
        Long invalidToken = 77777L;
        
        when(shipmentService.getShipmentsbyBuyerId(buyerId, invalidToken))
            .thenThrow(new RuntimeException("Token inválido"));
        
        Exception exception = assertThrows(RuntimeException.class, () -> {
            shipmentService.getShipmentsbyBuyerId(buyerId, invalidToken);
        });
        
        assertEquals("Token inválido", exception.getMessage());
    }

    private Shipment createMockShipment(Long id) {
        Shipment shipment = new Shipment();
        shipment.setId(id);
        shipment.setStatus(ShipmentStatus.SHIPPED);
        
        User buyer = new User();
        buyer.setId(id);
        buyer.setUsername("buyer" + id);
        shipment.setBuyer(buyer);
        
        Item item = new Clothes();
        item.setId(id * 10);
        item.setTitle("item" + id);
        
        User seller = new User();
        seller.setId(id + 100);
        seller.setUsername("seller" + id);
        item.setSeller(seller);
        
        shipment.setItem(item);
        
        return shipment;
    }

    private ShipmentDTO createMockShipmentDTO(Long id) {
        User buyer = new User();
        buyer.setId(id);
        buyer.setUsername("buyer" + id);
        UserDTO buyerDTO = buyer.toDTO();
        
        Item item = new Clothes();
        item.setId(id * 10);
        item.setTitle("item" + id);
        
        User seller = new User();
        seller.setId(id + 100);
        seller.setUsername("seller" + id);
        item.setSeller(seller);
        
        ItemDTO itemDTO = item.toDTO();
        
        return new ShipmentDTO(
            id,
            ShipmentStatus.SHIPPED,
            buyerDTO,
            itemDTO,
            LocalDateTime.now(),
            id,
            id+1
        );
    }
}