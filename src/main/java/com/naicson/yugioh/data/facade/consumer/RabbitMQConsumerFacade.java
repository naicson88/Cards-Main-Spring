package com.naicson.yugioh.data.facade.consumer;

import cardscommons.dto.CardYuGiOhAPI;
import com.naicson.yugioh.consumer.ConsumerUtils;
import com.naicson.yugioh.consumer.DeckConsumerRabbitMQ;
import com.naicson.yugioh.entity.Card;
import com.naicson.yugioh.entity.Deck;
import com.naicson.yugioh.entity.LogEntity;
import com.naicson.yugioh.entity.RelDeckCards;
import com.naicson.yugioh.repository.LogEntityRepository;
import com.naicson.yugioh.service.card.CardRegistry;
import com.naicson.yugioh.service.deck.DeckServiceImpl;
import com.naicson.yugioh.service.deck.RelDeckCardsServiceImpl;
import com.naicson.yugioh.util.ApiExceptionHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Component
public class RabbitMQConsumerFacade {
    RelDeckCardsServiceImpl relDeckCardService;
    ConsumerUtils consumerUtils;
    CardRegistry cardRegistry;
    DeckServiceImpl deckService;
    LogEntityRepository logRepository;
    ApiExceptionHandler exceptionHandler;

    Logger logger = LoggerFactory.getLogger(RabbitMQConsumerFacade.class);
    public RabbitMQConsumerFacade(RelDeckCardsServiceImpl relDeckCardService, ConsumerUtils consumerUtils, CardRegistry cardRegistry, LogEntityRepository logRepository,
                                  DeckServiceImpl deckService, ApiExceptionHandler exceptionHandler){
        this.relDeckCardService = relDeckCardService;
        this.consumerUtils = consumerUtils;
        this.cardRegistry = cardRegistry;
        this.deckService = deckService;
        this.logRepository = logRepository;
        this.exceptionHandler = exceptionHandler;
    }
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void saveLog(LogEntity entity){ logRepository.save(entity);}
    public void saveRelDeckCards(List<RelDeckCards> listRelDeckCards){
        relDeckCardService.saveRelDeckCards(listRelDeckCards);
    }

    public void saveLogEntity(Exception e, HttpStatus http, String url){
        exceptionHandler.saveLogEntity(e, http, url);
    }

    public Object convertJsonToDTO(String json, String obj){
        return consumerUtils.convertJsonToDTO(json, obj);
    }

    public Card registryCardFromYuGiOhAPI(List<CardYuGiOhAPI> cardsToBeRegistered){
        return cardRegistry.registryCardFromYuGiOhAPI(cardsToBeRegistered).get(0);
    }

    public Deck saveDeckProcess(Deck newDeck ) {
        logger.info("Saving Deck process...");
        newDeck.setRel_deck_cards(consumerUtils.setRarity(newDeck.getRel_deck_cards()));

        newDeck = deckService.countCardRaritiesOnDeck(newDeck);

        newDeck = deckService.saveKonamiDeck(newDeck);

        newDeck = consumerUtils.setDeckIdInRelDeckCards(newDeck, newDeck.getId());

        relDeckCardService.saveRelDeckCards(newDeck.getRel_deck_cards());

        return newDeck;
    }

    public List<Deck> findDeckByNome(String deckName){
        return deckService.findByNome(deckName);
    }


}
