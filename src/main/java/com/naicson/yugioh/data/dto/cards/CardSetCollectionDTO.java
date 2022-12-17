package com.naicson.yugioh.data.dto.cards;

import java.util.Collections;
import java.util.List;

import com.naicson.yugioh.entity.RelDeckCards;

public class CardSetCollectionDTO {
	
	private Integer cardId;
	private Integer number;
	private String name;
	private int quantityUserHave;
	private int quantityOtherCollections;
	private List<String> listSetCode;
	private RelDeckCards relDeckCards;
	private List<RelDeckCards> searchedRelDeckCards = Collections.emptyList();
	private boolean isSpeedDuel;
	private String genericType;
	private int sortOrder;
	
	public CardSetCollectionDTO(Integer cardId, Integer number, String name,int quantityUserHave,
			int quantityOtherCollections, RelDeckCards rel, boolean isSpeedDuel, String genericType) {
		super();
		this.cardId = cardId;
		this.number = number;
		this.name = name;
		this.quantityUserHave = quantityUserHave;
		this.quantityOtherCollections = quantityOtherCollections;
		this.relDeckCards = rel;
		this.isSpeedDuel = isSpeedDuel;
		this.genericType = genericType;
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

	public List<RelDeckCards> getSearchedRelDeckCards() {
		return searchedRelDeckCards;
	}

	public void setSearchedRelDeckCards(List<RelDeckCards> searchedRelDeckCards) {
		this.searchedRelDeckCards = searchedRelDeckCards;
	}

	public RelDeckCards getRelDeckCards() {
		return relDeckCards;
	}

	public void setRelDeckCards(RelDeckCards relDeckCards) {
		this.relDeckCards = relDeckCards;
	}

	public List<String> getListSetCode() {
		return listSetCode;
	}

	public void setListSetCode(List<String> listSetCode) {
		this.listSetCode = listSetCode;
	}

	public boolean isSpeedDuel() {
		return isSpeedDuel;
	}

	public void setSpeedDuel(boolean isSpeedDuel) {
		this.isSpeedDuel = isSpeedDuel;
	}

	public String getGenericType() {
		return genericType;
	}

	public void setGenericType(String genericType) {
		this.genericType = genericType;
	}

	public int getSortOrder() {
		return sortOrder;
	}

	public void setSortOrder(int sortOrder) {
		this.sortOrder = sortOrder;
	}
	
	
	
}
