package com.naicson.yugioh.repository;


import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.naicson.yugioh.data.dto.RelUserDeckDTO;
import com.naicson.yugioh.entity.Deck;
import com.naicson.yugioh.entity.sets.UserDeck;

@Repository
public interface DeckRepository extends JpaRepository<Deck, Long> {


	List<Deck> findTop30ByNomeContaining(String nomeDeck);
	
	Page<Deck> findAllBySetType(String setType, Pageable pageable);
	
	List<Deck> findAllByIdIn(Long[] arraySetsIds);
	
	@Query(value = "Select * from tab_user_deck where user_id = :userId", countQuery = "SELECT count(*) FROM yugioh.tab_cards", nativeQuery = true)
	Page<UserDeck> listDeckUser(Pageable page, Long userId);
	
	@Query(value = " SELECT DK.id, DK.user_id, KONAMI_DECK_COPIED AS deck_id, COUNT(KONAMI_DECK_COPIED) AS qtd " +
				   " FROM tab_user_deck DK " +
				   " WHERE USER_ID = :userId and KONAMI_DECK_COPIED IN (:deckId) " +
				   " GROUP BY (KONAMI_DECK_COPIED) ", nativeQuery = true)	
	List<RelUserDeckDTO> searchForDecksUserHave(Long userId, String deckId);
	
	
}
