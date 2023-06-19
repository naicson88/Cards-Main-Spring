package com.naicson.yugioh.data.dto;

import java.util.Date;

public class PriceDTO {
	
	private String cardName;
	private String cardSetCode;
	private double price;
	private String cardRarity;
	private Date updateTime;
	
	public PriceDTO() {
		
	}
	
	public PriceDTO(String cardName, String cardSetCode, double price, Date updateTime) {
		super();
		this.cardName = cardName;
		this.cardSetCode = cardSetCode;
		this.price = price;
		this.updateTime = updateTime;
	}
	
	public String getCardName() {
		return cardName;
	}
	public void setCardName(String cardName) {
		this.cardName = cardName;
	}
	public String getCardSetCode() {
		return cardSetCode;
	}
	public void setCardSetCode(String cardSetCode) {
		this.cardSetCode = cardSetCode;
	}
	public double getPrice() {
		return price;
	}
	public void setPrice(double price) {
		this.price = price;
	}
	public Date getUpdateTime() {
		return updateTime;
	}
	public void setUpdateTime(Date updateTime) {
		this.updateTime = updateTime;
	}

	public String getCardRarity() {
		return cardRarity;
	}

	public void setCardRarity(String cardRarity) {
		this.cardRarity = cardRarity;
	}
	
	
}
