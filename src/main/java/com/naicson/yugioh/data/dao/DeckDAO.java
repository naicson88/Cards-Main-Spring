package com.naicson.yugioh.data.dao;

import java.math.BigInteger;
import java.util.Date;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.naicson.yugioh.data.bridge.source.set.RelDeckCardsRelationBySource;
import com.naicson.yugioh.data.dto.RelUserCardsDTO;
import com.naicson.yugioh.data.dto.set.DeckDTO;
import com.naicson.yugioh.entity.Card;
import com.naicson.yugioh.entity.Deck;
import com.naicson.yugioh.entity.RelDeckCards;
import com.naicson.yugioh.repository.DeckRepository;

@Repository
@Transactional
public class DeckDAO implements RelDeckCardsRelationBySource{
	
	 @PersistenceContext
	 EntityManager em;
	 
	 @Autowired
	 DeckRepository deckRepository;
	 
	 Logger logger = LoggerFactory.getLogger(DeckDAO.class);
	 
	 
	public DeckDAO() {
		
	}
	 
	 public DeckDAO(EntityManager em) {
		 this.em = em;
		
	 }

	public int addDeckToUserCollection(Long originalDeckId, long userId) {
		Query query = em.createNativeQuery("INSERT INTO TAB_REL_USER_DECK (user_id, deck_id, qtd) values(:user_id, :deck_id, 1)")
				.setParameter("user_id", userId)
				.setParameter("deck_id", originalDeckId);
		
		BigInteger inserted = (BigInteger) query.getSingleResult();
		 return (int) inserted.longValue();		
		
	}
	
	public Long addDeck(Deck deck) {
		
		if(deck == null) 
			throw new IllegalArgumentException("Deck is null.");		
		
			Deck d = deckRepository.save(deck);
			deckRepository.flush();
			
			return d.getId();
			
	}
	
	public List<DeckDTO> relationDeckAndCards(Long originalDeckId) {
		
		Query query = em.createNativeQuery("SELECT * FROM tab_rel_deck_cards WHERE DECK_ID = :deckId", DeckDTO.class)
				.setParameter("deckId", originalDeckId);
		
		return (List<DeckDTO>) query.setParameter("deckId", originalDeckId).getResultList();		
	}
	
	public boolean verifyIfUserAleadyHasTheCard(long userId, String cardSetCode) {
		
		Query query = em.createNativeQuery(" SELECT count(*) FROM tab_rel_user_cards WHERE CARD_SET_CODE = :cardSetCode AND USER_ID = :userId ")
				.setParameter("cardSetCode", cardSetCode)
				.setParameter("userId", userId);
		
		return ((Number) query.getSingleResult()).intValue() > 0 ? true : false;
	}
	
	public int verifyIfUserAleadyHasTheDeck(Long originalDeckId, long userId) {

		Query query = em.createNativeQuery(" SELECT qtd FROM tab_rel_user_deck WHERE deck_id = :deckId AND USER_ID = :userId ")
				.setParameter("userId", userId)
				.setParameter("deckId", originalDeckId);
		
		try {
			//Quando ele não acha nada, acusa um exception, este try catch é só pra ignorar. 
			return ((Number) query.getSingleResult()).intValue();
		} catch(NoResultException e) {
			return 0;
		}
			
	}
	
	public int changeQuantityOfEspecifCardUserHas(long userId, String cardSetCode, String flagAddOrRemove){
		
		int changed;
		
		if(flagAddOrRemove.equals("A")) {
			Query query = em.createNativeQuery(" UPDATE tab_rel_user_cards set qtd = qtd + 1 WHERE CARD_SET_CODE = :cardSetCode AND USER_ID = :userId ")
					.setParameter("cardSetCode", cardSetCode)
					.setParameter("userId", userId);
			
			changed = query.executeUpdate();
			
		} else {		
			Query query = em.createNativeQuery(" UPDATE tab_rel_user_cards set qtd = qtd - 1 WHERE CARD_SET_CODE = :cardSetCode AND USER_ID = :userId ")
					.setParameter("cardSetCode", cardSetCode)
					.setParameter("userId", userId);
			
			changed = query.executeUpdate();
		}
		
		return changed;
		
	}
	
	public int insertCardToUserCollection(RelUserCardsDTO rel){
		
		Query query = em.createNativeQuery(" INSERT INTO tab_rel_user_cards (user_id, card_numero, card_set_code, qtd, dt_criacao)"
				+ " VALUES (:userId, :card_number, :card_set_code, :qtd, :dt_criacao) ")
				.setParameter("userId", rel.getUserId())
				.setParameter("card_number", rel.getCardNumero())
				.setParameter("card_set_code", rel.getCardSetCode())
				.setParameter("qtd", rel.getQtd())
				.setParameter("dt_criacao", rel.getDtCriacao());
		
		return query.executeUpdate();
	}

	public int changeQuantitySpecificDeckUserHas(Long originalDeckId, long userId, String flagAddOrRemove) {
	
		int changed;
		
		if(flagAddOrRemove.equals("A")) {
			Query query = em.createNativeQuery(" UPDATE tab_rel_user_deck set qtd = qtd + 1 WHERE deck_id = :deckId AND USER_ID = :userId ")
					.setParameter("deckId", originalDeckId)
					.setParameter("userId", userId);
			
			changed = query.executeUpdate();
			
		} else {		
			Query query = em.createNativeQuery(" UPDATE tab_rel_user_deck set qtd = qtd - 1 WHERE deck_id = :deckId AND USER_ID = :userId ")
					.setParameter("deckId", originalDeckId)
					.setParameter("userId", userId);
			
			changed = query.executeUpdate();
		}
		
		return changed;
	}

	public int addCardsToDeck(Long originalDeckId, Long generatedDeckId) {
		int result = 0;
	
		if(originalDeckId != null && generatedDeckId != null) {
			Query query = em.createNativeQuery(" INSERT INTO tab_rel_deckusers_cards (DECK_ID, CARD_NUMERO,CARD_RARIDADE, set_rarity_code,rarity_details, CARD_SET_CODE,CARD_PRICE, DT_CRIACAO, is_side_deck, CARD_ID, IS_SPEED_DUEL, QUANTITY) "+
											  " SELECT " + generatedDeckId + " , CARD_NUMERO,CARD_RARIDADE, set_rarity_code, rarity_details, CARD_SET_CODE,CARD_PRICE, CURDATE(),0, CARD_ID, IS_SPEED_DUEL, QUANTITY  FROM TAB_REL_DECK_CARDS " +
											  " where deck_id = " + originalDeckId  );
			
			 result = query.executeUpdate();
		}
		
		return result;
	}

	
	public int removeCardsFromUserSet(Long setId) {
			
		if(setId == null || setId == 0)
			throw new IllegalArgumentException("Set id was not informed.");
				
		Query query = em.createNativeQuery("DELETE FROM tab_rel_deckusers_cards WHERE deck_id = :setId")
				.setParameter("setId", setId);	
		
		int removed = query.executeUpdate();
		
		return removed;
	}
	
	// Traz informações completas dos cards contidos num deck
	public List<Card> cardsOfDeck(Long deckId, String table) {
		Query query = em.createNativeQuery("SELECT * FROM TAB_CARDS WHERE ID IN "
				+ "(SELECT CARD_ID FROM "+ table + " WHERE DECK_ID = :deckId) " + "order by case "
				+ "when categoria LIKE 'link monster' then 1 " + "when categoria like 'XYZ Monster' then 2 "
				+ "when categoria like 'Fusion Monster' then 3 " + "when categoria like '%Synchro%' then 4 "
				+ "when categoria LIKE '%monster%' then 5 " + "when categoria = 'Spell Card' then 6 "
				+ "ELSE    7 " + " END ", Card.class);
		List<Card> cards = (List<Card>) query.setParameter("deckId", deckId).getResultList();
		return cards;
	}

	public List<Card> consultMainDeck(Long deckId) {
		
		 Query query = em.createNativeQuery(
				  " SELECT * FROM TAB_CARDS CARDS "
			    + " INNER JOIN tab_rel_deckusers_cards rel on rel.card_id = cards.id "	 
				+ " WHERE cards.id IN "
				+ " (SELECT card_id FROM tab_rel_deckusers_cards WHERE DECK_ID = :deckId and is_side_deck != 1 "
				+ " AND CARDS.GENERIC_TYPE NOT IN ('XYZ', 'SYNCHRO', 'FUSION','LINK')) "
				+ " and deck_id = :deckId  and is_side_deck = 0 order by cards.nome ", Card.class);
		
		List<Card> cards = (List<Card>) query.setParameter("deckId", deckId).getResultList();
		
		return cards;
	}
	
	public List<Card> consultSideDeckCards(Long deckId, String userOrKonamiDeck) {
		
		if(deckId == null || deckId == 0)
			throw new IllegalArgumentException("Invalid Deck ID");
		
		Query query = em.createNativeQuery(" SELECT * FROM TAB_CARDS cards "
					+ " INNER JOIN tab_rel_deckusers_cards rel on rel.card_id = cards.id "	 
					+ " WHERE cards.id IN "
					+ " (SELECT card_id FROM tab_rel_deckusers_cards WHERE DECK_ID = :deckId and is_side_deck = 1) and deck_id = :deckId  and is_side_deck = 1", Card.class);
		
		List<Card> cards = (List<Card>) query.setParameter("deckId", deckId).getResultList();
		
		return cards;
	}

	public List<Card> consultExtraDeckCards(Long deckId, String userOrKonamiDeck) {
		
		if(!userOrKonamiDeck.equals("User"))
			throw new IllegalArgumentException("Type of deck not informed");
		
		Query query = em.createNativeQuery(
			  " SELECT * FROM TAB_CARDS CARDS "
			+ " INNER JOIN tab_rel_deckusers_cards rel on rel.card_id = cards.id"	 
			+ " WHERE cards.id IN "
			+ " (SELECT card_id FROM tab_rel_deckusers_cards WHERE DECK_ID = :deckId and (is_side_deck = 0 or is_side_deck is null)) "
			+ " AND CARDS.GENERIC_TYPE IN ('XYZ', 'SYNCHRO', 'FUSION', 'LINK') and deck_id = :deckId  and is_side_deck = 0 order by cards.generic_type", Card.class);
	
		List<Card> cards = (List<Card>) query.setParameter("deckId", deckId).getResultList();
		
		return cards;
	}

	@Override
	public List<RelDeckCards> findRelationByDeckId(Long deckUserId) {
		Query query = em.createNativeQuery("select * from tab_rel_deckusers_cards where deck_id = :deckUserId", RelDeckCards.class);
			
		List<RelDeckCards> relation = (List<RelDeckCards>) query.setParameter("deckUserId", deckUserId).getResultList();
		
		return relation;
	}
	
	public void deleteCardsDeckuserByDeckId(Long deckUserId) {
		Query query = em.createNativeQuery("delete from tab_rel_deckusers_cards where deck_id = :deckUserId");
		query.setParameter("deckUserId", deckUserId);
		query.executeUpdate();
	}

	public int saveRelDeckUserCard(RelDeckCards rel, Long deckId) {
		int id = 0;
			
			Query query = em.createNativeQuery("insert into tab_rel_deckusers_cards (deck_id, card_numero, card_raridade, card_set_code, card_price, dt_criacao, is_side_deck, card_id, is_speed_duel, quantity )" 
					+ " values (:deck_id,:card_numero, :card_raridade, :card_set_code, :card_price, :dt_criacao, :is_side_deck, :card_id, :is_speed_duel, :quantity )")
			.setParameter("deck_id", deckId)
			.setParameter("card_numero", rel.getCardNumber())
			.setParameter("card_raridade", rel.getCard_raridade())
			.setParameter("card_set_code", rel.getCard_set_code())
			.setParameter("card_price", rel.getCard_price())
			.setParameter("dt_criacao", new Date())
			.setParameter("is_side_deck", rel.getIsSideDeck())
			.setParameter("card_id", rel.getCardId())
			.setParameter("is_speed_duel", rel.getIsSpeedDuel())
			.setParameter("quantity", rel.getQuantity());
				
			 id = query.executeUpdate();	
			 
		return id;
		
	}
	
	
	
	
		
}
