package com.naicson.yugioh.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.naicson.yugioh.controller.UserRelDeckCards;

@Repository
public interface UserRelDeckCardsRepository extends JpaRepository<UserRelDeckCards, Long> {
	
}
