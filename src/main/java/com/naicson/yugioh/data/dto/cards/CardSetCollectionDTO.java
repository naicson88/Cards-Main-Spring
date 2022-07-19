package com.naicson.yugioh.data.dto.cards;

public class CardSetCollectionDTO {
	
	private Integer cardId;
	private Integer number;
	private String name;
	private Double price;
	private String cardSetCode;
	private String rarity;
	private int quantityUserHave;
	private int quantityOtherCollections;
	
	private CardSetCollectionDTO() {}
	
	public CardSetCollectionDTO(Integer cardId, Integer number, String name, Double price, String cardSetCode,
			String rarity, int quantityUserHave, int quantityOtherCollections) {
		super();
		this.cardId = cardId;
		this.number = number;
		this.name = name;
		this.price = price;
		this.cardSetCode = cardSetCode;
		this.rarity = rarity;
		this.quantityUserHave = quantityUserHave;
		this.quantityOtherCollections = quantityOtherCollections;
	}



	public Integer getCardId() {
		return cardId;
	}

	public void setCardId(Integer cardId) {
		this.cardId = cardId;
	}

	public Integer getNumber() {
		return number;
	}

	public void setNumber(Integer number) {
		this.number = number;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Double getPrice() {
		return price;
	}

	public void setPrice(Double price) {
		this.price = price;
	}

	public int getQuantityUserHave() {
		return quantityUserHave;
	}

	public void setQuantityUserHave(int quantityUserHave) {
		this.quantityUserHave = quantityUserHave;
	}

	

	public int getQuantityOtherCollections() {
		return quantityOtherCollections;
	}



	public void setQuantityOtherCollections(int quantityOtherCollections) {
		this.quantityOtherCollections = quantityOtherCollections;
	}



	public String getCardSetCode() {
		return cardSetCode;
	}

	public void setCardSetCode(String cardSetCode) {
		this.cardSetCode = cardSetCode;
	}



	public String getRarity() {
		return rarity;
	}



	public void setRarity(String rarity) {
		this.rarity = rarity;
	}
	
	
}
