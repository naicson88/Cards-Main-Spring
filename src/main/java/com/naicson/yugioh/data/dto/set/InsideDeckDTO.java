package com.naicson.yugioh.data.dto.set;

import java.util.List;

import com.naicson.yugioh.data.dto.cards.CardSetDetailsDTO;
import com.naicson.yugioh.entity.RelDeckCards;

public class InsideDeckDTO {
	
	public String insideDeckName;
	public String insideDeckImage;
	
	public List<CardSetDetailsDTO> cards;
	
	public List<CardSetDetailsDTO> getCards() {
		return cards;
	}
	
	public void setCards(List<CardSetDetailsDTO> cards) {
		this.cards = cards;
	}
	public String getInsideDeckName() {
		return insideDeckName;
	}
	public void setInsideDeckName(String insideDeckName) {
		this.insideDeckName = insideDeckName;
	}
	public String getInsideDeckImage() {
		return insideDeckImage;
	}
	public void setInsideDeckImage(String insideDeckImage) {
		this.insideDeckImage = insideDeckImage;
	}
	
	
}
