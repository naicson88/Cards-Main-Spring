package com.naicson.yugioh.consumer;

import java.util.Date;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.naicson.yugioh.data.dto.KonamiDeck;
import com.naicson.yugioh.entity.Deck;
import com.naicson.yugioh.entity.RelDeckCards;
import com.naicson.yugioh.repository.CardAlternativeNumberRepository;
import com.naicson.yugioh.util.enums.ECardRarity;
import com.naicson.yugioh.util.enums.SetType;

@Component
public class ConsumerUtils {
	
	@Autowired
	CardAlternativeNumberRepository alternativeRepository;
	
	Logger logger = LoggerFactory.getLogger(ConsumerUtils.class);
	
	public  Deck createNewDeck(KonamiDeck kDeck) {
			
			if(kDeck == null) 
				throw new IllegalArgumentException("Informed Konami Deck is invalid!");
				
			Deck deck = new Deck();
			deck.setDt_criacao(new Date());
			deck.setImagem(kDeck.getImagem());
			deck.setLancamento(kDeck.getLancamento());
			deck.setNome(kDeck.getNome().trim());
			deck.setNomePortugues(kDeck.getNomePortugues());
			deck.setRel_deck_cards(kDeck.getListRelDeckCards());
			deck.setSetType(SetType.valueOf(kDeck.getSetType()).toString());
			deck.setIsSpeedDuel(kDeck.getIsSpeedDuel());
			deck.setImgurUrl(kDeck.getImagem());
			
			return deck;
	}
	
	public  Deck setDeckIdInRelDeckCards(Deck newDeck, Long deckId) {
		
		if(deckId == null || deckId == 0) {
			throw new IllegalArgumentException("Generated Deck Id is invalid.");
		}		
		newDeck.getRel_deck_cards().stream().forEach(rel -> {
			rel.setDeckId(deckId);
			rel.setQuantity(1);			
			rel.setCardId(alternativeRepository.findByCardAlternativeNumber(rel.getCardNumber()).getCardId());
		});
		
		return newDeck;
	}
	
	public List<RelDeckCards> setRarity(List<RelDeckCards> listRelDeckCards) {
		
		if(listRelDeckCards == null)
			throw new IllegalArgumentException("Invalid list of Rel DeckCards");
		
		listRelDeckCards.stream()
			.filter(rel -> !ECardRarity.DEFAULT_RARITIES.contains(rel.getCard_raridade())).forEach(rel -> {
				ECardRarity rarity = ECardRarity.getRarityByRarityCode(rel.getSetRarityCode());
			rel.setCard_raridade(rarity.getCardRarity());
		});
		
		return listRelDeckCards;
	}
}
