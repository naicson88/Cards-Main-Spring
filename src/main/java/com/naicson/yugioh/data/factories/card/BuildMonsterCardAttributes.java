package com.naicson.yugioh.data.factories.card;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.naicson.yugioh.data.dto.CardYuGiOhAPI;
import com.naicson.yugioh.entity.Card;
import com.naicson.yugioh.entity.TipoCard;
import com.naicson.yugioh.repository.TipoCardRepository;

@Component
public class BuildMonsterCardAttributes implements BuildCard {
	
	@Autowired
	TipoCardRepository cardTypeRepository;

	@Override
	public Card buildCard(CardYuGiOhAPI apiCard, Card card) {
		
		card.setNivel(apiCard.getLevel());
		card.setAtk(apiCard.getAtk());
		card.setDef(apiCard.getDef());
		card.setQtd_link(apiCard.getLinkval() > 0 ? String.valueOf(apiCard.getLinkval()) : null); 
		card.setEscala(apiCard.getScale() != null ? Integer.parseInt(apiCard.getScale()) : null);
		card.setTipo(apiCard.getRace() != null ? this.getCardType(apiCard.getRace()) : null);
		
		return card;
	}
	
	private TipoCard getCardType(String race) {
		TipoCard type = new TipoCard();

		if (!"Normal".equalsIgnoreCase(race)) // Trap and Spell cards has "normal" type
			type = cardTypeRepository.findByName(race != null ? race.trim() : null);
		else
			type = null;

		return type;
	}

}
