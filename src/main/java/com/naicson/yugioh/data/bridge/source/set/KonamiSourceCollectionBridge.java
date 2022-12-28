package com.naicson.yugioh.data.bridge.source.set;

import javax.persistence.EntityNotFoundException;

import com.naicson.yugioh.data.bridge.source.SourceTypes;
import com.naicson.yugioh.data.dto.set.SetDetailsDTO;
import com.naicson.yugioh.entity.sets.SetCollection;
import com.naicson.yugioh.repository.SetCollectionRepository;
import com.naicson.yugioh.service.setcollection.SetCollectionServiceImpl;

public class KonamiSourceCollectionBridge implements SourceSetBridge{
	
	SetCollectionServiceImpl setService;
	SetCollectionRepository setRepository;
	private static final String TABLE = "tab_rel_deck_cards";
	
	public KonamiSourceCollectionBridge(SetCollectionServiceImpl setService, SetCollectionRepository setRepository) {
		super();
		this.setService = setService;
		this.setRepository = setRepository;
	}

	@Override
	public SetDetailsDTO getDetails(Long setId) {
		
		 SetCollection setCollection = setRepository.findById(setId.intValue())
					.orElseThrow(() -> new EntityNotFoundException("Set Collection not found! ID: " + setId));
				
				 return setService.convertSetCollectionToDeck(setCollection, SourceTypes.KONAMI, TABLE);

	}

}
