package com.naicson.yugioh.data.bridge.source;

import com.naicson.yugioh.data.bridge.source.set.SourceSetBridge;
import com.naicson.yugioh.data.dto.set.SetDetailsDTO;


public abstract class SourceBridge {
	
	protected SourceSetBridge sourceSet;
	
	public SourceBridge(SourceSetBridge sourceSet) {
		this.sourceSet = sourceSet;
	}
	
	abstract public SetDetailsDTO getSetDetail(Long setId, boolean withStats);
}
