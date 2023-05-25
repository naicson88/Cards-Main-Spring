package com.naicson.yugioh.repository;

import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.naicson.yugioh.entity.RelDeckCards;

@Repository
public interface RelDeckCardsRepository extends JpaRepository<RelDeckCards, Long>{
	
	@Query(value = " select * from tab_rel_deck_cards rel "
			+ " inner join tab_decks deck on deck.id = rel.deck_id "
			+ " inner join tab_cards card on card.numero = rel.card_numero "
			+ " where card_numero = :cardNumber and deck.is_konami_deck = 'S'",			
			nativeQuery = true)
	List<RelDeckCards> findCardByNumberAndIsKonamiDeck(Long cardNumber);

	List<RelDeckCards> findByDeckId(Long deckId);
	
	List<RelDeckCards> findByDeckIdAndCardNumber(Long deckId, Long cardNumero);

	List<RelDeckCards> findByCardNumber(Long cardNumber);
	 
	List<RelDeckCards> findByCardSetCode(String card_set_code);
	 
	@Query(value = "SELECT * FROM yugioh.tab_rel_deck_cards where card_id = :cardId", nativeQuery = true)
	List<RelDeckCards> findByCardId(Integer cardId);
	
	@Modifying
	@Query(value = "insert into tab_rel_deckusers_cards ("
			+ " deck_id, card_numero, card_raridade, card_set_code, card_price, dt_criacao, is_side_deck, card_id, is_speed_duel, quantity) "
			+ " values (:deckId, :cardNumber, :card_raridade, :card_set_code, :card_price, :dt_criacao, :isSideDeck, :cardId, :isSpeedDuel, :quantity)" , nativeQuery = true)
	void saveRelUserDeckCards(Long deckId, Long cardNumber, String card_raridade, String card_set_code,
			Double card_price, Date dt_criacao, Boolean isSideDeck, Integer cardId, Boolean isSpeedDuel,Integer quantity);

	Optional<List<RelDeckCards>> findByCardSetCodeLike(String setCode);
}
