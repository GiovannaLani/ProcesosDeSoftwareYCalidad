package com.spq.vinted.model;

import java.util.ArrayList;
import java.util.List;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;

import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;

import jakarta.persistence.ManyToOne;

import jakarta.persistence.OneToOne;

@Entity
public class Shipment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(optional = false, cascade = CascadeType.ALL)
    @JoinColumn(name = "item_id")
    private Item item;

    @ManyToOne(optional = false)
    @JoinColumn(name = "buyer_id")
    private User buyer;

    @Column(nullable = false)
    private ShipmentStatus status;

    public Shipment() {
    }

    public Shipment(Item item, User buyer) {    
        this.item = item;
        this.buyer = buyer;
        this.status = ShipmentStatus.SHIPPED;
    }

    public Long getId() { 
        return id; 
    }

    public void setId(Long id) { 
        this.id = id; 
    }
    
    public Item getItem() { 
        return item; 
    }

    public void setItem(Item item) { 
        this.item = item; 
    }

    public User getBuyer() { 
        return buyer; 
    }

    public void setBuyer(User buyer) { 
        this.buyer = buyer; 
    }

    public ShipmentStatus getStatus() { 
        return status; 
    }

    public void setStatus(ShipmentStatus status) { 
        this.status = status; 
    }
    


}
