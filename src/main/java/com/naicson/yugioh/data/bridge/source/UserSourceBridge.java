package com.naicson.yugioh.data.bridge.source;

import com.naicson.yugioh.data.bridge.source.set.SourceSetBridge;
import com.naicson.yugioh.data.dto.set.SetDetailsDTO;


public class UserSourceBridge extends SourceBridge{

	public UserSourceBridge(SourceSetBridge sourceSet) {
		super(sourceSet);
		// TODO Auto-generated constructor stub
	}

	@Override
	public SetDetailsDTO getSetDetail(Long setId) {
		return sourceSet.getDetails(setId);		
	}	
}
