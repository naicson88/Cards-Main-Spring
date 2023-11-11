package com.naicson.yugioh.consumer;

import cardscommons.dto.KonamiDeckDTO;
import com.naicson.yugioh.data.builders.DeckBuilder;
import com.naicson.yugioh.data.composite.JsonConverterValidationFactory;
import com.naicson.yugioh.data.facade.consumer.RabbitMQConsumerFacade;
import com.naicson.yugioh.entity.Deck;
import com.naicson.yugioh.entity.LogEntity;
import com.naicson.yugioh.util.enums.SetType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.ZonedDateTime;
import java.util.Date;

@Component
public class DeckConsumerRabbitMQ {

	@Autowired
	RabbitMQConsumerFacade facade;
	
	Logger logger = LoggerFactory.getLogger(DeckConsumerRabbitMQ.class);
	
	@RabbitListener(queues = "${rabbitmq.queue.deck}", autoStartup = "${rabbitmq.autostart.consumer}")
	@Transactional(rollbackFor = {Exception.class})
	public void consumer(String json) {		

		try {
			logger.info("Start consuming new KonamiDeck: {}" , json);

			KonamiDeckDTO kDeck = (KonamiDeckDTO) facade.convertJsonToDTO(json, JsonConverterValidationFactory.KONAMI_DECK);

			if(!facade.findDeckByNome(kDeck.getNome()).isEmpty())
				throw new DuplicateKeyException("Deck already registered with that name: " + kDeck.getNome());

			facade.registryCardFromYuGiOhAPI(kDeck.getCardsToBeRegistered());

			Deck newDeck = this.createNewDeck(kDeck);

			newDeck = facade.saveDeckProcess(newDeck);

			logger.info("Deck successfully saved! {}", newDeck.getNome());
		} catch (Exception e){
			logger.error(e.getMessage());
			facade.saveLogEntity(e ,HttpStatus.INTERNAL_SERVER_ERROR, "DECK_QUEUE");
			throw  e;
		}
	}

	public Deck createNewDeck(KonamiDeckDTO kDeck) {
		logger.info("Creating New Deck...");

		if (kDeck == null)
			throw new IllegalArgumentException("Informed Konami Deck is invalid!");

		return DeckBuilder.builder()
			.dt_criacao(new Date())
			.imagem(kDeck.getImagem())
			.lancamento(kDeck.getLancamento())
			.nome(kDeck.getNome().trim())
			.relDeckCardsFromDTO(kDeck.getRelDeckCards())
			.setType(SetType.valueOf(kDeck.getSetType()))
			.isSpeedDuel(kDeck.getIsSpeedDuel())
			.imgurUrl(kDeck.getImagem())
			.isBasedDeck(kDeck.getIsBasedDeck())
			.setCode(kDeck.getSetCode())
			.description(kDeck.getDescription())
			.build();
	}
	
}
