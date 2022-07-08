package com.naicson.yugioh.data.dao;

import java.lang.annotation.Native;
import java.sql.SQLException;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.Tuple;

import org.springframework.stereotype.Repository;

import com.naicson.yugioh.data.dto.RelUserCardsDTO;
import com.naicson.yugioh.data.dto.cards.KonamiSetsWithCardDTO;
import com.naicson.yugioh.entity.Deck;
import com.naicson.yugioh.entity.sets.SetCollection;

@Repository
public class CardDAO {
	
	@PersistenceContext
	 EntityManager em;
	
	public List<RelUserCardsDTO> searchForCardsUserHave(long userId, String cardsNumbers) {		
		Query query = em.createNativeQuery(" SELECT * FROM tab_rel_user_cards WHERE user_id = :userId and card_numero in (" + cardsNumbers + ")", RelUserCardsDTO.class)
				.setParameter("userId", userId);
		
		@SuppressWarnings("unchecked")
		List<RelUserCardsDTO> relList = query.getResultList();
		
		return relList;
	}
	
	public List<Tuple> listCardOfUserDetails(Long cardNumber, long userId) {
		Query query = em.createNativeQuery("select du.nome , rel.card_set_code , rel.card_raridade as rarity,\r\n"
			+ " rel.card_price as price, count(rel.card_set_code) as quantity\r\n"
			+ "from tab_user_deck du \r\n"
			+ "inner join tab_rel_deckusers_cards rel on rel.deck_id = du.id \r\n"
			+ "where rel.card_numero = :cardNumber and du.user_id = :userId", Tuple.class)
				.setParameter("cardNumber", cardNumber)
				.setParameter("userId", userId);
		
		@SuppressWarnings("unchecked")
		List<Tuple> resultCards = query.getResultList();
		
		return resultCards;
	}
	
	public List<Deck> cardDecks(Integer cardId) {
		Query query = em.createNativeQuery("SELECT *  FROM tab_decks deck "
				+ " inner join TAB_REL_DECK_CARDS rel on deck.id = rel.deck_id " 
				+ " where rel.card_id = :cardId ", Deck.class);
		
		List<Deck> decks_set = (List<Deck>) query.setParameter("cardId", cardId).getResultList();
		
		return decks_set;
	}
	

	public List<SetCollection> cardSetCollection(Integer cardId) {
		Query query = em.createNativeQuery("select * from tab_set_collection sc "
				+ " inner join tab_setcollection_deck scd on scd.set_collection_id = sc.id "
				+ " inner join tab_decks decks on decks.id = scd.deck_id "
				+ " inner join TAB_REL_DECK_CARDS rel on decks.id = rel.deck_id"
				+ " where rel.card_id = :cardId", SetCollection.class);
		
		List<SetCollection> decks_set = (List<SetCollection>) query.setParameter("cardId", cardId).getResultList();
		
		return decks_set;
	}
	
}
