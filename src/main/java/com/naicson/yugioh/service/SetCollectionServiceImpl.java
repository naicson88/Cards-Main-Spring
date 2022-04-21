package com.naicson.yugioh.service;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import com.naicson.yugioh.entity.sets.SetCollection;
import com.naicson.yugioh.repository.SetCollectionRepository;
import com.naicson.yugioh.service.interfaces.SetCollectionService;
import com.naicson.yugioh.util.enums.SetCollectionTypes;

@Service
public class SetCollectionServiceImpl implements SetCollectionService{
	
	@Autowired
	SetCollectionRepository setColRepository;

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
//		
//		if(CollectionUtils.isEmpty(setCollection.getDecks()))
//			throw new IllegalArgumentException("Invalid Deck for Set Collection.");
//		
		if(StringUtils.isEmpty(setCollection.getImgPath()))
			throw new IllegalArgumentException("Invalid Image path for Set Collection.");
		
		if(StringUtils.isEmpty(setCollection.getName()))
			throw new IllegalArgumentException("Invalid Name for Set Collection.");
		
		if(setCollection.getRegistrationDate() == null)
			throw new IllegalArgumentException("Invalid Registration Date for Set Collection.");
		
		if(setCollection.getReleaseDate() == null)
			throw new IllegalArgumentException("Invalid Release Date for Set Collection.");
		
		SetCollectionTypes.valueOf(setCollection.getSetCollectionType().toString());
		
	}

}
