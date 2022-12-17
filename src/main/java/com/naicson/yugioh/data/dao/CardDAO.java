package com.naicson.yugioh.data.dao;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.Tuple;

import org.springframework.stereotype.Repository;

import com.naicson.yugioh.data.dto.RelUserCardsDTO;
import com.naicson.yugioh.entity.Deck;
import com.naicson.yugioh.entity.sets.SetCollection;

@Repository
public class CardDAO {
	
	@PersistenceContext
	 EntityManager em;
	
	public List<RelUserCardsDTO> searchForCardsUserHave(long userId, String cardsNumbers) {		
		Query query = em.createNativeQuery(" SELECT * FROM tab_rel_user_cards WHERE user_id = :userId and card_numero in (" + cardsNumbers + ")", RelUserCardsDTO.class)
				.setParameter("userId", userId);
		
		return query.getResultList();

	}
	
	public List<Tuple> listCardOfUserDetails(Integer cardId, long userId) {
		Query query = em.createNativeQuery("select du.nome, rel.card_set_code , rel.card_raridade as rarity, "
				+ " rel.card_price as price, sum(quantity) as quantity, du.id, du.set_type "
				+ " from tab_user_deck du  "
				+ " inner join tab_rel_deckusers_cards rel on rel.deck_id = du.id  "
				+ " where rel.card_id = :cardId and du.user_id = :userId and set_type = 'DECK' "
				+ " group by rel.card_set_code, nome "
				+ " union "
				+ " select usc.name , rel.card_set_code , rel.card_raridade as rarity, "
				+ " rel.card_price as price, sum(quantity) as quantity, usc.id, usc.set_collection_type "
				+ " from tab_user_set_collection usc "
				+ " inner join tab_user_setcollection_deck usdeck on usdeck.user_set_collection_id = usc.id "
				+ " inner join tab_user_deck du on du.id = usdeck.deck_id "
				+ " inner join tab_rel_deckusers_cards rel on rel.deck_id = usdeck.deck_id "
				+ " where rel.card_id = :cardId and du.user_id = :userId and set_type != 'DECK' "
				+ " group by rel.card_set_code,nome ", Tuple.class)
				.setParameter("cardId", cardId)
				.setParameter("userId", userId);
		
		
		return query.getResultList();
	}

	public List<Deck> cardDecks(Integer cardId) {
		Query query = em.createNativeQuery("SELECT *  FROM tab_decks deck "
				+ " inner join TAB_REL_DECK_CARDS rel on deck.id = rel.deck_id " 
				+ " where rel.card_id = :cardId ", Deck.class);
		
		return (List<Deck>) query.setParameter("cardId", cardId).getResultList();
	}
	

	public List<SetCollection> cardSetCollection(Integer cardId) {
		Query query = em.createNativeQuery("select * from tab_set_collection sc "
				+ " inner join tab_setcollection_deck scd on scd.set_collection_id = sc.id "
				+ " inner join tab_decks decks on decks.id = scd.deck_id "
				+ " inner join TAB_REL_DECK_CARDS rel on decks.id = rel.deck_id"
				+ " where rel.card_id = :cardId", SetCollection.class);
		
		return (List<SetCollection>) query.setParameter("cardId", cardId).getResultList();
	}
	
	
	
}
