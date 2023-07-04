package com.naicson.yugioh.data.facade.consumer;

import com.naicson.yugioh.consumer.ConsumerUtils;
import com.naicson.yugioh.data.dto.CardYuGiOhAPI;
import com.naicson.yugioh.entity.Card;
import com.naicson.yugioh.entity.Deck;
import com.naicson.yugioh.entity.RelDeckCards;
import com.naicson.yugioh.service.card.CardRegistry;
import com.naicson.yugioh.service.deck.DeckServiceImpl;
import com.naicson.yugioh.service.deck.RelDeckCardsServiceImpl;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class RabbitMQConsumerFacade {
    RelDeckCardsServiceImpl relDeckCardService;
    ConsumerUtils consumerUtils;
    CardRegistry cardRegistry;
    DeckServiceImpl deckService;

    public RabbitMQConsumerFacade(RelDeckCardsServiceImpl relDeckCardService, ConsumerUtils consumerUtils, CardRegistry cardRegistry,
                                  DeckServiceImpl deckService){
        this.relDeckCardService = relDeckCardService;
        this.consumerUtils = consumerUtils;
        this.cardRegistry = cardRegistry;
        this.deckService = deckService;
    }

    public void saveRelDeckCards(List<RelDeckCards> listRelDeckCards){
        relDeckCardService.saveRelDeckCards(listRelDeckCards);
    }

    public Object convertJsonToDTO(String json, String obj){
        return consumerUtils.convertJsonToDTO(json, obj);
    }

    public Card registryCardFromYuGiOhAPI(List<CardYuGiOhAPI> cardsToBeRegistered){
        return cardRegistry.registryCardFromYuGiOhAPI(cardsToBeRegistered).get(0);
    }

    public Deck countCardRaritiesAndSaveKonamiDeck(Deck newDeck){
        newDeck = deckService.countCardRaritiesOnDeck(newDeck);
        return deckService.saveKonamiDeck(newDeck);
    }
}
