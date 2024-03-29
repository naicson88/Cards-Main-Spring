package com.naicson.yugioh.service.interfaces;

import java.sql.SQLException;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.naicson.yugioh.data.bridge.source.SourceTypes;
import com.naicson.yugioh.data.dto.set.DeckSummaryDTO;
import com.naicson.yugioh.data.dto.set.SetDetailsDTO;
import com.naicson.yugioh.entity.Card;
import com.naicson.yugioh.entity.Deck;
import com.naicson.yugioh.entity.RelDeckCards;

@Service
public interface DeckDetailService {
	
	List<Card> cardsOfDeck(Long deckId, String table);
	
	Page<Deck> findAll(Pageable pageable);
	
	Deck findById(Long Id);

	Long addDeck(Deck deck) throws SQLException;

	SetDetailsDTO deckAndCards(Long deckId, SourceTypes setType, String table);

	Page<DeckSummaryDTO> searchBySetName(String setName, SourceTypes source);
	
	Deck countCardRaritiesOnDeck(Deck deck);

	List<RelDeckCards> relDeckCards(Long deckId, SourceTypes setSource);
	
	Deck saveKonamiDeck(Deck kDeck);
	
	Deck returnDeckWithCards(Long deckId, SourceTypes deckSource, String table);
	
}
