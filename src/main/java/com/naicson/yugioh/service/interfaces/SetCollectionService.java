package com.naicson.yugioh.service.interfaces;

import com.naicson.yugioh.entity.Deck;
import com.naicson.yugioh.entity.sets.SetCollection;

public interface SetCollectionService {
	
	public SetCollection saveSetCollection(SetCollection setCollection)  throws Exception;
	
	public Deck setCollectionDetailsAsDeck(Long setId, String source);
}
