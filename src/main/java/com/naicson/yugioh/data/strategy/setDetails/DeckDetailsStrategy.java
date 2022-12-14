package com.naicson.yugioh.data.strategy.setDetails;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.naicson.yugioh.data.bridge.source.KonamiSourceBridge;
import com.naicson.yugioh.data.bridge.source.SourceBridge;
import com.naicson.yugioh.data.bridge.source.SourceTypesBridge;
import com.naicson.yugioh.data.bridge.source.set.KonamiSourceDeckBridge;
import com.naicson.yugioh.data.dto.cards.CardSetDetailsDTO;
import com.naicson.yugioh.data.dto.set.InsideDeckDTO;
import com.naicson.yugioh.data.dto.set.SetDetailsDTO;
import com.naicson.yugioh.entity.Deck;
import com.naicson.yugioh.repository.RelDeckCardsRepository;
import com.naicson.yugioh.repository.sets.UserDeckRepository;
import com.naicson.yugioh.service.deck.DeckServiceImpl;
import com.naicson.yugioh.service.deck.RelDeckCardsServiceImpl;
import com.naicson.yugioh.service.setcollection.SetsUtils;

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
	public SetDetailsDTO getSetDetails(Long deckId, String deckSource) {
//		
//		if (!("Konami").equalsIgnoreCase(deckSource) && !("User").equalsIgnoreCase(deckSource))
//			throw new IllegalArgumentException("Deck Source invalid: " + deckSource);
		
		//SourceBridge src = new KonamiSourceBridge(new KonamiSourceDeckBridge(deckService, relService));
		SourceBridge src =  SourceTypesBridge.valueOf(deckSource.toUpperCase()).getDeckSource(deckService, relService, userDeckRepository);

		SetDetailsDTO dto = src.getSetDetail(deckId);
		
		return dto;
	}
	
//	@Override
//	public SetDetailsDTO getSetDetails(Long deckId, String deckSource) {
//		
//		if (!("Konami").equalsIgnoreCase(deckSource) && !("User").equalsIgnoreCase(deckSource))
//			throw new IllegalArgumentException("Deck Source invalid: " + deckSource);
//
//		Deck deck = deckService.returnDeckWithCards(deckId, deckSource);
//
//		SetDetailsDTO dto = convertDeckToSetDetailsDTO(deck);
//		
//		dto.setQuantity(deckService.countDeckRarityQuantity(dto));
//		dto.setQuantityUserHave(userDeckRepository.countQuantityOfADeckUserHave(deckId, GeneralFunctions.userLogged().getId()));
//
//		//dto = utils.getSetStatistics(dto);
//
//		return dto;
//	}
	

	
	private SetDetailsDTO convertDeckToSetDetailsDTO(Deck deck) {

		SetDetailsDTO dto = new SetDetailsDTO();
		InsideDeckDTO insideDeck = new InsideDeckDTO();
		SetsUtils setsUtils = new SetsUtils();
		
		BeanUtils.copyProperties(deck, dto);		
		
		List<CardSetDetailsDTO> cardDetailsList = deck.getCards().stream().map(c -> {			
			CardSetDetailsDTO cardDetail = new CardSetDetailsDTO();		
			BeanUtils.copyProperties(c, cardDetail);
			
			cardDetail.setListCardRarity(setsUtils.listCardRarity(cardDetail, deck.getRel_deck_cards()));

			return cardDetail;

		})//.sorted(Comparator.comparingInt(CardSetDetailsDTO::getId))
		  .collect(Collectors.toList());
		
		insideDeck.setCards(cardDetailsList);
		dto.setInsideDecks(List.of(insideDeck));

		return dto;
	}

}
