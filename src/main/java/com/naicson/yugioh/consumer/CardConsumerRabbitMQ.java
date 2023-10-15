package com.naicson.yugioh.consumer;

import java.util.Date;
import java.util.List;

import cardscommons.dto.AddNewCardToDeckDTO;
import com.naicson.yugioh.data.facade.consumer.RabbitMQConsumerFacade;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.naicson.yugioh.data.composite.JsonConverterValidationFactory;
import com.naicson.yugioh.entity.Card;
import com.naicson.yugioh.entity.RelDeckCards;
import com.naicson.yugioh.service.card.CardAlternativeNumberService;
import com.naicson.yugioh.service.card.CardServiceImpl;

@Component
public class CardConsumerRabbitMQ {

	@Autowired
	CardAlternativeNumberService alternativeService;

	@Autowired
	CardServiceImpl cardService;

	@Autowired
	RabbitMQConsumerFacade facade;

	Logger logger = LoggerFactory.getLogger(CardConsumerRabbitMQ.class);

	@RabbitListener(queues = "${rabbitmq.queue.card}", autoStartup = "${rabbitmq.autostart.consumer}")
	@Transactional(rollbackFor = { Exception.class })
	public void consumer(String json) {
		logger.info(" Start saving Card on Deck: {}", json);

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
			cardFound = facade.registryCardFromYuGiOhAPI(card.getCardsToBeRegistered());//.get(0);


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
