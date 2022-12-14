package com.naicson.yugioh.data.strategy.setDetails;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import javax.persistence.EntityNotFoundException;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.naicson.yugioh.data.dto.cards.CardSetDetailsDTO;
import com.naicson.yugioh.data.dto.set.InsideDeckDTO;
import com.naicson.yugioh.data.dto.set.SetDetailsDTO;
import com.naicson.yugioh.entity.Deck;
import com.naicson.yugioh.entity.sets.SetCollection;
import com.naicson.yugioh.entity.sets.UserSetCollection;
import com.naicson.yugioh.repository.SetCollectionRepository;
import com.naicson.yugioh.repository.UserSetCollectionRepository;
import com.naicson.yugioh.service.deck.DeckServiceImpl;
import com.naicson.yugioh.service.setcollection.SetsUtils;
import com.naicson.yugioh.util.GeneralFunctions;

@Component
public class CollectionDetailsStrategy implements SetDetailsStrategy{
	
	@Autowired
	UserSetCollectionRepository userSetRepository;

	@Autowired
	SetCollectionRepository setColRepository;
	
	@Autowired
	DeckServiceImpl deckService;
	
	@Autowired
	SetsUtils setsUtils;
	
	
	@Override
	public SetDetailsDTO getSetDetails(Long setId, String source) {
		validSetSource(setId, source);
		
		SetDetailsDTO setDetailsDto = "KONAMI".equalsIgnoreCase(source) ? this.konamiSetDetailsDTO(setId) : userSetDetailsDTO(setId);
		
//		if(setDetailsDto.getInsideDecks() != null && setDetailsDto.getInsideDecks().size() > 0)	
//			setDetailsDto = setsUtils.getSetStatistics(setDetailsDto);
//		
		setDetailsDto.setQuantityUserHave(userSetRepository.countQuantityOfASetUserHave(setId.intValue(), GeneralFunctions.userLogged().getId()));
		return setDetailsDto;
	}

	@Override
	public SetDetailsType setDetailsType() {
		return SetDetailsType.COLLECTION;
	}
	
	private void validSetSource(Long setId, String source) {
		if(setId == null)
			throw new IllegalArgumentException("Invalid Set Id");
		
		if(source == null || source.isBlank())
			throw new IllegalArgumentException("Invalid Source");
	}
	
	private SetDetailsDTO konamiSetDetailsDTO(Long setId) {
		
		 SetCollection setCollection = setColRepository.findById(setId.intValue())
			.orElseThrow(() -> new EntityNotFoundException("Set Collection not found! ID: " + setId));
		
		 SetDetailsDTO setDetailsDto = this.convertSetCollectionToDeck(setCollection, "KONAMI");
		
		return setDetailsDto;
	}
	
	private SetDetailsDTO userSetDetailsDTO(Long setId) {
		SetCollection setCollection = new SetCollection();

		UserSetCollection userSet = userSetRepository.findById(setId)
				.orElseThrow(() -> new EntityNotFoundException("User Set Collection not found! ID: " + setId));
		
		BeanUtils.copyProperties(userSet, setCollection);			
		setCollection.setId(userSet.getId().intValue());		
		setCollection.setDecks(List.of(Deck.deckFromDeckUser(userSet.getUserDeck().get(0))));

		 SetDetailsDTO setDetailsDto = this.convertSetCollectionToDeck(setCollection, "USER");	
		 
		 return setDetailsDto;
	}
	
	private SetDetailsDTO convertSetCollectionToDeck(SetCollection set, String deckSource) {
		
		SetDetailsDTO setDetailsDto = convertBasicSetToSetDetailsDTO(set);
		
		List<InsideDeckDTO> listInsideDeck = new ArrayList<>();		
		set = getCardsForEachDeck(set, deckSource);
		
		// Iterate over Deck	
		set.getDecks().stream().forEach(d -> { 			
			InsideDeckDTO insideDeck = new InsideDeckDTO();			
			insideDeck.setInsideDeckName(d.getNome());
			insideDeck.setInsideDeckImage(d.getImgurUrl());
			
			//Iterate over Cards
			List<CardSetDetailsDTO> listSetDetails = d.getCards().stream().map(c -> {			
				CardSetDetailsDTO cardDetail = new CardSetDetailsDTO();	
				BeanUtils.copyProperties(c, cardDetail);
				
				cardDetail.setListCardRarity(setsUtils.listCardRarity(cardDetail, d.getRel_deck_cards()));
				
				return cardDetail;		
				
			}).collect(Collectors.toList()); ;			
		
			insideDeck.setCards(listSetDetails);
			listInsideDeck.add(insideDeck);				
		});
		
		setDetailsDto.setInsideDecks(listInsideDeck);
		setDetailsDto.setQuantity(deckService.countDeckRarityQuantity(setDetailsDto));
		
		return setDetailsDto;
				
	}
	
	private SetDetailsDTO convertBasicSetToSetDetailsDTO(SetCollection set) {
		SetDetailsDTO deck = new SetDetailsDTO();
				
		deck.setDt_criacao(set.getRegistrationDate());
		deck.setId(set.getId().longValue());
		deck.setImagem(set.getImgurUrl());
		deck.setImgurUrl(set.getImgurUrl());
		deck.setIsSpeedDuel(set.getIsSpeedDuel());
		deck.setLancamento(set.getReleaseDate());
		deck.setNome(set.getName());
		deck.setNomePortugues(set.getPortugueseName());
		deck.setSetType(set.getSetCollectionType().toString());
		deck.setImgurUrl(set.getImgurUrl());
		return deck;
		
	}
	
	private SetCollection getCardsForEachDeck(SetCollection set, String deckSource) {
		
		set.getDecks().stream().forEach(d -> { 		
			Deck deckAux = deckService.returnDeckWithCards(d.getId(), deckSource);	
			d.setCards(deckAux.getCards());
			d.setRel_deck_cards(deckAux.getRel_deck_cards());		
		});
		
		return set;
	}

}
