package com.naicson.yugioh.data.factories.card;

import org.apache.commons.lang3.EnumUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import com.naicson.yugioh.data.dto.CardYuGiOhAPI;
import com.naicson.yugioh.entity.Card;
import com.naicson.yugioh.util.enums.CardProperty;

@Component
public class BuildNonMonsterCardAttributes implements BuildCard{

	@Override
	public Card buildCard(CardYuGiOhAPI apiCard, Card card) {
		
		if (StringUtils.containsIgnoreCase("spell", apiCard.getType()) 
				|| StringUtils.containsIgnoreCase("trap", apiCard.getType())) {

			card.setPropriedade(apiCard.getRace() != null ? this.getCardProperty(apiCard.getRace()) : null);
		}
		
		return card;
	}
		
	private String getCardProperty(String race) {
		if (!EnumUtils.isValidEnum(CardProperty.class, race))
			throw new IllegalArgumentException("Invalid property");
		return race;
	}

}
