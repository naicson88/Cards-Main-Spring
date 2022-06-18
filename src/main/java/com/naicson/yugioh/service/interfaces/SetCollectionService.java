package com.naicson.yugioh.service.interfaces;

import com.naicson.yugioh.data.dto.set.SetDetailsDTO;
import com.naicson.yugioh.entity.sets.SetCollection;

public interface SetCollectionService {
	
	public SetCollection findById(Integer id);
	
	public SetCollection saveSetCollection(SetCollection setCollection)  throws Exception;
	
	public SetDetailsDTO setCollectionDetailsAsDeck(Long setId, String source);
}
