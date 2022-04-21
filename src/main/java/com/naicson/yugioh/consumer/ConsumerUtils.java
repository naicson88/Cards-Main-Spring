package com.naicson.yugioh.consumer;

import java.util.Date;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.naicson.yugioh.data.dto.KonamiDeck;
import com.naicson.yugioh.entity.Deck;

@Component
public class ConsumerUtils {
	
	Logger logger = LoggerFactory.getLogger(ConsumerUtils.class);
	
	public static Deck createNewDeck(KonamiDeck kDeck) {
			
			if(kDeck == null) 
				throw new IllegalArgumentException("Informed Konami Deck is invalid!");
				
			Deck deck = new Deck();
			deck.setDt_criacao(new Date());
			deck.setImagem(kDeck.getImagem());
			deck.setLancamento(kDeck.getLancamento());
			deck.setNome(kDeck.getNome().trim());
			deck.setNomePortugues(kDeck.getNomePortugues());
			deck.setRel_deck_cards(kDeck.getListRelDeckCards());
			deck.setSetType(kDeck.getSetType());
			
			return deck;
		}
	
//	public static String adjustDeckName(String rawName) {
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
	
	public static Deck setDeckIdInRelDeckCards(Deck newDeck, Long deckId) {
		
		if(deckId == null || deckId == 0) {
			throw new IllegalArgumentException("Generated Deck Id is invalid.");
		}
		
		newDeck.getRel_deck_cards().stream().forEach(rel -> {rel.setDeckId(deckId);});
		
		return newDeck;
	}
}
