package com.naicson.yugioh.consumer;

import java.util.Date;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.naicson.yugioh.data.builders.DeckBuilder;
import com.naicson.yugioh.data.composite.JsonConverterValidationComposite;
import com.naicson.yugioh.data.composite.JsonConverterValidationFactory;
import com.naicson.yugioh.data.dto.KonamiDeck;
import com.naicson.yugioh.entity.Deck;
import com.naicson.yugioh.entity.RelDeckCards;
import com.naicson.yugioh.repository.CardAlternativeNumberRepository;
import com.naicson.yugioh.service.card.CardAlternativeNumberService;
import com.naicson.yugioh.util.enums.ECardRarity;
import com.naicson.yugioh.util.enums.SetType;
import com.naicson.yugioh.util.exceptions.ErrorMessage;

@Component
@SuppressWarnings("rawtypes")
public class ConsumerUtils {

	@Autowired
	CardAlternativeNumberService alternativeService;

	Logger logger = LoggerFactory.getLogger(ConsumerUtils.class);

	public Deck createNewDeck(KonamiDeck kDeck) {

		if (kDeck == null)
			throw new IllegalArgumentException("Informed Konami Deck is invalid!");

		return DeckBuilder.builder()
				.dt_criacao(new Date())
				.imagem(kDeck.getImagem())
				.lancamento(kDeck.getLancamento())
				.nome(kDeck.getNome().trim())
				.relDeckCards(kDeck.getListRelDeckCards())
				.setType(SetType.valueOf(kDeck.getSetType()))
				.isSpeedDuel(kDeck.getIsSpeedDuel())
				.imgurUrl(kDeck.getImagem())
				.isBasedDeck(kDeck.getIsBasedDeck())
				.setCode(kDeck.getSetCode())
				.description(kDeck.getDescription())
				.build();
	}

	public Deck setDeckIdInRelDeckCards(Deck newDeck, Long deckId) {

		if (deckId == null || deckId == 0)
			throw new IllegalArgumentException("Generated Deck Id is invalid.");

		newDeck.getRel_deck_cards().stream().forEach(rel -> {
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

	public Object convertJsonToSetCollectionDto(String json, String obj) {
		try {

			for (JsonConverterValidationComposite<?> criteria : JsonConverterValidationFactory.getAllCriterias()) {

				if (criteria.validate(obj)) {
					return new ObjectMapper().configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
							.configure(DeserializationFeature.USE_JAVA_ARRAY_FOR_JSON_ARRAY, true)
							.readValue(json, criteria.objetoRetorno);
				}
			}

			throw new IllegalArgumentException(" Invalid Object to mapper: " + obj);

		} catch (JsonProcessingException e) {
			e.printStackTrace();
			throw new ErrorMessage(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
		}
	}

}
