package com.naicson.yugioh.data.strategy.source;

import com.naicson.yugioh.data.dto.set.SetDetailsDTO;

public interface SourceStrategy {
	
	SetDetailsDTO getCollectionDetails(Long setId);
	
	SetDetailsDTO getDeckDetails(Long setId);
	
}
