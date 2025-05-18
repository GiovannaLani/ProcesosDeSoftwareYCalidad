package com.spq.vinted.model;
import org.junit.jupiter.api.Test;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Transient;

import java.lang.reflect.Field;
import static org.junit.jupiter.api.Assertions.*;

class AdTest {

    @Test
    void testDefaultConstructor() {
        Ad ad = new Ad();
        assertNotNull(ad, "El constructor por defecto debe crear una instancia");
        assertEquals(0, ad.getId());
        assertNull(ad.getTitle());
        assertNull(ad.getDescription());
        assertNull(ad.getImageUrl());
    }

    @Test
    void testParameterizedConstructors() {

        Ad ad1 = new Ad("Vendo Bicicleta", "Bicicleta de montaña casi nueva", "bici.jpg");
        assertEquals("Vendo Bicicleta", ad1.getTitle());
        assertEquals("Bicicleta de montaña casi nueva", ad1.getDescription());
        assertEquals("bici.jpg", ad1.getImageUrl());

        Ad ad2 = new Ad("Alquilo Piso", "Piso céntrico con 3 habitaciones");
        assertEquals("Alquilo Piso", ad2.getTitle());
        assertEquals("Piso céntrico con 3 habitaciones", ad2.getDescription());
        assertNull(ad2.getImageUrl());
    }

    @Test
    void testGettersAndSetters() {
        Ad ad = new Ad();
        
        ad.setId(123L);
        assertEquals(123L, ad.getId());

        ad.setTitle("Nuevo Título");
        assertEquals("Nuevo Título", ad.getTitle());

        ad.setDescription("Nueva Descripción");
        assertEquals("Nueva Descripción", ad.getDescription());

        ad.setImageUrl("nueva_imagen.png");
        assertEquals("nueva_imagen.png", ad.getImageUrl());
    }

    @Test
    void testJpaAnnotations() throws NoSuchFieldException {

        assertTrue(Ad.class.isAnnotationPresent(Entity.class), "La clase debe tener @Entity");

        Field idField = Ad.class.getDeclaredField("id");
        assertTrue(idField.isAnnotationPresent(Id.class), "El campo id debe tener @Id");
        
        GeneratedValue generatedValue = idField.getAnnotation(GeneratedValue.class);
        assertNotNull(generatedValue, "El campo id debe tener @GeneratedValue");
        assertEquals(GenerationType.IDENTITY, generatedValue.strategy());

        Field titleField = Ad.class.getDeclaredField("title");
        assertFalse(titleField.isAnnotationPresent(Transient.class), "El campo title debe ser persistente");

        Field descriptionField = Ad.class.getDeclaredField("description");
        assertFalse(descriptionField.isAnnotationPresent(Transient.class), "El campo description debe ser persistente");

        Field imageUrlField = Ad.class.getDeclaredField("imageUrl");
        assertFalse(imageUrlField.isAnnotationPresent(Transient.class), "El campo imageUrl debe ser persistente");
    }
}
