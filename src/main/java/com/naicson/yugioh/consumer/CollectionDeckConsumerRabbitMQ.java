package com.naicson.yugioh.consumer;

import java.util.Date;

import org.apache.commons.lang3.StringUtils;
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
import com.naicson.yugioh.data.builders.DeckBuilder;
import com.naicson.yugioh.data.dto.CollectionDeck;
import com.naicson.yugioh.entity.Deck;
import com.naicson.yugioh.entity.sets.SetCollection;
import com.naicson.yugioh.service.card.CardRegistry;
import com.naicson.yugioh.service.deck.DeckServiceImpl;
import com.naicson.yugioh.service.deck.RelDeckCardsServiceImpl;
import com.naicson.yugioh.service.setcollection.SetCollectionServiceImpl;
import com.naicson.yugioh.util.exceptions.ErrorMessage;

@Component
public class CollectionDeckConsumerRabbitMQ {
	@Autowired
	CardRegistry cardRegistry;
	@Autowired
	ConsumerUtils consumerUtils;
	@Autowired
	SetCollectionServiceImpl setCollectionService;
	@Autowired
	DeckServiceImpl deckService;
	@Autowired
	RelDeckCardsServiceImpl relDeckCardsService;	
	
	Logger logger = LoggerFactory.getLogger(CollectionDeckConsumerRabbitMQ.class);
	
	@RabbitListener(queues = "${rabbitmq.queue.deckcollection}", autoStartup = "${rabbitmq.autostart.consumer}")
	@Transactional(rollbackFor = {Exception.class, ErrorMessage.class})
	private void consumer (String json) {
		logger.info("Start consuming new CollectionDeck: {}" , json);
		
		CollectionDeck cDeck = this.convertJsonToSetCollectionDto(json);
		
		cardRegistry.registryCardFromYuGiOhAPI(cDeck.getCardsToBeRegistered());
		
		Deck newDeck = this.crateNewDeckOfCollection(cDeck);
		
		newDeck.setRel_deck_cards(consumerUtils.setRarity(newDeck.getRel_deck_cards()));
		
		newDeck = deckService.countQtdCardRarityInTheDeck(newDeck);
		
		Long deckId = deckService.saveKonamiDeck(newDeck).getId();
		
		newDeck = consumerUtils.setDeckIdInRelDeckCards(newDeck, deckId);
			
		relDeckCardsService.saveRelDeckCards(newDeck.getRel_deck_cards());
		
		this.setCollectionService.saveSetDeckRelation(cDeck.getSetId().longValue(), deckId);
		
		logger.info("Deck successfully saved! {}", newDeck.getNome());
				
	}
	
	private Deck crateNewDeckOfCollection(CollectionDeck cDeck) {
		
		if(cDeck == null || cDeck.getSetId() < 1)
			throw new IllegalArgumentException("Invalid Deck of SetCollection");
		
		SetCollection set = setCollectionService.findById(cDeck.getSetId());
		
		cDeck.getListRelDeckCards().forEach(rel -> rel.setIsSpeedDuel(set.getIsSpeedDuel()));
		
		return DeckBuilder.builder()
		.dt_criacao(new Date())
		.imagem(StringUtils.isBlank(cDeck.getImagem()) ? set.getImgPath() : cDeck.getImagem())
		.lancamento(set.getReleaseDate())
		.nome(cDeck.getNome())
		.setType(set.getSetCollectionType())
		.isSpeedDuel(set.getIsSpeedDuel())
		.imgurUrl(StringUtils.isBlank(cDeck.getImagem()) ?set.getImgurUrl(): cDeck.getImagem() )
		.isBasedDeck(cDeck.getIsBasedDeck())
		.setCode(set.getSetCode())
		.relDeckCards(cDeck.getListRelDeckCards())
		.build();

	}
	
	private CollectionDeck convertJsonToSetCollectionDto(String json) {		
		try {
			
			ObjectMapper mapper = new ObjectMapper();
			mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
			mapper.configure(DeserializationFeature.USE_JAVA_ARRAY_FOR_JSON_ARRAY, true);
			
			CollectionDeck dto = mapper.readValue(json, CollectionDeck.class);
			
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
