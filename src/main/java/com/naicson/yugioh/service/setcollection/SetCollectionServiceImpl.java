package com.naicson.yugioh.service.setcollection;

import javax.persistence.EntityNotFoundException;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.naicson.yugioh.entity.Deck;
import com.naicson.yugioh.entity.sets.SetCollection;
import com.naicson.yugioh.repository.SetCollectionRepository;
import com.naicson.yugioh.service.deck.DeckServiceImpl;
import com.naicson.yugioh.service.interfaces.SetCollectionService;
import com.naicson.yugioh.util.enums.SetType;

@Service
public class SetCollectionServiceImpl implements SetCollectionService{
	
	@Autowired
	SetCollectionRepository setColRepository;
	
	@Autowired
	DeckServiceImpl deckService;

	@Override
	public SetCollection saveSetCollection(SetCollection setCollection) {
		
		validSetCollection(setCollection);
	
		SetCollection collectionSaved = setColRepository.save(setCollection);
		
		return collectionSaved;
	}
	
	private void validSetCollection(SetCollection setCollection) {
		
		if(setCollection == null)
			throw new IllegalArgumentException("Invalid Set Collection.");
		
		if(setCollection.getIsSpeedDuel() == null)
			throw new IllegalArgumentException("Invalid Speed Duel definition.");
		
		if(setCollection.getOnlyDefaultDeck() == null)
			throw new IllegalArgumentException("Invalid Only default Deck definition.");
		
		if(StringUtils.isEmpty(setCollection.getImgPath()))
			throw new IllegalArgumentException("Invalid Image path for Set Collection.");
		
		if(StringUtils.isEmpty(setCollection.getName()))
			throw new IllegalArgumentException("Invalid Name for Set Collection.");
		
		if(setCollection.getRegistrationDate() == null)
			throw new IllegalArgumentException("Invalid Registration Date for Set Collection.");
		
		if(setCollection.getReleaseDate() == null)
			throw new IllegalArgumentException("Invalid Release Date for Set Collection.");
		
		SetType.valueOf(setCollection.getSetCollectionType().toString());
		
	}

	@Override
	public Deck setCollectionDetailsAsDeck(Long setId, String source) {
		
		if(setId == null)
			throw new IllegalArgumentException("Invalid Set Id");
		
		if(source == null || source.isBlank())
			throw new IllegalArgumentException("Invalid Source");
		
		SetCollection set = new SetCollection();
		Deck deck = new Deck();
		
		if("konami".equalsIgnoreCase(source)) {
			set = setColRepository.findById(setId.intValue())
				.orElseThrow(() -> new EntityNotFoundException("Set Collection not found! ID: " + setId));
			
			deck = this.convertSetCollectionToDeck(set);
			
		}
		
		return deck;
	}
	
	private Deck convertSetCollectionToDeck(SetCollection set) {
		
		Deck deck = new Deck();
		
		deck.setDt_criacao(set.getRegistrationDate());
		deck.setId(set.getId().longValue());
		deck.setImagem(set.getImgPath());
		deck.setIsSpeedDuel(set.getIsSpeedDuel());
		deck.setLancamento(set.getReleaseDate());
		deck.setNome(set.getName());
		deck.setNomePortugues(set.getPortugueseName());
		deck.setSetType(set.getSetCollectionType().toString());
		
		set.getDecks().stream().forEach(d -> { deck.setCards(d.getCards());});
		
		set.getDecks().stream().forEach(d -> {
			deck.setQtd_cards(deck.getQtd_cards() + d.getQtd_cards());
			deck.setQtd_comuns(deck.getQtd_comuns() + d.getQtd_comuns());
			deck.setQtd_raras(deck.getQtd_raras() + d.getQtd_raras());
			deck.setQtd_secret_raras(deck.getQtd_secret_raras() + d.getQtd_secret_raras());
			deck.setQtd_super_raras(deck.getQtd_super_raras() + d.getQtd_super_raras());
			deck.setQtd_ultra_raras(deck.getQtd_ultra_raras() + d.getQtd_ultra_raras());
		});
			
		set.getDecks().stream().forEach(d -> { 
			
			Deck deckAux = deckService.returnDeckWithCards(d.getId(), "konami");
			
			deck.setCards(deckAux.getCards());
			deck.setRel_deck_cards(deckAux.getRel_deck_cards());
			
			});
		
		return deck;
				
	}

}
