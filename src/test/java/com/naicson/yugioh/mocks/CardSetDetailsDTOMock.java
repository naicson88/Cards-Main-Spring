package com.naicson.yugioh.mocks;

import com.naicson.yugioh.data.dto.cards.CardSetDetailsDTO;

public class CardSetDetailsDTOMock {
	
	public static CardSetDetailsDTO returnCardSetDetailsDTO() {
		CardSetDetailsDTO dto = new CardSetDetailsDTO();
		dto.setId(1);
		
		return dto;
	}
}
