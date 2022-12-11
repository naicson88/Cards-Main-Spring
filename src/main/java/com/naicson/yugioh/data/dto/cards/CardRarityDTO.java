package com.naicson.yugioh.data.dto.cards;

public class CardRarityDTO {
	
	private Long cardNumber;
	private String card_raridade;
	private String setRarityCode;
	private String rarityDetails;
	private Double card_price;
	private String cardSetCode;
	
	
	public String getCard_raridade() {
		return card_raridade;
	}


	public void setCard_raridade(String card_raridade) {
		this.card_raridade = card_raridade;
	}


	public String getSetRarityCode() {
		return setRarityCode;
	}


	public void setSetRarityCode(String setRarityCode) {
		this.setRarityCode = setRarityCode;
	}


	public String getRarityDetails() {
		return rarityDetails;
	}


	public void setRarityDetails(String rarityDetails) {
		this.rarityDetails = rarityDetails;
	}


	public Double getCard_price() {
		return card_price;
	}


	public void setCard_price(Double card_price) {
		this.card_price = card_price;
	}


	public String getCardSetCode() {
		return cardSetCode;
	}
	public void setCardSetCode(String cardSetCode) {
		this.cardSetCode = cardSetCode;
	}


	public Long getCardNumber() {
		return cardNumber;
	}


	public void setCardNumber(Long card_numero) {
		this.cardNumber = card_numero;
	}
	
	
}
