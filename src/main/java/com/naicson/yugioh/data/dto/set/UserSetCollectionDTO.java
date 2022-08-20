package com.naicson.yugioh.data.dto.set;

import java.util.List;
import java.util.Map;

import com.naicson.yugioh.data.dto.cards.CardSetCollectionDTO;

public class UserSetCollectionDTO {
	
	private Long id;
	private String name;
	private Double totalPrice;
	private String image;
	private String setType;
	private Map<String, Integer> rarities;
	private List<String> setCodes;
	private List<CardSetCollectionDTO> cards;
	
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public Double getTotalPrice() {
		return totalPrice;
	}
	public void setTotalPrice(Double totalPrice) {
		this.totalPrice = totalPrice;
	}
	public Map<String, Integer> getRarities() {
		return rarities;
	}
	public void setRarities(Map<String, Integer> rarities) {
		this.rarities = rarities;
	}
	public List<String> getSetCodes() {
		return setCodes;
	}
	public void setSetCodes(List<String> setCodes) {
		this.setCodes = setCodes;
	}
	public List<CardSetCollectionDTO> getCards() {
		return cards;
	}
	public void setCards(List<CardSetCollectionDTO> cards) {
		this.cards = cards;
	}
	public String getImage() {
		return image;
	}
	public void setImage(String image) {
		this.image = image;
	}
	public String getSetType() {
		return setType;
	}
	public void setSetType(String setType) {
		this.setType = setType;
	}
	
	
}
