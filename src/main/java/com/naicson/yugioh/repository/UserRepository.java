package com.naicson.yugioh.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.naicson.yugioh.entity.auth.User;

@Repository
public interface UserRepository extends JpaRepository<User, Integer> {

	Optional<User> findByUserName(String username);

	boolean existsByUserName(String username);

	Boolean existsByEmail(String email);
	
	Optional<User> findByVerificationToken(String verificationToken);

	Optional<User> findByEmail(String email);

}
