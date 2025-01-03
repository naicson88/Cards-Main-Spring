package com.naicson.yugioh.consumer;

import cardscommons.dto.AddNewCardToDeckDTO;
import com.naicson.yugioh.data.composite.JsonConverterValidationFactory;
import com.naicson.yugioh.data.facade.consumer.ConsumerFacade;
import com.naicson.yugioh.entity.Card;
import com.naicson.yugioh.entity.RelDeckCards;
import com.naicson.yugioh.service.card.CardAlternativeNumberService;
import com.naicson.yugioh.service.card.CardServiceImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

@Component
public class CardConsumerRabbitMQ {

	@Autowired
	CardAlternativeNumberService alternativeService;

	@Autowired
	CardServiceImpl cardService;

	@Autowired
	ConsumerFacade facade;

	private  final static String CARD_QUEUE = "CARD_QUEUE";

	Logger logger = LoggerFactory.getLogger(CardConsumerRabbitMQ.class);

	@RabbitListener(queues = CARD_QUEUE, autoStartup = "${rabbitmq.autostart.consumer}")
	@Transactional(rollbackFor = { Exception.class })
	public void consumer(String json) {
		logger.info(" Start saving Card on Deck: {}", json);
		cardConsumer(json);
	}

	@KafkaListener(topics = CARD_QUEUE, groupId = "cards-main-ms")
	@Transactional(rollbackFor = { Exception.class })
	public void kafkaConsumer(String message){
		logger.info(" -> Consuming from Kafka {}", message);
		cardConsumer(message);
	}

	private void cardConsumer(String json) {
		AddNewCardToDeckDTO card = (AddNewCardToDeckDTO) facade
				.convertJsonToDTO(json, JsonConverterValidationFactory.ADD_NEW_CARD);

		Long cardId = this.verifyIfCardIsAlreadyRegistered(card);

		RelDeckCards cardToBeAdded = this.createRelDeckCards(cardId, card);

		facade.saveRelDeckCards(List.of(cardToBeAdded));

		logger.info("Card successfully saved on Deck: {}", cardToBeAdded.getCardNumber());
	}

	private Long verifyIfCardIsAlreadyRegistered(AddNewCardToDeckDTO card) {
		Card cardFound = alternativeService.findCardByCardNumber(card.getNumber());

		if (cardFound == null)
			cardFound = cardService.findByCardNome(card.getName());

		if (cardFound == null)
			cardFound = facade.registryCardFromYuGiOhAPI(card.getCardsToBeRegistered());


		return cardFound.getId().longValue();
	}

	private RelDeckCards createRelDeckCards(Long cardId, AddNewCardToDeckDTO card) {

		return new RelDeckCards.RelDeckCardsBuilder(
				card.getDeckId(),
				card.getNumber(), 
				card.getCardSetCode(),
				card.getPrice(),
				card.getRarity(),
				new Date(),
				false,
				false, 
				cardId.intValue(),
				1,
				card.getRarityCode(),
				card.getRarityDetails())
				.build();
	}

}
