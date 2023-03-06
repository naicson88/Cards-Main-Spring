package com.naicson.yugioh.entity;

import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
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
	private LocalDateTime registrationDate;
	
	public CardAlternativeNumber() {}

	public CardAlternativeNumber( int cardId, Long cardAlternativeNumber) {		
		this.cardId = cardId;
		this.cardAlternativeNumber = cardAlternativeNumber;
		this.registrationDate = LocalDateTime.now();
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

	public LocalDateTime getRegistrationDate() {
		return registrationDate;
	}

	public void setRegistrationDate(LocalDateTime registrationDate) {
		this.registrationDate = registrationDate;
	}
	
	
}
