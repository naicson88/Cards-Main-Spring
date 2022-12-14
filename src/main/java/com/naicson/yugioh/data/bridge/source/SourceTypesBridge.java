package com.naicson.yugioh.data.bridge.source;

import java.util.Arrays;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.naicson.yugioh.data.bridge.source.set.KonamiSourceDeckBridge;
import com.naicson.yugioh.data.bridge.source.set.UserSourceDeckBridge;
import com.naicson.yugioh.repository.sets.UserDeckRepository;
import com.naicson.yugioh.service.deck.DeckServiceImpl;
import com.naicson.yugioh.service.deck.RelDeckCardsServiceImpl;

public enum SourceTypesBridge {
	
	USER {
		@Override
		public SourceBridge getDeckSource(DeckServiceImpl deckService, RelDeckCardsServiceImpl relService,  UserDeckRepository userDeckRepository) {
			return new UserSourceBridge(new UserSourceDeckBridge(deckService,userDeckRepository));
		}
	},
	
	KONAMI {
		
		@Override
		public SourceBridge getDeckSource(DeckServiceImpl deckService, RelDeckCardsServiceImpl relService, UserDeckRepository userDeckRepository) {
			return new KonamiSourceBridge(new KonamiSourceDeckBridge(deckService, relService));
		}
	};
	
	public abstract SourceBridge getDeckSource(DeckServiceImpl deckService, RelDeckCardsServiceImpl relService, UserDeckRepository userDeckRepository);
	
	
}
