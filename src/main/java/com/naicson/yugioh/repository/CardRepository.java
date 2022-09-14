 package com.naicson.yugioh.repository;

import java.util.List;

import javax.persistence.Tuple;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.naicson.yugioh.entity.Card;


@Repository
public interface CardRepository extends JpaRepository<Card, Integer>, JpaSpecificationExecutor<Card> {
	
	List<Card> findAll();
	
	Page<Card> findAll(Pageable pageable);
	
	Card save (Card card);
	
	Card findByNumero(Long numero);
	
	void delete (Card card);
	
	Card findByNumero(String numero);
	
	@Query(value = "SELECT * FROM tab_cards WHERE COD_ARCHETYPE = :archId",  nativeQuery = true)	
	List<Card> findByArchetype(Integer archId);
	
	@Query(value = "SELECT * FROM tab_cards ORDER BY RAND() LIMIT 30",  nativeQuery = true)
	List<Card> findRandomCards();
	
	@Query(value = "SELECT * FROM tab_cards cards "
			+ " inner join tab_rel_deck_cards rcards on rcards.card_numero = cards.numero "
			+ " inner join tab_decks deck on deck.id = rcards.deck_id "
			+ " where deck.user_id = :userId and cards.generic_type = :type ",
			nativeQuery = true)
	Page<Card> findCardsByTypeAndUser(String type, int userId, Pageable page);
	
	@Query(value = " SELECT DISTINCT * FROM yugioh.tab_cards CARDS "
			+ " INNER JOIN TAB_REL_DECKUSERS_CARDS UCARDS ON UCARDS.CARD_NUMERO = CARDS.NUMERO "
			+ " INNER JOIN tab_user_deck DUSERS ON DUSERS.ID = UCARDS.DECK_ID "
			+ " WHERE CARDS.GENERIC_TYPE = :genericType AND DUSERS.USER_ID = :userId "
			+ " GROUP BY CARDS.NUMERO ",
			countQuery = "SELECT count(*) FROM tab_cards",
			nativeQuery=true)
	Page<Card> getByGenericType (Pageable page, String genericType, long userId);
	
	@Query(value = " SELECT DISTINCT * FROM yugioh.tab_cards CARDS "
			+ " INNER JOIN TAB_REL_DECKUSERS_CARDS UCARDS ON UCARDS.CARD_NUMERO = CARDS.NUMERO "
			+ " INNER JOIN tab_user_deck DUSERS ON DUSERS.ID = UCARDS.DECK_ID "
			+ " WHERE CARDS.nome like CONCAT('%',:cardName,'%') AND DUSERS.USER_ID = :userId "
			+ " GROUP BY CARDS.NUMERO ",
			countQuery = "SELECT count(*) FROM tab_cards",
			nativeQuery=true)
	Page<Card> cardSearchByNameUserCollection(String cardName, long userId, Pageable pageable);
	
	@Query(value = "select card_alternative_number from tab_card_alternative_numbers where card_alternative_number in :cardsNumber", nativeQuery = true)
	List<Long> findAllCardsByListOfCardNumbers(List<Long> cardsNumber);

	Card findByNome(String nome);
	
	@Query(value = " SELECT count(rel.card_set_code) as total, CONCAT(decks.nome, ' (', rel.card_set_code,')' ) AS card_set "
	+ " FROM yugioh.tab_rel_deckusers_cards as rel "
	+ " inner join tab_rel_deck_cards rdc on rdc.card_set_code = rel.card_set_code"
	+ " inner join tab_decks decks on decks.id = rdc.deck_id "
	+ " inner join tab_user_deck du on du.id = rel.deck_id "
	+ " where du.user_id = :userId "
	+ " and  rel.card_numero in (select card_alternative_number from tab_card_alternative_numbers where card_id = :cardId) "
	+ " group by rel.card_set_code, decks.nome", nativeQuery = true)
	List<Tuple> findQtdUserHaveByKonamiCollection(Integer cardId, long userId);
	
	@Query(value = " SELECT count(rel.card_set_code) as total, CONCAT(du.nome, ' (', rel.card_set_code,')' ) AS card_set "
	+ " FROM yugioh.tab_rel_deckusers_cards as rel "
	+ " inner join tab_rel_deck_cards rdc on rdc.card_set_code = rel.card_set_code"
	+ " inner join tab_user_deck du on du.id = rel.deck_id "
	+ " where du.user_id = :userId "
	+ " and  rel.card_numero in (select card_alternative_number from tab_card_alternative_numbers where card_id = :cardId) "
	+ " group by rel.card_set_code, du.nome", nativeQuery = true)
	List<Tuple> findQtdUserHaveByUserCollection(Integer cardId, long userId);
	
	@Query(value = " select deck.id, deck.set_type as setType, deck.imgur_url as image, deck.nome as name, rel.card_set_code as cardSetCode, rel.card_raridade as rarity, rel.card_price as price "
	+ " from tab_decks deck "
	+ " inner join tab_rel_deck_cards rel on rel.deck_id = deck.id "
	+ " where card_id = :cardId and deck.set_type = 'DECK' "
	+ " UNION "
	+ " select setcol.id, setcol.set_collection_type as setType, setcol.imgur_url as image, setcol.name, rel.card_set_code as cardSetCode, rel.card_raridade as rarity, rel.card_price  as price "
	+ " from tab_set_collection setcol "
	+ " inner join tab_setcollection_deck scd on scd.set_collection_id = setcol.id "
	+ " inner join tab_decks decks on decks.id = scd.deck_id "
	+ " inner join tab_rel_deck_cards rel on rel.deck_id = decks.id "
	+ " where card_id = :cardId and decks.set_type != 'DECK'", nativeQuery = true)
	List<Tuple> setsOfCard(Integer cardId);
	
	
}
