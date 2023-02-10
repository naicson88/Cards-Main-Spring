package com.naicson.yugioh.data.strategy.setDetails;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.naicson.yugioh.data.bridge.source.SourceBridge;
import com.naicson.yugioh.data.bridge.source.SourceTypes;
import com.naicson.yugioh.data.dto.set.SetDetailsDTO;
import com.naicson.yugioh.repository.SetCollectionRepository;
import com.naicson.yugioh.repository.UserSetCollectionRepository;
import com.naicson.yugioh.service.setcollection.SetCollectionServiceImpl;
import com.naicson.yugioh.util.GeneralFunctions;

@Component
public class CollectionDetailsStrategy implements SetDetailsStrategy{
	
	@Autowired
	UserSetCollectionRepository userSetRepository;

	@Autowired
	SetCollectionRepository setColRepository;
	
	@Autowired
	SetCollectionServiceImpl setService;
	
	@Override
	public SetDetailsDTO getSetDetails(Long setId, String source) {
		validSetSource(setId, source);
	
		SourceBridge src = SourceTypes
				.valueOf(source.toUpperCase()).getSetCollectionSource(setService, userSetRepository, setColRepository);
		
		SetDetailsDTO setDetailsDto = src.getSetDetail(setId);
		
		setDetailsDto.setQuantityUserHave(userSetRepository.countQuantityOfASetUserHave(setId.intValue(), GeneralFunctions.userLogged().getId()));
	//	setDetailsDto = setsUtils.getSetStatistics(setDetailsDto);
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
//	
//	private SetDetailsDTO konamiSetDetailsDTO(Long setId) {
//		
//		 SetCollection setCollection = setColRepository.findById(setId.intValue())
//			.orElseThrow(() -> new EntityNotFoundException("Set Collection not found! ID: " + setId));
//		
//		 SetDetailsDTO setDetailsDto = this.convertSetCollectionToDeck(setCollection, SourceTypes.KONAMI);
//		
//		return setDetailsDto;
//	}
//	
//	private SetDetailsDTO userSetDetailsDTO(Long setId) {
//		SetCollection setCollection = new SetCollection();
//
//		UserSetCollection userSet = userSetRepository.findById(setId)
//				.orElseThrow(() -> new EntityNotFoundException("User Set Collection not found! ID: " + setId));
//		
//		BeanUtils.copyProperties(userSet, setCollection);			
//		setCollection.setId(userSet.getId().intValue());		
//		setCollection.setDecks(List.of(Deck.deckFromDeckUser(userSet.getUserDeck().get(0))));
//
//		 SetDetailsDTO setDetailsDto = this.convertSetCollectionToDeck(setCollection, SourceTypes.USER);	
//		 
//		 return setDetailsDto;
//	}
//	
//	private SetDetailsDTO convertSetCollectionToDeck(SetCollection set, SourceTypes deckSource) {
//		
//		SetDetailsDTO setDetailsDto = convertBasicSetToSetDetailsDTO(set);
//		
//		List<InsideDeckDTO> listInsideDeck = new ArrayList<>();		
//		set = getCardsForEachDeck(set, deckSource);
//		
//		// Iterate over Deck	
//		set.getDecks().stream().forEach(d -> { 			
//			InsideDeckDTO insideDeck = new InsideDeckDTO();			
//			insideDeck.setInsideDeckName(d.getNome());
//			insideDeck.setInsideDeckImage(d.getImgurUrl());
//			
//			//Iterate over Cards
//			List<CardSetDetailsDTO> listSetDetails = d.getCards().stream().map(c -> {			
//				CardSetDetailsDTO cardDetail = new CardSetDetailsDTO();	
//				BeanUtils.copyProperties(c, cardDetail);
//				
//				cardDetail.setListCardRarity(setsUtils.listCardRarity(cardDetail, d.getRel_deck_cards()));
//				
//				return cardDetail;		
//				
//			}).collect(Collectors.toList()); ;			
//		
//			insideDeck.setCards(listSetDetails);
//			listInsideDeck.add(insideDeck);				
//		});
//		
//		setDetailsDto.setInsideDecks(listInsideDeck);
//		setDetailsDto.setQuantity(deckService.countDeckRarityQuantity(setDetailsDto));
//		
//		return setDetailsDto;
//				
//	}
//	
//	private SetDetailsDTO convertBasicSetToSetDetailsDTO(SetCollection set) {
//		SetDetailsDTO deck = new SetDetailsDTO();
//				
//		deck.setDt_criacao(set.getRegistrationDate());
//		deck.setId(set.getId().longValue());
//		deck.setImagem(set.getImgurUrl());
//		deck.setImgurUrl(set.getImgurUrl());
//		deck.setIsSpeedDuel(set.getIsSpeedDuel());
//		deck.setLancamento(set.getReleaseDate());
//		deck.setNome(set.getName());
//		deck.setNomePortugues(set.getPortugueseName());
//		deck.setSetType(set.getSetCollectionType().toString());
//		deck.setImgurUrl(set.getImgurUrl());
//		return deck;
//		
//	}
//	
//	private SetCollection getCardsForEachDeck(SetCollection set, SourceTypes deckSource) {
//		
//		set.getDecks().stream().forEach(d -> { 		
//			Deck deckAux = deckService.returnDeckWithCards(d.getId(), deckSource);	
//			d.setCards(deckAux.getCards());
//			d.setRel_deck_cards(deckAux.getRel_deck_cards());		
//		});
//		
//		return set;
//	}

}
