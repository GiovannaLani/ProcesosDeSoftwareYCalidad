package com.spq.vinted.model;

import java.util.ArrayList;
import java.util.List;

import com.spq.vinted.dto.UserDTO;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

@Entity
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    
    @Column(unique = true, nullable = false)
    private String email;
    
    @Column(nullable = false)
    private String password;

    @ManyToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE}, fetch = FetchType.LAZY)
    private List<Item> cartItems;

    @Column(unique = true, nullable = false)
    private String username;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String surname;

    @Column
    private String description;

    @Column
    private String profileImage;

    @OneToMany(mappedBy = "seller", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Item> itemsForSale = new ArrayList<>();
    

    public User() {
    }
    public User(String email, String password, String username, String name, String surname) {
        this.email = email;
        this.password = password;
        this.username = username;
        this.name = name;
        this.surname = surname;
        this.cartItems = new ArrayList<>();
    }
    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    public String getEmail() {
        return email;
    }
    public void setEmail(String email) {
        this.email = email;
    }
    public String getPassword() {
        return password;
    }
    public void setPassword(String password) {
        this.password = password;
    }
    public List<Item> getCartItems() {
        return cartItems;
    }
    public void setCartItems(List<Item> cartItems) {
        this.cartItems = cartItems;
    }

    public void addItemToCart(Item item) { //editar
        this.cartItems.add(item);
    }
    public String getUsername() {
        return username;
    }
    public void setUsername(String username) {
        this.username = username;
    }
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public String getSurname() {
        return surname;
    }
    public void setSurname(String surname) {
        this.surname = surname;
    }
    public String getDescription() {
        return description;
    }
    public void setDescription(String description) {
        this.description = description;
    }
    public String getProfileImage() {
        return profileImage;
    }
    public void setProfileImage(String profileImage) {
        this.profileImage = profileImage;
    }

    public UserDTO toDTO() {
        return new UserDTO(id, username, name, surname, description, profileImage);
    }
    public List<Item> getItemsForSale() {
        return itemsForSale;
    }
    public void setItemsForSale(List<Item> itemsForSale) {
        this.itemsForSale = itemsForSale;
    }
}
