package com.naicson.yugioh.service.setcollection;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import com.naicson.yugioh.data.dto.set.DeckSummaryDTO;
import com.naicson.yugioh.entity.Deck;
import com.naicson.yugioh.entity.sets.SetCollection;
import com.naicson.yugioh.entity.sets.UserDeck;
import com.naicson.yugioh.entity.sets.UserSetCollection;
import com.naicson.yugioh.repository.SetCollectionRepository;
import com.naicson.yugioh.repository.UserSetCollectionRepository;
import com.naicson.yugioh.service.deck.DeckServiceImpl;
import com.naicson.yugioh.service.deck.UserDeckServiceImpl;
import com.naicson.yugioh.util.GeneralFunctions;
import com.naicson.yugioh.util.enums.SetType;

@Component
public class SetsBySetTypeImpl <T> implements ISetsByType<T>{
	
	@Autowired
	UserDeckServiceImpl userDeckService;	
	@Autowired
	UserSetCollectionRepository userSetRepository ;
	
	@Autowired
	DeckServiceImpl deckService;	
	@Autowired
	SetCollectionRepository setRepository;

	
	@Override
	public Page<DeckSummaryDTO> findAllSetsByType(Pageable pageable, String setType) {
		
		SetType type = SetType.valueOf(setType);
		
		if(type.equals(SetType.DECK))
			return convertPageDeck(deckService.findAllBySetType(pageable, setType)); 
			
		return this.convertPageSetToPageDeck(setRepository.findAllBySetType(pageable, type.toString()));

	}
	

	private Page<DeckSummaryDTO> convertPageSetToPageDeck(Page<SetCollection> pageSet){
		
		return pageSet.map(originalPage -> {
			return  this.convertSetCollectionToDTO(originalPage);
			//return deck;
		});
		
		//return pageDeck;		
	}

	private DeckSummaryDTO convertSetCollectionToDTO(SetCollection set) {
		return DeckSummaryDTO.dtoFromSetCollection(set, userSetRepository);
	}

	private DeckSummaryDTO convertDeckToDTO(Deck originalDeck) {
		return DeckSummaryDTO.convertDeckToDTO(originalDeck, userDeckService);
	}
	
	private Page<DeckSummaryDTO> convertPageDeck(Page<Deck> deck){
		return deck.map(this::convertDeckToDTO);
	}

	private DeckSummaryDTO convertSetCollectionToDTO(UserSetCollection set) {
		return DeckSummaryDTO.convertSetCollectionToDTO(set);
	}

	@Override
	public Page<DeckSummaryDTO> findAllUserSetsByType(Pageable pageable, String setType) {
		
		SetType type = SetType.valueOf(setType);
		
		Page<DeckSummaryDTO> pageDTO  = null;
		
		if(type.equals(SetType.DECK))	
			 pageDTO = convertPageUserDeck(userDeckService.findAllBySetType(pageable, setType));
		else 		
			pageDTO = this.convertPageUserSetToPageDeck(userSetRepository.findAllBySetCollectionTypeOrderByIdDesc(pageable, type));
					
		 return pageDTO;
	}
	
	
	private Page<DeckSummaryDTO> convertPageUserSetToPageDeck(Page<UserSetCollection> pageSet){		
		return pageSet.map(originalPage -> {
			return this.convertSetCollectionToDTO(originalPage);
		});
		
	}


	private Page<DeckSummaryDTO> convertPageUserDeck(Page<UserDeck> userDeck){
		return userDeck.map(originalPage -> {
			DeckSummaryDTO deckDTO = new DeckSummaryDTO();
			
			BeanUtils.copyProperties(originalPage, deckDTO);
			
			return deckDTO;
		});
	}
}
