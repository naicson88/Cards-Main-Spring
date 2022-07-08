package com.naicson.yugioh.data.dto.cards;

import java.math.BigDecimal;
import java.math.BigInteger;

public class KonamiSetsWithCardDTO {
	
	private BigInteger id;
	private String setType;
	private String image;
	private String name;
	private String cardSetCode;
	private String rarity;
	private BigDecimal price;
	
	public KonamiSetsWithCardDTO() {}
	
	public KonamiSetsWithCardDTO(BigInteger id, String setType, String image, String name, String cardSetCode,
			String rarity, BigDecimal price) {
		super();
		this.id = id;
		this.setType = setType;
		this.image = image;
		this.name = name;
		this.cardSetCode = cardSetCode;
		this.rarity = rarity;
		this.price = price;
	}

	public BigInteger getId() {
		return id;
	}
	public void setId(BigInteger id) {
		this.id = id;
	}
	public String getSetType() {
		return setType;
	}
	public void setSetType(String setType) {
		this.setType = setType;
	}
	public String getImage() {
		return image;
	}
	public void setImage(String image) {
		this.image = image;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
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
	public BigDecimal getPrice() {
		return price;
	}
	public void setPrice(BigDecimal price) {
		this.price = price;
	}
	
	
}
