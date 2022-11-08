package com.naicson.yugioh.entity;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "tab_rel_deckusers_cards")
public class UserRelDeckCards {
	@Id
	@Column
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	@Column(name = "deck_id")
	private Long deckId;
	@Column(name = "card_numero")
	private Long cardNumber;
	@Column(name = "card_set_code")
	private String cardSetCode;
	private Double card_price;
	private String card_raridade;
	private Date dt_criacao;	
	private Boolean isSideDeck;
	private Boolean isSpeedDuel;
	@Column(nullable = false)
	private Integer cardId;
	private Integer quantity;
	private String setRarityCode;
	private String rarityDetails;
	
	
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public Long getDeckId() {
		return deckId;
	}
	public void setDeckId(Long deckId) {
		this.deckId = deckId;
	}
	public Long getCardNumber() {
		return cardNumber;
	}
	public void setCardNumber(Long cardNumber) {
		this.cardNumber = cardNumber;
	}
	public String getCardSetCode() {
		return cardSetCode;
	}
	public void setCardSetCode(String cardSetCode) {
		this.cardSetCode = cardSetCode;
	}
	public Double getCard_price() {
		return card_price;
	}
	public void setCard_price(Double card_price) {
		this.card_price = card_price;
	}
	public String getCard_raridade() {
		return card_raridade;
	}
	public void setCard_raridade(String card_raridade) {
		this.card_raridade = card_raridade;
	}
	public Date getDt_criacao() {
		return dt_criacao;
	}
	public void setDt_criacao(Date dt_criacao) {
		this.dt_criacao = dt_criacao;
	}
	public Boolean getIsSideDeck() {
		return isSideDeck;
	}
	public void setIsSideDeck(Boolean isSideDeck) {
		this.isSideDeck = isSideDeck;
	}
	public Boolean getIsSpeedDuel() {
		return isSpeedDuel;
	}
	public void setIsSpeedDuel(Boolean isSpeedDuel) {
		this.isSpeedDuel = isSpeedDuel;
	}
	public Integer getCardId() {
		return cardId;
	}
	public void setCardId(Integer cardId) {
		this.cardId = cardId;
	}
	public Integer getQuantity() {
		return quantity;
	}
	public void setQuantity(Integer quantity) {
		this.quantity = quantity;
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
	
	
}
