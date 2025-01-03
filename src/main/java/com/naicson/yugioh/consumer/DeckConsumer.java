package com.naicson.yugioh.consumer;

import cardscommons.dto.KonamiDeckDTO;
import com.naicson.yugioh.data.builders.DeckBuilder;
import com.naicson.yugioh.data.composite.JsonConverterValidationFactory;
import com.naicson.yugioh.data.facade.consumer.ConsumerFacade;
import com.naicson.yugioh.entity.Deck;
import com.naicson.yugioh.util.enums.SetType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.HttpStatus;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;

@Component
public class DeckConsumer {

	@Autowired
	ConsumerFacade facade;

	private final static String DECK_QUEUE = "DECK_QUEUE";
	
	Logger logger = LoggerFactory.getLogger(DeckConsumer.class);
	
	@RabbitListener(queues = DECK_QUEUE, autoStartup = "${rabbitmq.autostart.consumer}")
	@Transactional(rollbackFor = { Exception.class })
	public void consumer(String json) {		
		deckQueueConsumer(json);
	}

	@KafkaListener(topics = DECK_QUEUE, groupId = "cards-main-ms")
	@Transactional(rollbackFor = { Exception.class })
	public void kafkaConsumer(String message){
		logger.info(" -> Consuming from Kafka {}", message);
		deckQueueConsumer(message);
	}

	public void deckQueueConsumer(String json) {
		try {
			logger.info("Start consuming new KonamiDeck: {}" , json);

			KonamiDeckDTO kDeck = (KonamiDeckDTO) facade.convertJsonToDTO(json, JsonConverterValidationFactory.KONAMI_DECK);

			if(!facade.findDeckByNome(kDeck.getNome()).isEmpty())
				throw new DuplicateKeyException("Deck already registered with that name: " + kDeck.getNome());

			facade.registryCardFromYuGiOhAPI(kDeck.getCardsToBeRegistered());

			Deck newDeck = this.createNewDeck(kDeck);

			newDeck = facade.saveDeckProcess(newDeck);

			logger.info("Deck successfully saved! {}", newDeck.getNome());
		} catch (DuplicateKeyException e){
			logger.error(e.getMessage());
			facade.saveLogEntity(e ,HttpStatus.INTERNAL_SERVER_ERROR, DECK_QUEUE);
		}
		catch (Exception e){
			logger.error(e.getMessage());
			facade.saveLogEntity(e ,HttpStatus.INTERNAL_SERVER_ERROR, DECK_QUEUE);
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
