package com.naicson.yugioh.data.bridge.source.set;

import org.springframework.stereotype.Component;

import com.naicson.yugioh.data.dto.set.SetDetailsDTO;

@Component
public interface SourceSetBridge {
	
	public SetDetailsDTO getDetails(Long setId, boolean withStats);
}
