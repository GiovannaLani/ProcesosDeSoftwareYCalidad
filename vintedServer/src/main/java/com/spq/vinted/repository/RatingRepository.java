package com.spq.vinted.repository;

import com.spq.vinted.model.Rating;
import com.spq.vinted.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RatingRepository extends JpaRepository<Rating, Long> {
    List<Rating> findByRatedUserId(Long ratedUserId);
}