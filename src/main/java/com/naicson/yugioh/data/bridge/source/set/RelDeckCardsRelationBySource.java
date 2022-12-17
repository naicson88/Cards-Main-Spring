package com.naicson.yugioh.data.bridge.source.set;

import java.util.List;

import com.naicson.yugioh.entity.RelDeckCards;

public interface RelDeckCardsRelationBySource {
	
	List<RelDeckCards> findRelationByDeckId(Long setId);
}
