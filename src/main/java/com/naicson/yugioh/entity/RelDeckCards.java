package com.naicson.yugioh.entity;

import java.io.Serializable;
import java.util.Date;
import java.util.Objects;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Entity
@Table(name = "tab_rel_deck_cards")
public class RelDeckCards implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	@Id
	@Column
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	@Column(name = "deck_id")
	@NotNull
	private Long deckId;
	@Column(name = "card_numero")
	@NotNull
	private Long cardNumber;
	@Column(name = "card_set_code")
	@NotBlank
	private String cardSetCode;
	private Double card_price;
	@NotBlank
	private String card_raridade;
	private Date dt_criacao;	
	@NotNull
	private boolean isSideDeck;
	@NotNull
	private boolean isSpeedDuel;
	@Column(nullable = false)
	@NotNull
	private Integer cardId;
	@NotNull
	private Integer quantity;
	@NotBlank
	private String setRarityCode;
	@NotBlank
	private String rarityDetails;
	
	public RelDeckCards(String cardSetCode, Double price, String rarity, String setRarityCode, String rarityDetails) {
		this.cardSetCode = cardSetCode;
		this.card_price = price;
		this.card_raridade = rarity;
		this.setRarityCode = setRarityCode;
		this.rarityDetails = rarityDetails;
	}
	

	
	public RelDeckCards() {};
	
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}
	public Long getDeckId() {
		return deckId;
	}
	public void setDeckId(Long deck_id) {
		this.deckId = deck_id;
	}

	public String getCard_set_code() {
		return cardSetCode;
	}
	public void setCard_set_code(String card_set_code) {
		this.cardSetCode = card_set_code;
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
	public void setSpeedDuel(boolean isSpeedDuel) {
		this.isSpeedDuel = isSpeedDuel;
	}
	
	public Integer getQuantity() {
		return quantity;
	}
	public void setQuantity(Integer quantity) {
		this.quantity = quantity;
	}
	@Override
	public String toString() {
		return "RelDeckCards [id=" + id + ", deckId=" + deckId + ", cardNumber=" + cardNumber + ", cardSetCode="
				+ cardSetCode + ", card_price=" + card_price + ", card_raridade=" + card_raridade + ", dt_criacao="
				+ dt_criacao + ", isSideDeck=" + isSideDeck + ", isSpeedDuel=" + isSpeedDuel + "]";
	}
	
	@Override
	public int hashCode() {
		return Objects.hash(cardNumber);
	}
	
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		RelDeckCards other = (RelDeckCards) obj;
		return Objects.equals(cardNumber, other.cardNumber);
	}
	public Integer getCardId() {
		return cardId;
	}
	public void setCardId(Integer cardId) {
		this.cardId = cardId;
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
	
	private RelDeckCards(RelDeckCardsBuilder builder) {
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
		this.id = builder.id;
	}
	
		public static class RelDeckCardsBuilder {
			
			// required parameters
			private final Long deckId;
			private final Long cardNumber;
			private final String cardSetCode;
			private final Double card_price;
			private final String card_raridade;
			private final Date dt_criacao;	
			private final Boolean isSideDeck;
			private final Boolean isSpeedDuel;
			private final Integer cardId;
			private final Integer quantity;
			private final String setRarityCode;
			private final String rarityDetails;
			
			// optional parameters
			private Long id;
	
			public RelDeckCardsBuilder(Long deckId, Long cardNumber, String cardSetCode, Double card_price,
					String card_raridade, Date dt_criacao, Boolean isSideDeck, Boolean isSpeedDuel, Integer cardId,
					Integer quantity, String setRarityCode, String rarityDetails) {
	
				this.deckId = deckId;
				this.cardNumber = cardNumber;
				this.cardSetCode = cardSetCode;
				this.card_price = card_price;
				this.card_raridade = card_raridade;
				this.dt_criacao = dt_criacao;
				this.isSideDeck = isSideDeck;
				this.isSpeedDuel = isSpeedDuel;
				this.cardId = cardId;
				this.quantity = quantity;
				this.setRarityCode = setRarityCode;
				this.rarityDetails = rarityDetails;
			}
	
			public RelDeckCardsBuilder setId(Long id) {
				this.id = id;
				return this;
			}
			
			public RelDeckCards build() {
				RelDeckCards rel = new RelDeckCards(this);
				validateRelDeckCards(rel);
				return rel;
			}
	
			private void validateRelDeckCards(RelDeckCards rel) {
				
				if(Objects.isNull(rel.getCard_price()))
					throw new IllegalArgumentException("Card Price cannot be null");
				if(Objects.isNull(rel.getCard_raridade()) || rel.getCard_raridade().isBlank())
					throw new IllegalArgumentException("Card Raridade cannot be empty");
				if(Objects.isNull(rel.getCardSetCode()) || rel.getCardSetCode().isBlank())
					throw new IllegalArgumentException("Card Set Code cannot be empty");
				if(Objects.isNull(rel.getCardId()))
					throw new IllegalArgumentException(" Card ID cannot be empty");
				if(Objects.isNull(rel.getCardNumber()))
					throw new IllegalArgumentException(" Card Number cannot be empty");
				if(Objects.isNull(rel.getDeckId()))
					throw new IllegalArgumentException(" Deck ID cannot be empty");
				if(Objects.isNull(rel.getDt_criacao()))
					throw new IllegalArgumentException("Creation Date cannot be empty");
				if(Objects.isNull(rel.getIsSideDeck()))
					throw new IllegalArgumentException("Is Side Deck cannot be empty");
				if(Objects.isNull(rel.getIsSpeedDuel()))
					throw new IllegalArgumentException("Is Speed Deck cannot be empty");
				if(Objects.isNull(rel.getQuantity()) || rel.getQuantity() == 0)
					throw new IllegalArgumentException("Card Quantity cannot be empty");
				if(Objects.isNull(rel.getRarityDetails()) || rel.getRarityDetails().isBlank())
					throw new IllegalArgumentException("Rarity Details cannot be empty");
				if(Objects.isNull(rel.getSetRarityCode()) || rel.getSetRarityCode().isBlank())
					throw new IllegalArgumentException("Card Rarity Code cannot be empty");
				
			}	
			
		}
	

}
