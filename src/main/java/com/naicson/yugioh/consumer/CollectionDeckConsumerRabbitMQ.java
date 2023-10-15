package com.naicson.yugioh.consumer;

import cardscommons.dto.CollectionDeckDTO;
import com.naicson.yugioh.data.builders.DeckBuilder;
import com.naicson.yugioh.data.composite.JsonConverterValidationFactory;
import com.naicson.yugioh.data.facade.consumer.RabbitMQConsumerFacade;
import com.naicson.yugioh.entity.Deck;
import com.naicson.yugioh.entity.sets.SetCollection;
import com.naicson.yugioh.service.setcollection.SetCollectionServiceImpl;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;

@Component
public class CollectionDeckConsumerRabbitMQ {
	@Autowired
	SetCollectionServiceImpl setCollectionService;

	@Autowired
	RabbitMQConsumerFacade facade;
	
	Logger logger = LoggerFactory.getLogger(CollectionDeckConsumerRabbitMQ.class);
	
	@RabbitListener(queues = "${rabbitmq.queue.deckcollection}", autoStartup = "${rabbitmq.autostart.consumer}")
	@Transactional(rollbackFor = {Exception.class})
	public void consumer (String json) {
		logger.info("Start consuming new CollectionDeck: {}" , json);
		
		CollectionDeckDTO cDeck = (CollectionDeckDTO) facade.convertJsonToDTO(json, JsonConverterValidationFactory.COLLECTION_DECK);
		
		if(cDeck.getSetId() == null)
			throw new IllegalArgumentException("Invalid Set ID");
		
		if(cDeck.getCardsToBeRegistered() != null && !cDeck.getCardsToBeRegistered().isEmpty())
			facade.registryCardFromYuGiOhAPI(cDeck.getCardsToBeRegistered());
		
		Deck newDeck = this.crateNewDeckOfCollection(cDeck);

		newDeck = facade.saveDeckProcess(newDeck);
		
		this.setCollectionService.saveSetDeckRelation(cDeck.getSetId().longValue(), newDeck.getId());
		
		logger.info("Collection Deck successfully saved! {}", newDeck.getNome());
				
	}
	
	private Deck crateNewDeckOfCollection(CollectionDeckDTO cDeck) {
		
		if(cDeck == null || cDeck.getSetId() < 1)
			throw new IllegalArgumentException("Invalid Deck of SetCollection");
		
		SetCollection set = setCollectionService.findById(cDeck.getSetId());
		
		cDeck.getRelDeckCards().forEach(rel -> rel.setIsSpeedDuel(set.getIsSpeedDuel()));
		
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
		.relDeckCardsFromDTO(cDeck.getRelDeckCards())
		.build();

	}
}
