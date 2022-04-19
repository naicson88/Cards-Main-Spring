package com.naicson.yugioh.consumer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.naicson.yugioh.data.dto.KonamiDeck;
import com.naicson.yugioh.entity.Deck;
import com.naicson.yugioh.service.DeckServiceImpl;
import com.naicson.yugioh.service.RelDeckCardsServiceImpl;
import com.naicson.yugioh.service.card.CardRegistry;

@Component
public class DeckConsumerRabbitMQ {
	
	@Autowired
	RelDeckCardsServiceImpl relDeckCardsService;
	
	@Autowired
	DeckServiceImpl deckService;
	
	@Autowired
	CardRegistry cardRegistry;
	
	Logger logger = LoggerFactory.getLogger(DeckConsumerRabbitMQ.class);
	
	@RabbitListener(queues = "${rabbitmq.queue.deck}")
	@Transactional(rollbackFor = Exception.class)
	private void consumer(KonamiDeck kDeck) {
		
		try {
			
			cardRegistry.RegistryCardFromYuGiOhAPI(kDeck);
			
			Deck newDeck = ConsumerUtils.createNewDeck(kDeck);
			
			newDeck = deckService.countQtdCardRarityInTheDeck(newDeck);
			
			Long deckId = deckService.saveKonamiDeck(newDeck).getId();
			
			newDeck = ConsumerUtils.setDeckIdInRelDeckCards(newDeck, deckId);
			
			relDeckCardsService.saveRelDeckCards(newDeck.getRel_deck_cards());
			
			logger.info("Deck successfully saved!");
			
		} catch(Exception e) {
			logger.error("DeckConsumer: " + e.getLocalizedMessage());
		}			
	}
	
//	private Deck createNewDeck(KonamiDeck kDeck) {
//		
//		if(kDeck == null) 
//			throw new IllegalArgumentException("Informed Konami Deck is invalid!");
//		
//		
//		Deck deck = new Deck();
//		deck.setDt_criacao(new Date());
//		deck.setImagem(kDeck.getImagem());
//		deck.setLancamento(kDeck.getLancamento());
//		deck.setNome(this.adjustDeckName(kDeck.getNome()));
//		deck.setNomePortugues(kDeck.getNomePortugues());
//		deck.setRel_deck_cards(kDeck.getListRelDeckCards());
//		deck.setSetType(kDeck.getSetType());
//		
//		logger.info("Deck created!");
//		return deck;
//	}
	
//	private Deck setDeckIdInRelDeckCards(Deck newDeck, Long deckId) {
//		
//		if(deckId == null || deckId == 0) {
//			throw new IllegalArgumentException("Generated Deck Id is invalid.");
//		}
//		
//		newDeck.getRel_deck_cards().stream().forEach(rel -> {rel.setDeckId(deckId);});
//		logger.info("Deck's Id setted");
//		return newDeck;
//	}
	
//	private String adjustDeckName(String rawName) {
//		
//		if(StringUtils.containsIgnoreCase(rawName,"Structure" )) {
//			rawName = rawName.replace("Structure", "");
//
//			if(StringUtils.containsIgnoreCase(rawName,"Deck"))
//				rawName = rawName.replace("Deck", "");
//	
//			if(StringUtils.containsIgnoreCase(rawName,":"))
//				rawName = rawName.replace(":", "");
//		}
//					
//		return rawName.trim();
//	}
	
}
