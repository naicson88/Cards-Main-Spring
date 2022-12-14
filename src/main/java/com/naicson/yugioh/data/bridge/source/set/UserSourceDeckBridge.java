package com.naicson.yugioh.data.bridge.source.set;

import javax.persistence.EntityNotFoundException;

import com.naicson.yugioh.data.dto.set.SetDetailsDTO;
import com.naicson.yugioh.entity.Deck;
import com.naicson.yugioh.entity.sets.UserDeck;
import com.naicson.yugioh.repository.sets.UserDeckRepository;
import com.naicson.yugioh.service.deck.DeckServiceImpl;

public class UserSourceDeckBridge implements SourceSetBridge {	
	
	private final String TABLE = "tab_rel_deckusers_cards";
	
	DeckServiceImpl deckService;
	UserDeckRepository userDeckRepository;
	
	public UserSourceDeckBridge( DeckServiceImpl deckService, UserDeckRepository userDeckRepository) {
		super();
		this.userDeckRepository = userDeckRepository;
		this.deckService = deckService;
	}

	@Override
	public SetDetailsDTO getDetails(Long deckId) {
		
		UserDeck deckUser = userDeckRepository.findById(deckId)
				.orElseThrow(() -> new EntityNotFoundException("Can't find UserDeck with ID: " + deckId));
		Deck deck = Deck.deckFromDeckUser(deckUser);
		
		return  deckService.constructDeckDetails(deckId, deck, TABLE, "user");

	}

}
