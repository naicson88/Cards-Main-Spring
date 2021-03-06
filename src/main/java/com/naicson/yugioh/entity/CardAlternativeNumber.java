package com.naicson.yugioh.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table(name = "tab_card_alternative_numbers")
public class CardAlternativeNumber {
	
	@Id
	@Column
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	@Column(name = "card_id")
	private int cardId;
	private Long cardAlternativeNumber;
	/*@ManyToOne()
	@JoinColumn(name = "card_id",  referencedColumnName = "id")
	private Card card;*/
	
	public CardAlternativeNumber() {}

	public CardAlternativeNumber(Long id, int cardId, Long cardAlternativeNumber) {
		
		this.id = id;
		this.cardId = cardId;
		this.cardAlternativeNumber = cardAlternativeNumber;
	}
	
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public int getCardId() {
		return cardId;
	}
	public void setCardId(int cardId) {
		this.cardId = cardId;
	}
	public Long getCardAlternativeNumber() {
		return cardAlternativeNumber;
	}
	public void setCardAlternativeNumber(Long cardAlternativeNumber) {
		this.cardAlternativeNumber = cardAlternativeNumber;
	}
	
	
}
