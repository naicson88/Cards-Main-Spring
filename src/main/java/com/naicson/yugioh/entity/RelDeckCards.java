package com.naicson.yugioh.entity;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
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
	private Long deckId;
	@Column(name = "card_numero")
	private Long cardNumber;
	@Column(name = "card_set_code")
	private String cardSetCode;
	private Double card_price;
	private String card_raridade;
	private Date dt_criacao;
	private Boolean isSideDeck;
	
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
	public Long getCard_numero() {
		return cardNumber ;
	}
	public void setCard_numero(Long card_numero) {
		this.cardNumber = card_numero;
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
	
	
}