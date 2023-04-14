package com.naicson.yugioh.data.strategy.setDetails;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.naicson.yugioh.data.bridge.source.SourceBridge;
import com.naicson.yugioh.data.bridge.source.SourceTypes;
import com.naicson.yugioh.data.dto.set.SetDetailsDTO;
import com.naicson.yugioh.repository.SetCollectionRepository;
import com.naicson.yugioh.repository.UserSetCollectionRepository;
import com.naicson.yugioh.service.setcollection.SetCollectionServiceImpl;
import com.naicson.yugioh.service.setcollection.SetsUtils;
import com.naicson.yugioh.util.GeneralFunctions;

@Component
public class CollectionDetailsStrategy implements SetDetailsStrategy{
	
	@Autowired
	UserSetCollectionRepository userSetRepository;

	@Autowired
	SetCollectionRepository setColRepository;
	
	@Autowired
	SetCollectionServiceImpl setService;
	
	@Autowired
	SetsUtils setsUtils;
	
	@Override
	public SetDetailsDTO getSetDetails(Long setId, String source, boolean withStats) {
		validSetSource(setId, source);
	
		SourceBridge src = SourceTypes
				.valueOf(source.toUpperCase()).getSetCollectionSource(setService, userSetRepository, setColRepository);
		
		SetDetailsDTO setDetailsDto = src.getSetDetail(setId, withStats);
		
		setDetailsDto.setQuantityUserHave(userSetRepository.countQuantityOfASetUserHave(setId.intValue(), GeneralFunctions.userLogged().getId()));
		
		if(withStats) {
			setDetailsDto = setsUtils.getSetStatistics(setDetailsDto);
			setDetailsDto.setInsideDecks(null); 
		}
				
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
}
