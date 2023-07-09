package com.naicson.yugioh.consumer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.naicson.yugioh.data.composite.JsonConverterValidationComposite;
import com.naicson.yugioh.data.composite.JsonConverterValidationFactory;
import com.naicson.yugioh.entity.Deck;
import com.naicson.yugioh.entity.RelDeckCards;
import com.naicson.yugioh.service.card.CardAlternativeNumberService;
import com.naicson.yugioh.util.enums.ECardRarity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@SuppressWarnings("rawtypes")
public class ConsumerUtils {

	@Autowired
	CardAlternativeNumberService alternativeService;

	Logger logger = LoggerFactory.getLogger(ConsumerUtils.class);

	public Deck setDeckIdInRelDeckCards(Deck newDeck, Long deckId) {

		if (deckId == null || deckId == 0)
			throw new IllegalArgumentException("Generated Deck Id is invalid.");

		newDeck.getRel_deck_cards().forEach(rel -> {
			rel.setDeckId(deckId);
			rel.setQuantity(1);
			rel.setCardId(alternativeService.findByCardAlternativeNumber(rel.getCardNumber()).getCardId());
		});

		return newDeck;
	}

	public List<RelDeckCards> setRarity(List<RelDeckCards> listRelDeckCards) {

		if (listRelDeckCards == null)
			throw new IllegalArgumentException("Invalid list of Rel DeckCards");

		listRelDeckCards.stream().filter(rel -> !ECardRarity.DEFAULT_RARITIES.contains(rel.getCard_raridade()))
				.forEach(rel -> {
					rel = ECardRarity.checkOtherRarities(rel);
					ECardRarity rarity = ECardRarity.getRarityByRarityCode(rel.getSetRarityCode());
					rel.setCard_raridade(rarity.getCardRarity());
				});

		return listRelDeckCards;
	}

	public Object convertJsonToDTO(String json, String obj) {
		try {

			for (JsonConverterValidationComposite<?> criteria : JsonConverterValidationFactory.getAllCriterias()) {

				if (criteria.validate(obj)) {
					return new ObjectMapper()
								.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
								.configure(DeserializationFeature.USE_JAVA_ARRAY_FOR_JSON_ARRAY, true)
								.readValue(json, criteria.objetoRetorno);
				}
			}

			throw new IllegalArgumentException(" Invalid Object to mapper: " + obj);

		} catch (JsonProcessingException e) {
			logger.error(" Error while trying parse JSON #convertJsonToDTO");
			e.printStackTrace();
			throw new RuntimeException(e.getMessage());
		}
	}

}
