package com.naicson.yugioh.mocks;

import java.util.Date;

import com.naicson.yugioh.entity.RelDeckCards;

public class RelDeckCardsMock {
	
	public static RelDeckCards relDeckCards() {
		
		RelDeckCards rel = new RelDeckCards();
		rel.setId(1L);
		rel.setCard_price(1.5);
		rel.setCard_raridade("Super Rare");
		rel.setCard_set_code("ABCD");
		rel.setCardId(1);
		rel.setCardNumber(123456L);
		rel.setDt_criacao(new Date());
		rel.setDeckId(1L);
		rel.setIsSideDeck(false);
		rel.setIsSpeedDuel(false);
		rel.setQuantity(5);
		rel.setSetRarityCode("SR");
		
		return rel;		
		
	}
	
	public static RelDeckCards cardRarities(String raridade) {
		RelDeckCards rel = new RelDeckCards();
		rel.setCard_raridade(raridade);
		
		return rel;
	}
}