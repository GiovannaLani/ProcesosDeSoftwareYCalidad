package com.spq.vinted.repository;

import org.springframework.stereotype.Repository;

import com.spq.vinted.model.Category;
import com.spq.vinted.model.Clothes;
import com.spq.vinted.model.Item;
import com.spq.vinted.model.User;

import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;


@Repository
public interface ItemRepository extends JpaRepository<Item, Long> {
    List<Item> findBySellerIdNot(Long userId);
    List<Item> findBySeller(User seller);
    Page<Item> findBySellerIdNotAndIsSoldFalse(Long sellerId, Pageable pageable);
    Page<Item> findByIsSoldFalse(Pageable pageable);
    
    @Query("SELECT i FROM Item i WHERE TYPE(i) = :type AND i.isSold = false")
    Page<Item> findByType(@Param("type") Class<? extends Item> type, Pageable pageable);
    @Query("SELECT i FROM Item i WHERE i.seller.id <> :sellerId AND TYPE(i) = :type AND i.isSold = false")
    Page<Item> findBySellerIdNotAndType(@Param("sellerId") Long sellerId, @Param("type") Class<? extends Item> type, Pageable pageable);
    
    @Query("SELECT c FROM Clothes c WHERE c.category = :category AND c.isSold = false")
    Page<Clothes> findClothesByCategory(@Param("category") Category category, Pageable pageable);
    @Query(" SELECT c FROM Clothes c WHERE c.seller.id <> :sellerId AND c.category = :category AND c.isSold = false")
    Page<Clothes> findClothesByCategoryAndSellerIdNot(@Param("sellerId") Long sellerId, @Param("category") Category category, Pageable pageable);
    
    @Query("SELECT i FROM Item i WHERE TYPE(i) = :type AND LOWER(i.title) LIKE LOWER(CONCAT('%', :query, '%')) AND i.isSold = false")
    Page<Item> searchByTypeAndQuery(@Param("type") Class<? extends Item> type, @Param("query") String query, Pageable pageable);
    @Query("SELECT i FROM Item i WHERE LOWER(i.title) LIKE LOWER(CONCAT('%', :query, '%'))")
    Page<Item> searchByQuery(@Param("query") String query, Pageable pageable);
}