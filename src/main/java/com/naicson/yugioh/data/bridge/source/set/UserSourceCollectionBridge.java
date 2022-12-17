package com.naicson.yugioh.data.bridge.source.set;

import java.util.List;

import javax.persistence.EntityNotFoundException;

import org.springframework.beans.BeanUtils;

import com.naicson.yugioh.data.bridge.source.SourceTypes;
import com.naicson.yugioh.data.dto.set.SetDetailsDTO;
import com.naicson.yugioh.entity.Deck;
import com.naicson.yugioh.entity.sets.SetCollection;
import com.naicson.yugioh.entity.sets.UserSetCollection;
import com.naicson.yugioh.repository.UserSetCollectionRepository;
import com.naicson.yugioh.service.setcollection.SetCollectionServiceImpl;

public class UserSourceCollectionBridge implements SourceSetBridge{

	UserSetCollectionRepository userSetRepository;
	SetCollectionServiceImpl setService;
	
	private final String TABLE = "tab_rel_deckusers_cards";
	
	
	
	public UserSourceCollectionBridge(UserSetCollectionRepository userSetRepository, SetCollectionServiceImpl setService) {
		super();
		this.userSetRepository = userSetRepository;
		this.setService = setService;
	}



	@Override
	public SetDetailsDTO getDetails(Long setId) {
		SetCollection setCollection = new SetCollection();

		UserSetCollection userSet = userSetRepository.findById(setId)
				.orElseThrow(() -> new EntityNotFoundException("User Set Collection not found! ID: " + setId));
		
		BeanUtils.copyProperties(userSet, setCollection);			
		setCollection.setId(userSet.getId().intValue());		
		setCollection.setDecks(List.of(Deck.deckFromDeckUser(userSet.getUserDeck().get(0))));

		 SetDetailsDTO setDetailsDto = setService.convertSetCollectionToDeck(setCollection, SourceTypes.USER, TABLE);	
		 
		 return setDetailsDto;

	}

}
