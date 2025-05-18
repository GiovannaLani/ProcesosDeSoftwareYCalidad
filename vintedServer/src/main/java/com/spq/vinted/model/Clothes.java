/**
 * @file Clothes.java
 * @brief Clase que representa una prenda de ropa. 
 */
package com.spq.vinted.model;

import com.spq.vinted.dto.ClothesDTO;
import com.spq.vinted.dto.ItemDTO;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;


/**
 * @class Clothes
 * @brief Clase que representa una prenda de ropa.
 * 
 * Esta clase hereda de la clase Item y representa una prenda de ropa con su tamaño, tipo y categoría.
 */
@Entity
@Table(name = "clothes")
public class Clothes extends Item {
    

    @Column(nullable = false)
    private ClothesSize size;
    @Column(nullable = false)
    private ClothesType clothesType;
    @Column(nullable = false)
    private Category category;

    /**
     * @brief Constructor por defecto de la clase Clothes.
     * 
     * Este constructor es necesario para la creación de objetos de la clase Clothes.
     */
    public Clothes() {
    }

    /**
     * @brief Constructor de la clase Clothes.
     * 
     * @param title Título del anuncio.
     * @param description Descripción del anuncio.
     * @param price Precio del anuncio.
     * @param size Tamaño de la prenda.
     * @param type Tipo de prenda.
     * @param category Categoría de la prenda.
     * @param seller Vendedor del anuncio.
     */
    public Clothes(String title, String description, float price, ClothesSize size, ClothesType type, Category category, User seller) {
        super(title, description, price, seller);
        this.size = size;
        this.clothesType = type;
        this.category = category;
    }

    /**
     * @brief Getter de la talla de una prenda de ropa.
     * 
     *  @return Talla de prenda de ropa.
     */
    public ClothesSize getSize() {
        return size;
    }

    /**
     * @brief Setter de la talla de una prenda de ropa.
     * 
     * @param size Talla de la prenda de ropa.
     */
    public void setSize(ClothesSize size) {
        this.size = size;
    }

    /**
     * @brief Getter del tipo de prenda de ropa.
     * 
     * @return Tipo de prenda de ropa.
     */
    public ClothesType getClothesType() {
        return clothesType;
    }

    /**
     * @brief Setter del tipo de prenda de ropa.
     * 
     * @param type Tipo de prenda de ropa.
     */
    public void setClothesType(ClothesType type) {
        this.clothesType = type;
    }

    /**
     * @brief Getter de la categoría de una prenda de ropa.
     * 
     * @return Categoría de prenda de ropa.
     */
    public Category getCategory() {
        return category;
    }

    /**
     * @brief Setter de la categoría de una prenda de ropa.
     * 
     * @param category Categoría de prenda de ropa.
     */
    public void setCategory(Category category) {
        this.category = category;
    }

    /**
     * @brief Método para convertir un objeto Clothes a un objeto ClothesDTO.
     * 
     * @return Objeto ClothesDTO convertido.
     */
    @Override
    public ItemDTO toDTO() {
        return new ClothesDTO(getId(), getTitle(), getDescription(), getPrice(), getSize(), getClothesType(), getCategory(), getImages(), getIsSold());
    }
}