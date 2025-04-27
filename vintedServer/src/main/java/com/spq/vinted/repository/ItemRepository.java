package com.spq.vinted.repository;

import org.springframework.stereotype.Repository;

import com.spq.vinted.model.Item;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;


@Repository
public interface ItemRepository extends JpaRepository<Item, Long> {
    List<Item> findBySellerIdNot(Long userId);
}
