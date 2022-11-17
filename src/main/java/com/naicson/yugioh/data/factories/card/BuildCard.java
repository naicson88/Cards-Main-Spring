package com.naicson.yugioh.data.factories.card;

import com.naicson.yugioh.data.dto.CardYuGiOhAPI;
import com.naicson.yugioh.entity.Card;

public interface BuildCard {
	
	Card buildCard(CardYuGiOhAPI apiCard, Card card);
}
