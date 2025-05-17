package com.spq.vinted.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.spq.vinted.model.Ad;

@Repository
public interface AdRepository extends JpaRepository<Ad, Long>{
    
}
