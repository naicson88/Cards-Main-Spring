package com.naicson.yugioh.consumer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

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
	
	@RabbitListener(queues = "${rabbitmq.queue.deck}", autoStartup = "${rabbitmq.autostart.consumer}")
	@Transactional(rollbackFor = {Exception.class, ErrorMessage.class})
	private void consumer(String json) {		
			
		logger.info("Start consuming new KonamiDeck: {}" , json);
		
		KonamiDeck kDeck = (KonamiDeck) consumerUtils.convertJsonToSetCollectionDto(json, ConsumerUtils.KONAMI_DECK);
		
		if(!deckService.findByNome(kDeck.getNome()).isEmpty())
			throw new ErrorMessage("Deck already registered with that name: " + kDeck.getNome());
		
		cardRegistry.registryCardFromYuGiOhAPI(kDeck.getCardsToBeRegistered());
		
		Deck newDeck = consumerUtils.createNewDeck(kDeck);
		
		newDeck.setRel_deck_cards(consumerUtils.setRarity(kDeck.getListRelDeckCards()));
		
		newDeck = deckService.countQtdCardRarityInTheDeck(newDeck);
		
		Long deckId = deckService.saveKonamiDeck(newDeck).getId();
		
		newDeck = consumerUtils.setDeckIdInRelDeckCards(newDeck, deckId);
		
		relDeckCardsService.saveRelDeckCards(newDeck.getRel_deck_cards());
		
		logger.info("Deck successfully saved! {}", newDeck.getNome());
						
	}
	
}
