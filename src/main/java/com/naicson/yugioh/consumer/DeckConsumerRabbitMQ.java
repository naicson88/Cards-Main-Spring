package com.naicson.yugioh.consumer;

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
import com.naicson.yugioh.data.dto.KonamiDeck;
import com.naicson.yugioh.entity.Deck;
import com.naicson.yugioh.service.card.CardRegistry;
import com.naicson.yugioh.service.deck.DeckServiceImpl;
import com.naicson.yugioh.service.deck.RelDeckCardsServiceImpl;
import com.naicson.yugioh.util.exceptions.ErrorMessage;

@Component
public class DeckConsumerRabbitMQ {
	
	@Autowired
	RelDeckCardsServiceImpl relDeckCardsService;	
	@Autowired
	DeckServiceImpl deckService;	
	@Autowired
	CardRegistry cardRegistry;
	@Autowired
	ConsumerUtils consumerUtils;
	
	Logger logger = LoggerFactory.getLogger(DeckConsumerRabbitMQ.class);
	
	//@RabbitListener(queues = "${rabbitmq.queue.deck}")
	@Transactional(rollbackFor = Exception.class)
	private void consumer(String json) {		
			
		logger.info("Start consuming new KonamiDeck: {}" , json);
		
		KonamiDeck kDeck = convertJsonToSetCollectionDto(json);
		
		cardRegistry.registryCardFromYuGiOhAPI(kDeck.getCardsToBeRegistered());
		
		Deck newDeck = consumerUtils.createNewDeck(kDeck);
		
		newDeck = deckService.countQtdCardRarityInTheDeck(newDeck);
		
		Long deckId = deckService.saveKonamiDeck(newDeck).getId();
		
		newDeck = consumerUtils.setDeckIdInRelDeckCards(newDeck, deckId);
		
		relDeckCardsService.saveRelDeckCards(newDeck.getRel_deck_cards());
		
		logger.info("Deck successfully saved! {}", newDeck.getNome());
						
	}
	
	private KonamiDeck convertJsonToSetCollectionDto(String json) {
			
		try {
			
			ObjectMapper mapper = new ObjectMapper();
			mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
			mapper.configure(DeserializationFeature.USE_JAVA_ARRAY_FOR_JSON_ARRAY, true);
			
			KonamiDeck dto = mapper.readValue(json, KonamiDeck.class);
			
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
