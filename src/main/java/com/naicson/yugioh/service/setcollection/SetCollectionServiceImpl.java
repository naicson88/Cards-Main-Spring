package com.naicson.yugioh.service.setcollection;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.persistence.EntityNotFoundException;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.naicson.yugioh.data.dto.cards.CardSetDetailsDTO;
import com.naicson.yugioh.data.dto.set.InsideDeckDTO;
import com.naicson.yugioh.data.dto.set.SetDetailsDTO;
import com.naicson.yugioh.entity.Deck;
import com.naicson.yugioh.entity.sets.SetCollection;
import com.naicson.yugioh.entity.sets.UserSetCollection;
import com.naicson.yugioh.repository.SetCollectionRepository;
import com.naicson.yugioh.repository.UserSetCollectionRepository;
import com.naicson.yugioh.service.deck.DeckServiceImpl;
import com.naicson.yugioh.service.interfaces.SetCollectionService;
import com.naicson.yugioh.util.enums.SetType;

@Service
public class SetCollectionServiceImpl implements SetCollectionService{
	
	@Autowired
	SetCollectionRepository setColRepository;
	
	@Autowired
	UserSetCollectionRepository userSetRepository;
	
	@Autowired
	DeckServiceImpl deckService;
	
	@Autowired
	SetsUtils utils;
	
	Logger logger = LoggerFactory.getLogger(SetCollectionServiceImpl.class);

	@Override
	@Transactional(rollbackFor = Exception.class)
	public SetCollection saveSetCollection(SetCollection setCollection) {
		
		validSetCollection(setCollection);
	
		SetCollection collectionSaved = setColRepository.save(setCollection);
		
		return collectionSaved;
	}

	@Override
	public SetDetailsDTO setCollectionDetailsAsDeck(Long setId, String source) {
		
		if(setId == null)
			throw new IllegalArgumentException("Invalid Set Id");
		
		if(source == null || source.isBlank())
			throw new IllegalArgumentException("Invalid Source");
		
		SetCollection set = new SetCollection();
		SetDetailsDTO deck = new SetDetailsDTO();
		
		if("KONAMI".equalsIgnoreCase(source)) {
			set = setColRepository.findById(setId.intValue())
				.orElseThrow(() -> new EntityNotFoundException("Set Collection not found! ID: " + setId));
			
			deck = this.convertSetCollectionToDeck(set);			
		} 
		
		else {
			UserSetCollection userSet = userSetRepository.findById(setId)
					.orElseThrow(() -> new EntityNotFoundException("User Set Collection not found! ID: " + setId));;
			
			BeanUtils.copyProperties(userSet, set);			
			set.setId(userSet.getId().intValue());
			
			if(set.getDecks() != null && set.getDecks().size() > 0)			
				deck = this.convertSetCollectionToDeck(set);	
			else
				deck = convertBasicSetToSetDetailsDTO(set);
		}	
		
		if(set.getDecks() != null && set.getDecks().size() > 0)	
			deck = utils.getSetStatistics(deck);
		
		return deck;
	}
	
	private SetDetailsDTO convertSetCollectionToDeck(SetCollection set) {
		
		SetDetailsDTO deck = convertBasicSetToSetDetailsDTO(set);
		
		List<InsideDeckDTO> listInsideDeck = new ArrayList<>();
		
		set = getCardsForEachDeck(set);
		// Iterate over Deck	
		set.getDecks().stream().forEach(d -> { 
			
			InsideDeckDTO insideDeck = new InsideDeckDTO();	
			Map<Integer, CardSetDetailsDTO> mapCardSetDetails = new HashMap<>();
			
			insideDeck.setInsideDeckName(d.getNome());
			insideDeck.setInsideDeckImage(d.getImgurUrl());	
			//Iterate over Cards
			List<CardSetDetailsDTO> listSetDetails = d.getCards().stream().map(c -> {
				
				CardSetDetailsDTO cardDetail = new CardSetDetailsDTO();				
				BeanUtils.copyProperties(c, cardDetail);
				mapCardSetDetails.put(cardDetail.getId(), cardDetail);
				return cardDetail;	
				
			}).collect(Collectors.toList()); ;
			
			//Iterate over Rel. Deck Cards
			d.getRel_deck_cards().forEach(r -> {
				CardSetDetailsDTO detail =  mapCardSetDetails.get(r.getCardId());		
				BeanUtils.copyProperties(r, detail);
			});	
			
			insideDeck.setCards(listSetDetails);
			listInsideDeck.add(insideDeck);		
			
		});
		
		deck.setInsideDecks(listInsideDeck);

		set.getDecks().stream().forEach(d -> {
			deck.setQtd_cards(deck.getQtd_cards() + d.getQtd_cards());
			deck.setQtd_comuns(deck.getQtd_comuns() + d.getQtd_comuns());
			deck.setQtd_raras(deck.getQtd_raras() + d.getQtd_raras());
			deck.setQtd_secret_raras(deck.getQtd_secret_raras() + d.getQtd_secret_raras());
			deck.setQtd_super_raras(deck.getQtd_super_raras() + d.getQtd_super_raras());
			deck.setQtd_ultra_raras(deck.getQtd_ultra_raras() + d.getQtd_ultra_raras());
		});
			
		return deck;
				
	}



	private SetDetailsDTO convertBasicSetToSetDetailsDTO(SetCollection set) {
		SetDetailsDTO deck = new SetDetailsDTO();
				
		deck.setDt_criacao(set.getRegistrationDate());
		deck.setId(set.getId().longValue());
		deck.setImagem(set.getImgurUrl());
		deck.setIsSpeedDuel(set.getIsSpeedDuel());
		deck.setLancamento(set.getReleaseDate());
		deck.setNome(set.getName());
		deck.setNomePortugues(set.getPortugueseName());
		deck.setSetType(set.getSetCollectionType().toString());
		deck.setImgurUrl(set.getImgurUrl());
		return deck;
		
	}

	private SetCollection getCardsForEachDeck(SetCollection set) {
		
		set.getDecks().stream().forEach(d -> { 		
			Deck deckAux = deckService.returnDeckWithCards(d.getId(), "konami");	
			d.setCards(deckAux.getCards());
			d.setRel_deck_cards(deckAux.getRel_deck_cards());		
		});
		
		return set;
	}
	
			
	@Override
	public SetCollection findById(Integer id) {	
		SetCollection col = setColRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("SetCollection not found"));		
		return col;
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

}
