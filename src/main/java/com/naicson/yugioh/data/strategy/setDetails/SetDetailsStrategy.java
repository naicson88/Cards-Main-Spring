package com.naicson.yugioh.data.strategy.setDetails;

import com.naicson.yugioh.data.dto.set.SetDetailsDTO;

public interface SetDetailsStrategy {
	
	 SetDetailsDTO getSetDetails(Long deckId, String source);
	
	 SetDetailsType setDetailsType();
}
