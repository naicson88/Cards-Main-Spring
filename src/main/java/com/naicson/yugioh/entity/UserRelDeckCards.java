package com.naicson.yugioh.entity;

import java.util.Date;
import java.util.function.Consumer;

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
	
	public UserRelDeckCards() {}
	
	private UserRelDeckCards(Builder builder) {
		super();
		this.id = builder.id;
		this.deckId = builder.deckId;
		this.cardNumber = builder.cardNumber;
		this.cardSetCode = builder.cardSetCode;
		this.card_price = builder.card_price;
		this.card_raridade = builder.card_raridade;
		this.dt_criacao = builder.dt_criacao;
		this.isSideDeck = builder.isSideDeck;
		this.isSpeedDuel = builder.isSpeedDuel;
		this.cardId = builder.cardId;
		this.quantity = builder.quantity;
		this.setRarityCode = builder.setRarityCode;
		this.rarityDetails = builder.rarityDetails;
	}
	
	public Long getId() {
		return id;
	}
	public Long getDeckId() {
		return deckId;
	}
	public Long getCardNumber() {
		return cardNumber;
	}
	public String getCardSetCode() {
		return cardSetCode;
	}
	public Double getCard_price() {
		return card_price;
	}
	public String getCard_raridade() {
		return card_raridade;
	}
	public Date getDt_criacao() {
		return dt_criacao;
	}
	public Boolean getIsSideDeck() {
		return isSideDeck;
	}
	public Boolean getIsSpeedDuel() {
		return isSpeedDuel;
	}
	public Integer getCardId() {
		return cardId;
	}
	public Integer getQuantity() {
		return quantity;
	}
	public String getSetRarityCode() {
		return setRarityCode;
	}
	public String getRarityDetails() {
		return rarityDetails;
	}	
		
	public void setQuantity(Integer quantity) {
		this.quantity = quantity;
	}
	
	

	public void setCardNumber(Long cardNumber) {
		this.cardNumber = cardNumber;
	}

	public void setCardSetCode(String cardSetCode) {
		this.cardSetCode = cardSetCode;
	}

	public void setCard_price(Double card_price) {
		this.card_price = card_price;
	}

	public void setCard_raridade(String card_raridade) {
		this.card_raridade = card_raridade;
	}

	public void setIsSideDeck(Boolean isSideDeck) {
		this.isSideDeck = isSideDeck;
	}

	public void setIsSpeedDuel(Boolean isSpeedDuel) {
		this.isSpeedDuel = isSpeedDuel;
	}

	public void setCardId(Integer cardId) {
		this.cardId = cardId;
	}

	public void setSetRarityCode(String setRarityCode) {
		this.setRarityCode = setRarityCode;
	}

	public void setRarityDetails(String rarityDetails) {
		this.rarityDetails = rarityDetails;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public void setDeckId(Long deckId) {
		this.deckId = deckId;
	}

	public void setDt_criacao(Date dt_criacao) {
		this.dt_criacao = dt_criacao;
	}



	public static class Builder {
		public Long id;
		public Long deckId;
		public Long cardNumber;
		public String cardSetCode;
		public Double card_price;
		public String card_raridade;
		public Date dt_criacao;	
		public Boolean isSideDeck;
		public Boolean isSpeedDuel;
		public Integer cardId;
		public Integer quantity;
		public String setRarityCode;
		public String rarityDetails;
		
		public Builder() {}
		
		public Builder with(Consumer<Builder> consumer) {
			consumer.accept(this); return this;
		}
		
		
		
		public UserRelDeckCards build() {return new UserRelDeckCards(this);}
	}
	
	
}
