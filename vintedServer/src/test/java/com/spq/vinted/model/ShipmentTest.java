package com.spq.vinted.model;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;

import static org.junit.jupiter.api.Assertions.*;
import java.lang.reflect.Field;

class ShipmentTest {

    @Test
    void testDefaultConstructor() {
        Shipment shipment = new Shipment();
        assertNotNull(shipment, "El constructor por defecto debe crear una instancia");
        assertNull(shipment.getId());
        assertNull(shipment.getItem());
        assertNull(shipment.getBuyer());
        assertNull(shipment.getStatus());
    }

    @Test
    void testParameterizedConstructor() {
        Item item = new Clothes();
        User buyer = new User();
        Shipment shipment = new Shipment(item, buyer);
        
        assertEquals(item, shipment.getItem());
        assertEquals(buyer, shipment.getBuyer());
        assertEquals(ShipmentStatus.SHIPPED, shipment.getStatus(), "El estado inicial debe ser SHIPPED");
    }

    @Test
    void testGettersAndSetters() {
        Shipment shipment = new Shipment();
        
        shipment.setId(1L);
        assertEquals(1L, shipment.getId());

        Item item = new Clothes();
        shipment.setItem(item);
        assertEquals(item, shipment.getItem());

        User buyer = new User();
        shipment.setBuyer(buyer);
        assertEquals(buyer, shipment.getBuyer());

        shipment.setStatus(ShipmentStatus.DELIVERED);
        assertEquals(ShipmentStatus.DELIVERED, shipment.getStatus());
    }

    @Test
    void testJpaAnnotations() throws NoSuchFieldException {
        Field idField = Shipment.class.getDeclaredField("id");
        assertTrue(idField.isAnnotationPresent(Id.class), "El campo id debe tener @Id");
        assertTrue(idField.isAnnotationPresent(GeneratedValue.class), "El campo id debe tener @GeneratedValue");

        Field itemField = Shipment.class.getDeclaredField("item");
        assertTrue(itemField.isAnnotationPresent(OneToOne.class), "El campo item debe tener @OneToOne");
        OneToOne oneToOneAnnotation = itemField.getAnnotation(OneToOne.class);
        assertFalse(oneToOneAnnotation.optional());
        assertEquals(CascadeType.ALL, oneToOneAnnotation.cascade()[0]);

        Field buyerField = Shipment.class.getDeclaredField("buyer");
        assertTrue(buyerField.isAnnotationPresent(ManyToOne.class), "El campo buyer debe tener @ManyToOne");
        assertFalse(buyerField.getAnnotation(ManyToOne.class).optional());

        Field statusField = Shipment.class.getDeclaredField("status");
        assertTrue(statusField.isAnnotationPresent(Column.class), "El campo status debe tener @Column");
        assertFalse(statusField.getAnnotation(Column.class).nullable());
    }
}
