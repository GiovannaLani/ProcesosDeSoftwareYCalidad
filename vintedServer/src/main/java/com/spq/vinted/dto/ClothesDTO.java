/**
 * @file ClothesDTO.java
 * @brief Clase que representa un DTO (Data Transfer Object) para una prenda de ropa.
 */
package com.spq.vinted.dto;

import java.util.List;

import com.spq.vinted.model.Category;
import com.spq.vinted.model.ClothesSize;
import com.spq.vinted.model.ClothesType;

/**
 * @class ClothesDTO
 * @brief Clase que representa un DTO (Data Transfer Object) para una prenda de ropa.
 * 
 * Esta clase hereda de la clase ItemDTO y representa una prenda de ropa con su tamaño, tipo y categoría.
 */
public class ClothesDTO extends ItemDTO {
    
    private ClothesSize size;
    private ClothesType clothesType;
    private Category category;

    /**
     * @brief Constructor por defecto de la clase ClothesDTO.
     * 
     * Este constructor es necesario para la creación de objetos de la clase ClothesDTO.
     */
    public ClothesDTO() {

    }

    /**
     * @brief Constructor de la clase ClothesDTO.
     * 
     * @param id ID del anuncio.
     * @param title Título del anuncio.
     * @param description Descripción del anuncio.
     * @param price Precio del anuncio.
     * @param size Tamaño de la prenda.
     * @param type Tipo de prenda.
     * @param category Categoría de la prenda.
     * @param images Lista de imágenes del anuncio.
     * @param isSold Indica si el anuncio está vendido o no.
     */
    public ClothesDTO(long id, String title, String description, float price, ClothesSize size, ClothesType type, Category category,List<String> images, boolean isSold) {
        super(id, title, description, price,images, isSold); 
        this.size = size;
        this.clothesType = type;
        this.category = category;
    }


    /**
     * @brief Getter de la talla de una prenda de ropa.
     * 
     * @return Talla de la prenda.
     */
    public ClothesSize getSize() {
        return size;
    }

    /**
     * @brief Setter de la talla de una prenda de ropa.
     * 
     * @param size Talla de la prenda.
     */
    public void setSize(ClothesSize size) {
        this.size = size;
    }

    /**
     * @brief Getter del tipo de prenda de ropa.
     * 
     * @return Tipo de prenda.
     */
    public ClothesType getClothesType() {
        return clothesType;
    }
    
    /**
     * @brief Setter del tipo de prenda de ropa.
     * 
     * @param type Tipo de prenda.
     */
    public void setClothesType(ClothesType type) {
        this.clothesType = type;
    }

    /**
     * @brief Getter de la categoría de una prenda de ropa.
     * 
     * @return Categoría de la prenda.
     */
    public Category getCategory() {
        return category;
    }

    /**
     * @brief Setter de la categoría de una prenda de ropa.
     * 
     * @param category Categoría de la prenda.
     */
    public void setCategory(Category category) {
        this.category = category;
    }
}