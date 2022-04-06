package com.naicson.yugioh.mocks;

import com.naicson.yugioh.entity.CardAlternativeNumber;

public class CardAlternativeNumberMock {
	
	public static CardAlternativeNumber generateValidCardAlternativeNumber(Long id) {
		
		CardAlternativeNumber alternative = new CardAlternativeNumber();
		alternative.setCardAlternativeNumber(105090L);
		alternative.setCardId(190);
		alternative.setId(id);
		
		return alternative;
	}
}
