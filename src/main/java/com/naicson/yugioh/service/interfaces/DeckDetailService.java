package com.naicson.yugioh.service.interfaces;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

import javax.persistence.Query;

import org.springframework.stereotype.Service;

import com.naicson.yugioh.dto.RelUserDeckDTO;
import com.naicson.yugioh.entity.Card;
import com.naicson.yugioh.entity.Deck;
import com.naicson.yugioh.entity.RelDeckCards;
import com.naicson.yugioh.util.exceptions.ErrorMessage;

@Service
public interface DeckDetailService {
	
	
	List<Card> cardsOfDeck(Long deckId, String table) throws ErrorMessage;
	 
	Deck findById(Long Id);

	int addSetToUserCollection(Long originalDeckId) throws SQLException, ErrorMessage, Exception;

	int ImanegerCardsToUserCollection(Long originalDeckId, String flagAddOrRemove);

	List<RelUserDeckDTO> searchForDecksUserHave(Long[] decksIds);

	Long addDeck(Deck deck) throws SQLException, ErrorMessage;

	int addCardsToUserDeck(Long originalDeckId, Long generatedDeckId) throws SQLException, Exception, ErrorMessage;

	int removeSetFromUsersCollection(Long setId) throws SQLException, ErrorMessage, Exception;
	
	int addOrRemoveCardsToUserCollection(Long originalDeckId, long userId, String flagAddOrRemove)
			throws SQLException, ErrorMessage;
	
	Deck deckAndCards(Long deckId, String setType) throws Exception;
	
	List<Card> consultMainDeck(Long deckId);
	
	List<Card> consultExtraDeckCards(Long deckId,String userOrKonamiDeck);

	List<Card> sortMainDeckCards(List<Card> cardList);
	
    List<RelDeckCards> relDeckUserCards(Long deckUserId);

	void saveUserdeck(Deck deck) throws SQLException;
	
	List<Deck> searchByDeckName(String setName, String source);
	
	Deck countQtdCardRarityInTheDeck(Deck deck);

	List<RelDeckCards> relDeckCards(Long deckId, String setSource);
	
	Deck saveKonamiDeck(Deck kDeck);

	List<Card> consultSideDeckCards(Long deckId, String deckSource);

	Deck editUserDeck(Long deckId);
	
	
}
