package com.naicson.yugioh.consumer;

import java.util.Date;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.naicson.yugioh.data.dto.cards.AddNewCardToDeckDTO;
import com.naicson.yugioh.entity.Card;
import com.naicson.yugioh.entity.RelDeckCards;
import com.naicson.yugioh.repository.CardAlternativeNumberRepository;
import com.naicson.yugioh.service.card.CardRegistry;
import com.naicson.yugioh.service.card.CardServiceImpl;
import com.naicson.yugioh.service.deck.RelDeckCardsServiceImpl;
import com.naicson.yugioh.util.exceptions.ErrorMessage;

@Component
public class CardConsumerRabbitMQ {
	
	@Autowired
	CardRegistry cardRegistry;
	
	@Autowired
	CardAlternativeNumberRepository cardAlternativeNumberRepository;
	
	@Autowired
	RelDeckCardsServiceImpl relDeckCardService;
	
	@Autowired
	CardServiceImpl cardService;
	
	Logger logger = LoggerFactory.getLogger(CollectionDeckConsumerRabbitMQ.class);
	
	@RabbitListener(queues = "${rabbitmq.queue.card}", autoStartup = "${rabbitmq.autostart.consumer}")
	@Transactional(rollbackFor = Exception.class)
	public void consumer(String json) {
		logger.info("Start saving Card on Deck: {}" , json);
		
		AddNewCardToDeckDTO card = this.convertJsonToNewCardDTO(json);	
		
		Long cardId = this.verifyIfCardIsAlreadyRegistered(card);
			
		RelDeckCards cardToBeAdded = this.createRelDeckCards(cardId, card);
		
		relDeckCardService.saveRelDeckCards(List.of(cardToBeAdded));	
		
		logger.info("Card successfully saved on Deck: {}" , cardToBeAdded.getCardNumber());
	}
	
	private Long verifyIfCardIsAlreadyRegistered(AddNewCardToDeckDTO card) {
		Card cardFound = cardAlternativeNumberRepository.findCardByCardNumber(card.getNumber());
		
		if(cardFound == null)
			cardFound = cardService.findByCardNome(card.getName());
		
		if(cardFound == null) 
			cardFound = cardRegistry.registryCardFromYuGiOhAPI(card.getCardsToBeRegistered()).get(0);
		
		return cardFound.getId().longValue();
	}
	
	private RelDeckCards createRelDeckCards(Long cardId, AddNewCardToDeckDTO card) {
		RelDeckCards rel = new RelDeckCards();
		rel.setCard_price(card.getPrice());
		rel.setCardSetCode(card.getCardSetCode());
		rel.setCardId(cardId.intValue());
		rel.setCardNumber(card.getNumber());
		rel.setDeckId(card.getDeckId());
		rel.setDt_criacao(new Date());
		rel.setIsSideDeck(false);
		rel.setIsSpeedDuel(card.getIsSpeedDuel());
		rel.setQuantity(1);
		rel.setCard_raridade(card.getRarity());
		rel.setSetRarityCode(card.getRarityCode());
		rel.setRarityDetails(card.getRarityDetails());
		
		return rel;
				
	}

	private AddNewCardToDeckDTO convertJsonToNewCardDTO(String json) {
	try {			
			ObjectMapper mapper = new ObjectMapper();
			mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
			mapper.configure(DeserializationFeature.USE_JAVA_ARRAY_FOR_JSON_ARRAY, true);		
			AddNewCardToDeckDTO dto = mapper.readValue(json, AddNewCardToDeckDTO.class);	
			return dto;
			
		} catch (JsonMappingException e) {
			e.printStackTrace();
			throw new ErrorMessage(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
			
		} catch (JsonProcessingException e) {
			e.printStackTrace();
			throw new ErrorMessage(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
		}
	}
}
