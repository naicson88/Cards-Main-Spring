package com.naicson.yugioh.mocks;

import cardscommons.dto.CardYuGiOhAPI;

public class CardYuGiOhAPIMock {
	
	public static CardYuGiOhAPI createCardAPI() {
		
		CardYuGiOhAPI card = new CardYuGiOhAPI();
		card.setId(123456789L);
		card.setArchetype("Teste");
		card.setAtk(1000);
		card.setAttribute("EARTH");
		card.setDef(1500);
		card.setDesc("Description test");
		card.setLevel(5);
		card.setName("Card Name");
		card.setRace("Dragon");
		card.setType("monster");
		
		return card;
	}
}
