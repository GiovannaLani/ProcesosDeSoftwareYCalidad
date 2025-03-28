package com.spq.vinted.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.spq.vinted.model.User;

@Repository
public interface UserRepository extends JpaRepository<User, String>{
	Optional<User> findByEmail(String email);
	Optional<User> findByUsername(String username);
}