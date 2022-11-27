package com.naicson.yugioh.data.dto.cards;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Date;
import java.util.List;

import javax.persistence.Tuple;

import com.fasterxml.jackson.annotation.JsonFormat;

public class KonamiSetsWithCardDTO {
	
	private BigInteger id;
	private String setType;
	private String image;
	private String name;
	private String cardSetCode;
	private List<String> rarity;
	private List<BigDecimal> price;
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
	private Date releaseDate;
	
	public KonamiSetsWithCardDTO() {}
	
	public KonamiSetsWithCardDTO(Tuple c) {
		
		this.id = c.get(0, BigInteger.class);
		this.setType = c.get(1, String.class);
		this.image = 	c.get(2, String.class);
		this.name = c.get(3, String.class);
		this.cardSetCode = c.get(4, String.class);
		this.rarity = List.of(c.get(5, String.class));
		this.price = List.of(c.get(6, BigDecimal.class));
		this.releaseDate = c.get(7, Date.class);
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

	public List<String> getRarity() {
		return rarity;
	}

	public void setRarity(List<String> rarity) {
		this.rarity = rarity;
	}

	public List<BigDecimal> getPrice() {
		return price;
	}

	public void setPrice(List<BigDecimal> price) {
		this.price = price;
	}

	public Date getReleaseDate() {
		return releaseDate;
	}

	public void setReleaseDate(Date releaseDate) {
		this.releaseDate = releaseDate;
	}
	
}
