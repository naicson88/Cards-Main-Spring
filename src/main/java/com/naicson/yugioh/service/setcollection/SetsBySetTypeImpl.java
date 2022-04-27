package com.naicson.yugioh.service.setcollection;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import com.naicson.yugioh.data.dto.set.DeckSummaryDTO;
import com.naicson.yugioh.entity.Deck;
import com.naicson.yugioh.entity.sets.SetCollection;
import com.naicson.yugioh.repository.DeckRepository;
import com.naicson.yugioh.repository.SetCollectionRepository;
import com.naicson.yugioh.service.interfaces.DeckDetailService;
import com.naicson.yugioh.util.enums.SetType;

import ch.qos.logback.core.pattern.Converter;

@Component
public class SetsBySetTypeImpl <T> implements ISetsByType<T>{
	
	@Autowired
	DeckDetailService deckService;
	
	@Autowired
	SetCollectionRepository setRepository;
	
	@Override
	public Page<DeckSummaryDTO> findAllSetsByType(Pageable pageable, String informedSetType) {
		Page<SetCollection> pageSet = null;
		Page<Deck> pageDeck;
		Page<DeckSummaryDTO> pageDTO  = null;
		
		SetType setType = SetType.valueOf(informedSetType);
		
		if(setType.equals(SetType.DECK)) {
			 pageDeck = deckService.findAll(pageable);		
			 pageDTO = convertPageDeck(pageDeck);
		} 
		// Any other type is a collection
		else {
			pageSet =  setRepository.findAllBySetType(pageable, setType.toString());
			pageDTO = this.convertPageSetToPageDeck(pageSet);
		}
					
		 return pageDTO;
	}
	
	private Page<DeckSummaryDTO> convertPageSetToPageDeck(Page<SetCollection> pageSet){
		
		Page<DeckSummaryDTO> pageDeck = pageSet.map(originalPage -> {
			DeckSummaryDTO deck =  this.convertSetCollectionToDTO(originalPage);
			return deck;
		});
		
		return pageDeck;		
	}

	private DeckSummaryDTO convertSetCollectionToDTO(SetCollection set) {
		
		DeckSummaryDTO deck = new DeckSummaryDTO();
		deck.setLancamento(set.getReleaseDate());
		deck.setNome(set.getName());
		deck.setSetType(set.getSetCollectionType().toString());
		deck.setImagem(set.getImgPath());
		deck.setNomePortugues(set.getPortugueseName());
		deck.setQuantityUserHave(0);
		
		return deck;
	}
	
	private Page<DeckSummaryDTO> convertPageDeck(Page<Deck> deck){
		
		Page<DeckSummaryDTO> pageDeck = deck.map(originalPage -> {
			DeckSummaryDTO deckDTO =  this.convertDeckToDTO(originalPage);
			return deckDTO;
		});
		
		return pageDeck;		
	}

	private DeckSummaryDTO convertDeckToDTO(Deck originalDeck) {
				
		DeckSummaryDTO deck = new DeckSummaryDTO();
		deck.setLancamento(originalDeck.getLancamento());
		deck.setNome(originalDeck.getNome());
		deck.setSetType(originalDeck.getSetType().toString());
		deck.setImagem(originalDeck.getImagem());
		deck.setNomePortugues(originalDeck.getNomePortugues());
		
		Long[] idEntity = {originalDeck.getId()};
		 if(deckService.searchForDecksUserHave(idEntity) != null && deckService.searchForDecksUserHave(idEntity).size() > 0)
			 deck.setQuantityUserHave(deckService.searchForDecksUserHave(idEntity).get(0).getQuantity());
		
		return deck;
	}
}
