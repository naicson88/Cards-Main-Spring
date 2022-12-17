package com.naicson.yugioh.data.bridge.source;

import java.util.Map;

import com.naicson.yugioh.data.bridge.source.set.KonamiSourceCollectionBridge;
import com.naicson.yugioh.data.bridge.source.set.KonamiSourceDeckBridge;
import com.naicson.yugioh.data.bridge.source.set.RelDeckCardsRelationBySource;
import com.naicson.yugioh.data.bridge.source.set.UserSourceCollectionBridge;
import com.naicson.yugioh.data.bridge.source.set.UserSourceDeckBridge;
import com.naicson.yugioh.repository.SetCollectionRepository;
import com.naicson.yugioh.repository.UserSetCollectionRepository;
import com.naicson.yugioh.repository.sets.UserDeckRepository;
import com.naicson.yugioh.service.deck.DeckServiceImpl;
import com.naicson.yugioh.service.deck.RelDeckCardsServiceImpl;
import com.naicson.yugioh.service.setcollection.SetCollectionServiceImpl;

public enum SourceTypes {
	
	USER {
		@Override
		public SourceBridge getDeckSource(DeckServiceImpl deckService, RelDeckCardsServiceImpl relService,  UserDeckRepository userDeckRepository) {
			return new UserSourceBridge(new UserSourceDeckBridge(deckService,userDeckRepository));
		}

		@Override
		public RelDeckCardsRelationBySource getRelationCards(
				Map<SourceTypes, RelDeckCardsRelationBySource> mapDependencies) {
			return mapDependencies.get(SourceTypes.USER);
		}

		@Override
		public SourceBridge getSetCollectionSource(SetCollectionServiceImpl deckService,
				UserSetCollectionRepository userSetRepository, SetCollectionRepository setColRepository) {
			return new UserSourceBridge(new UserSourceCollectionBridge(userSetRepository, deckService));
		}
	},
	
	KONAMI {
		
		@Override
		public SourceBridge getDeckSource(DeckServiceImpl deckService, RelDeckCardsServiceImpl relService, UserDeckRepository userDeckRepository) {
			return new KonamiSourceBridge(new KonamiSourceDeckBridge(deckService, relService));
		}

		@Override
		public RelDeckCardsRelationBySource getRelationCards(
				Map<SourceTypes, RelDeckCardsRelationBySource> mapDependencies) {
			return mapDependencies.get(SourceTypes.KONAMI);
		}

		@Override
		public SourceBridge getSetCollectionSource(SetCollectionServiceImpl deckService,
				UserSetCollectionRepository userSetRepository, SetCollectionRepository setColRepository) {
			return new KonamiSourceBridge(new KonamiSourceCollectionBridge(deckService, setColRepository));
		}
	};
	
	public abstract SourceBridge getDeckSource(DeckServiceImpl deckService, RelDeckCardsServiceImpl relService, UserDeckRepository userDeckRepository);
	public abstract RelDeckCardsRelationBySource getRelationCards(Map<SourceTypes, RelDeckCardsRelationBySource> mapDependencies);
	public abstract SourceBridge getSetCollectionSource(SetCollectionServiceImpl setService,
			UserSetCollectionRepository userSetRepository, SetCollectionRepository setColRepository);
	
	
}
