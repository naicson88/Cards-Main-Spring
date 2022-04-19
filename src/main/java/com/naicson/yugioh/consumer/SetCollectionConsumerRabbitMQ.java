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
import com.naicson.yugioh.data.dto.set.SetCollectionDto;
import com.naicson.yugioh.entity.Deck;
import com.naicson.yugioh.entity.sets.SetCollection;
import com.naicson.yugioh.service.DeckServiceImpl;
import com.naicson.yugioh.service.RelDeckCardsServiceImpl;
import com.naicson.yugioh.service.SetCollectionServiceImpl;
import com.naicson.yugioh.service.card.CardRegistry;
import com.naicson.yugioh.util.exceptions.ErrorMessage;

@Component
public class SetCollectionConsumerRabbitMQ {
	
	@Autowired
	CardRegistry cardRegistry;
	
	@Autowired
	DeckServiceImpl deckService;
	
	@Autowired
	RelDeckCardsServiceImpl relDeckCardsService;
	
	@Autowired
	SetCollectionServiceImpl setColService;
	
	Logger logger = LoggerFactory.getLogger(DeckConsumerRabbitMQ.class);
		
	@RabbitListener(queues = "${rabbitmq.queue.setcollection}")
	@Transactional(rollbackFor = Exception.class)
	private void consumerSetCollectionQueue(String json) {
		
		try {	
			
			SetCollectionDto setCollection = convertJsonToSetCollectionDto(json);
			
			SetCollection setCollectionEntity = SetCollection.setCollectionDtoToEntity(setCollection);
		
			setCollectionEntity.setName("");//teste
			
			setCollectionEntity = setColService.saveSetCollection(setCollectionEntity);
			
			setCollection.getDecks().stream()
			.filter(deck -> deck.getCardsToBeRegistered().size() > 0)
			.forEach(deck -> cardRegistry.RegistryCardFromYuGiOhAPI(deck));
			
			setCollection.getDecks().stream().forEach(konamiDeck -> {
				
				Deck newDeck = ConsumerUtils.createNewDeck(konamiDeck);
				
				newDeck = deckService.countQtdCardRarityInTheDeck(newDeck);
				
				Long deckId = deckService.saveKonamiDeck(newDeck).getId();
				
				newDeck = ConsumerUtils.setDeckIdInRelDeckCards(newDeck, deckId);
				
				relDeckCardsService.saveRelDeckCards(newDeck.getRel_deck_cards());
				
				logger.info("Deck successfully saved! Deck: {}" , newDeck.toString());
				
			});
			
			
		}catch (Exception e) {
			logger.error("Error consuming Set Collection: {} " , e.getMessage());
		}
	}

	private SetCollectionDto convertJsonToSetCollectionDto(String json) {
		
		try {
			
			ObjectMapper mapper = new ObjectMapper();
			mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
			mapper.configure(DeserializationFeature.USE_JAVA_ARRAY_FOR_JSON_ARRAY, true);
			
			SetCollectionDto dto = mapper.readValue(json, SetCollectionDto.class);
			
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
