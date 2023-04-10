package com.naicson.yugioh.data.bridge.source;

import com.naicson.yugioh.data.bridge.source.set.SourceSetBridge;
import com.naicson.yugioh.data.dto.set.SetDetailsDTO;


public class KonamiSourceBridge extends SourceBridge {

	public KonamiSourceBridge(SourceSetBridge sourceSet) {
		super(sourceSet);
	}


	@Override
	public SetDetailsDTO getSetDetail(Long setId, boolean withStats) {
		return sourceSet.getDetails(setId, withStats);
	}
	
}
