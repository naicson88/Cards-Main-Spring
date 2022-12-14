package com.naicson.yugioh.data.bridge.source.set;

import org.springframework.stereotype.Service;

import com.naicson.yugioh.data.dto.set.SetDetailsDTO;
import com.naicson.yugioh.entity.Deck;
import com.naicson.yugioh.service.deck.DeckServiceImpl;
import com.naicson.yugioh.service.deck.RelDeckCardsServiceImpl;

@Service
public class KonamiSourceDeckBridge implements SourceSetBridge {

	private final String TABLE =  "tab_rel_deck_cards";
	DeckServiceImpl deckService;
	RelDeckCardsServiceImpl relService;
	
	public KonamiSourceDeckBridge(DeckServiceImpl deckService, RelDeckCardsServiceImpl relService) {
		super();
		this.deckService = deckService;
		this.relService = relService;
	}

	@Override
	public SetDetailsDTO getDetails(Long deckId) {
			
		Deck deck = deckService.findById(deckId);
		
		return deckService.constructDeckDetails(deckId, deck, TABLE, "konami") ;

	}

}
