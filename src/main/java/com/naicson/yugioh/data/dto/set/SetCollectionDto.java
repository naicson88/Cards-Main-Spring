package com.naicson.yugioh.data.dto.set;

import java.util.Date;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonFormat;

public class SetCollectionDto {
	
	private String name;
	private String portugueseName;
	private String imgPath;
	private Boolean onlyDefaultDeck;
	private String deckParameters;
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy")
	private Date releaseDate;
	private String setType;
	private List<Integer> decks;
	private Boolean isSpeedDuel;

	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getPortugueseName() {
		return portugueseName;
	}
	public void setPortugueseName(String portugueseName) {
		this.portugueseName = portugueseName;
	}
	public String getImgPath() {
		return imgPath;
	}
	public void setImgPath(String imgPath) {
		this.imgPath = imgPath;
	}
	public Boolean getOnlyDefaultDeck() {
		return onlyDefaultDeck;
	}
	public void setOnlyDefaultDeck(Boolean onlyDefaultDeck) {
		this.onlyDefaultDeck = onlyDefaultDeck;
	}
	public Date getReleaseDate() {
		return releaseDate;
	}
	public void setReleaseDate(Date releaseDate) {
		this.releaseDate = releaseDate;
	}
	public String getSetType() {
		return setType;
	}
	public void setSetType(String setType) {
		this.setType = setType;
	}
	
	public String getDeckParameters() {
		return deckParameters;
	}
	public void setDeckParameters(String deckParameters) {
		this.deckParameters = deckParameters;
	}

	public List<Integer> getDecks() {
		return decks;
	}
	public void setDecks(List<Integer> decks) {
		this.decks = decks;
	}
	public void setIsSpeedDuel(Boolean isSpeedDuel) {
		this.isSpeedDuel = isSpeedDuel;
	}
	public boolean getIsSpeedDuel() {
		return isSpeedDuel;
	}
	public void setIsSpeedDuel(boolean isSpeedDuel) {
		this.isSpeedDuel = isSpeedDuel;
	}
	
	@Override
	public String toString() {
		return "SetCollectionDto [name=" + name + ", portugueseName=" + portugueseName + ", imgPath=" + imgPath
				+ ", onlyDefaultDeck=" + onlyDefaultDeck + ", isSpeedDuel=" + isSpeedDuel + ", deckParameters="
				+ deckParameters + ", releaseDate=" + releaseDate + ", setType=" + setType + ", decks=" + decks + "]";
	}

}

