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
import com.naicson.yugioh.entity.sets.SetCollection;
import com.naicson.yugioh.service.card.CardRegistry;
import com.naicson.yugioh.service.deck.DeckServiceImpl;
import com.naicson.yugioh.service.deck.RelDeckCardsServiceImpl;
import com.naicson.yugioh.service.setcollection.SetCollectionServiceImpl;
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
	
	@Autowired
	ConsumerUtils consumerUtils;
	
	Logger logger = LoggerFactory.getLogger(DeckConsumerRabbitMQ.class);
		
	@RabbitListener(queues = "${rabbitmq.queue.setcollection}", autoStartup = "${rabbitmq.autostart.consumer}")
	@Transactional(rollbackFor = {Exception.class, ErrorMessage.class})
	private void consumerSetCollectionQueue(String json) {
			
		logger.info("Start consuming new Set Collection: {}" , json);
		
		SetCollectionDto setCollection = (SetCollectionDto) consumerUtils.convertJsonToSetCollectionDto(json, consumerUtils.SET_COLLECTION);
		
		SetCollection setCollectionEntity = SetCollection.setCollectionDtoToEntity(setCollection);
	
		setCollectionEntity = setColService.saveSetCollection(setCollectionEntity);
			
		logger.info("Registered Set Collection: {}", setCollectionEntity.toString() );			
			
	}

}
