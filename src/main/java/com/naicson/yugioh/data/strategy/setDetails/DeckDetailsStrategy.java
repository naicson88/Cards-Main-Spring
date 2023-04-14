package com.naicson.yugioh.data.strategy.setDetails;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.naicson.yugioh.data.bridge.source.SourceBridge;
import com.naicson.yugioh.data.bridge.source.SourceTypes;
import com.naicson.yugioh.data.dto.set.SetDetailsDTO;
import com.naicson.yugioh.repository.sets.UserDeckRepository;
import com.naicson.yugioh.service.deck.DeckServiceImpl;
import com.naicson.yugioh.service.deck.RelDeckCardsServiceImpl;

@Component
public class DeckDetailsStrategy implements SetDetailsStrategy {
	
	@Autowired
	DeckServiceImpl deckService;
	
	@Autowired
	UserDeckRepository userDeckRepository;	
	
	@Autowired
	RelDeckCardsServiceImpl relService;

	@Override
	public SetDetailsType setDetailsType() {
		return SetDetailsType.DECK;
	}

	@Override
	public SetDetailsDTO getSetDetails(Long deckId, String deckSource, boolean withStats) {

		SourceBridge src =  SourceTypes.valueOf(deckSource.toUpperCase()).getDeckSource(deckService, relService, userDeckRepository);

		return src.getSetDetail(deckId, withStats);
		
	}

}
