package com.naicson.yugioh.data.dto.cards;

import java.util.List;
import java.util.Map;

import com.naicson.yugioh.data.dto.set.CardsOfUserSetsDTO;

public class CardOfUserDetailDTO {
	
	private Long cardNumber;
	private String cardImage;
	private String cardName;
	private Map<String, Long> rarity;
	private List<CardsOfUserSetsDTO> setsWithThisCard;
	private List<CardsOfUserSetsDTO> setsUserDontHave;
	
	public CardOfUserDetailDTO() {

	}
	
	public CardOfUserDetailDTO(Long cardNumber, String cardImage, String cardName) {
		super();
		this.cardNumber = cardNumber;
		this.cardImage = cardImage;
		this.cardName = cardName;
	}
	
	public String getCardImage() {
		return cardImage;
	}
	public void setCardImage(String cardImage) {
		this.cardImage = cardImage;
	}
	public String getCardName() {
		return cardName;
	}
	public void setCardName(String cardName) {
		this.cardName = cardName;
	}
	public Map<String, Long> getRarity() {
		return rarity;
	}
	public void setRarity(Map<String, Long> rarity) {
		this.rarity = rarity;
	}
	public List<CardsOfUserSetsDTO> getSetsWithThisCard() {
		return setsWithThisCard;
	}
	public void setSetsWithThisCard(List<CardsOfUserSetsDTO> setsWithThisCard) {
		this.setsWithThisCard = setsWithThisCard;
	}
	public List<CardsOfUserSetsDTO> getSetsUserDontHave() {
		return setsUserDontHave;
	}
	public void setSetsUserDontHave(List<CardsOfUserSetsDTO> setsUserDontHave) {
		this.setsUserDontHave = setsUserDontHave;
	}

	public Long getCardNumber() {
		return cardNumber;
	}

	public void setCardNumber(Long cardNumber) {
		this.cardNumber = cardNumber;
	}
	
	
	
}
