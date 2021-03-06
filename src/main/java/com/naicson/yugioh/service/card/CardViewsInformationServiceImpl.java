package com.naicson.yugioh.service.card;


import java.time.LocalDateTime;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

import javax.persistence.EntityNotFoundException;
import javax.transaction.Transactional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.naicson.yugioh.data.dto.home.RankingForHomeDTO;
import com.naicson.yugioh.entity.Card;
import com.naicson.yugioh.entity.stats.CardViewsInformation;
import com.naicson.yugioh.repository.CardRepository;
import com.naicson.yugioh.repository.CardViewsInformationRepository;
import com.naicson.yugioh.service.HomeServiceImpl;
import com.naicson.yugioh.service.interfaces.CardViewsInformationDetails;
import com.naicson.yugioh.util.enums.CardStats;

@Service
public class CardViewsInformationServiceImpl implements CardViewsInformationDetails {
	
	@Autowired
	private CardViewsInformationRepository cardViewsRepository;
	@Autowired
	private CardRepository cardRepository;
	
	Logger logger = LoggerFactory.getLogger(HomeServiceImpl.class);	
	
	@Override
	public List<RankingForHomeDTO> getWeeklyMostViewed(){
	
			CardStats stats = CardStats.VIEW;
			CardStats.valueOf(CardStats.VIEW.toString());
			List<CardViewsInformation> cardViews = stats.getStatsView(cardViewsRepository);
			
			List<RankingForHomeDTO> rankingList = this.fromCardViewsInfoToRankingDTO(cardViews);
			
			if(rankingList == null || rankingList.isEmpty()) {
				logger.error("Ranking list of view is empty".toUpperCase());
				throw new NoSuchElementException("Ranking list of VIEW is empty");
			} 
			
		return rankingList;
	}
	
	private List<RankingForHomeDTO> fromCardViewsInfoToRankingDTO(List<CardViewsInformation> cardViews){
		
		if(cardViews == null || cardViews.isEmpty()) {
			logger.error("Card views list is emtpy".toUpperCase());
			throw new NoSuchElementException("Card views list is emtpy");
		}
		
		List<RankingForHomeDTO> rankingViews = cardViews.stream().map(card -> {
			String cardName = this.returnCardName(Long.parseLong(card.getCardNumber()));
			
			RankingForHomeDTO cardRankingViews = new RankingForHomeDTO();
			
			cardRankingViews.setCardName(cardName);
			cardRankingViews.setCardNumber(card.getCardNumber());
			cardRankingViews.setQtdViewsWeekly(card.getQtdViewsWeekly());
			
			return cardRankingViews;
		}).collect(Collectors.toList());
		
		return rankingViews;
				
	}
	
	private String returnCardName(Long cardNumber) {
		
		if(cardNumber == null || cardNumber == 0) {
			logger.error("#rankingViews: Invalid card number".toUpperCase());
			throw new IllegalArgumentException("#rankingViews: Invalid card number");
		}
		
		Card card = cardRepository.findByNumero(cardNumber);
		
		if(card == null) {
			logger.error("Card not found. Number: " + cardNumber + "".toUpperCase());
			throw new EntityNotFoundException("Card not found. Number: " + cardNumber);
		}
		
		return card.getNome();
	}

	private CardViewsInformation consultCardViews(Long cardNumber) {
		
		this.validCardNumber(cardNumber);
		
		CardViewsInformation views = cardViewsRepository.findByCardNumber(String.valueOf(cardNumber));
		
		return views;
	}

	@Override
	@Transactional
	public CardViewsInformation updateCardViewsOrInsertInDB(Long cardNumber) {
		this.validCardNumber(cardNumber);
		
		CardViewsInformation views = this.consultCardViews(cardNumber);
		
		if(views != null && views.getId() > 0) {
			views.setQtdViewsWeekly(views.getQtdViewsWeekly() + 1);
			views.setTotalQtdViews(views.getTotalQtdViews() + 1);
			views.setLastUpdate(LocalDateTime.now());
			views = cardViewsRepository.save(views);
			
		} else {
			views = this.insertCardViews(cardNumber);
		}	
		
		return views;
	}


	private CardViewsInformation insertCardViews(Long cardNumber) {
		
		this.validCardNumber(cardNumber);
		
		CardViewsInformation views = new CardViewsInformation();
		views.setCardNumber(String.valueOf(cardNumber));
		views.setQtdViewsWeekly(1L);
		views.setTotalQtdViews(1L);
		views.setLastUpdate(LocalDateTime.now());
		
		views = cardViewsRepository.save(views);
		
		if(views == null || views.getId() == null) {
			logger.error("It was not possible save card views".toUpperCase());
			throw new RuntimeException("It was not possible save card views");
		}
		
		return views;
	}
	
	private void validCardNumber(Long cardNumber) {
		
		if(cardNumber == null || cardNumber == 0)
			throw new IllegalArgumentException("Invalid Card Number: " + cardNumber);
		
		if (cardRepository.findByNumero(cardNumber) == null ) 
			throw new EntityNotFoundException("Can't find card with number: " + cardNumber);
		
	}
	
}
