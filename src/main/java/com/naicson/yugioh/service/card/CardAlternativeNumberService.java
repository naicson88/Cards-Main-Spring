package com.naicson.yugioh.service.card;

import java.util.HashMap;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.naicson.yugioh.entity.Card;
import com.naicson.yugioh.entity.CardAlternativeNumber;
import com.naicson.yugioh.repository.CardAlternativeNumberRepository;

@Service
public class CardAlternativeNumberService {
	
	@Autowired
	CardAlternativeNumberRepository cardAlternativeNumberRepository;
	
	public Card findCardByCardNumber(Long cardNumber) {
		return cardAlternativeNumberRepository.findCardByCardNumber(cardNumber);
	}
	
	public List<CardAlternativeNumber> findAllByCardId(Integer cardId) {
		return cardAlternativeNumberRepository.findAllByCardId(cardId);
	}
	
	public CardAlternativeNumber findByCardAlternativeNumber(Long cardNumber) {
		return cardAlternativeNumberRepository.findByCardAlternativeNumber(cardNumber);
	}
	
	@Transactional(rollbackFor = Exception.class)
	public CardAlternativeNumber save(CardAlternativeNumber card) {
		return cardAlternativeNumberRepository.save(card);
	}
}
