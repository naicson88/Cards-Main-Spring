package com.naicson.yugioh.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.naicson.yugioh.entity.UserRelDeckCards;

@Repository
public interface UserRelDeckCardsRepository extends JpaRepository<UserRelDeckCards, Long> {

	void deleteByDeckId(Long id);
	
	@Modifying
	@Query(value = "delete from tab_rel_deckusers_cards where deck_id = :deckId", nativeQuery = true)
	void deleteRelUserDeckByDeckId(Long deckId);
	
	
}
