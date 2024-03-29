package com.naicson.yugioh.util;

import java.util.Date;

import com.naicson.yugioh.data.dto.RelUserCardsDTO;
import com.naicson.yugioh.data.dto.set.DeckDTO;
import com.naicson.yugioh.entity.Card;
import com.naicson.yugioh.entity.Deck;
import com.naicson.yugioh.entity.RelDeckCards;
import com.naicson.yugioh.entity.sets.UserDeck;
import com.naicson.yugioh.service.user.UserDetailsImpl;
import com.naicson.yugioh.util.enums.ECardRarity;

public abstract class ValidObjects {
	
	public static RelUserCardsDTO generateRelUserCardsDTO(int cardNumero) {
		RelUserCardsDTO rel = new RelUserCardsDTO();
		rel.setCardNumero(cardNumero);
		rel.setCardSetCode("xxx");
		rel.setDtCriacao(new Date());
		rel.setId(1);
		rel.setQtd(3);
		rel.setUserId(1);
		
		return rel;
	}
	
	
	public static UserDetailsImpl generateValidUser() {
		UserDetailsImpl user = new UserDetailsImpl();
		user.setEmail("usertest@hotmail.com");
		user.setId(1);
		user.setPassword("123456");
		user.setUsername("Alan Naicson");
		
		return user;
	}
	
	public static RelDeckCards generateRelDeckCards() {
		RelDeckCards relDeckCards = new RelDeckCards();
		
		relDeckCards.setCardNumber(9999999L);
		relDeckCards.setCard_price(90.58);
		relDeckCards.setCard_raridade(ECardRarity.RARE.getCardRarity());
		relDeckCards.setCard_set_code("YYYY-1111");
		relDeckCards.setDeckId(1L);
		relDeckCards.setDt_criacao(new Date());
		
		return relDeckCards;
	}
	
	public static Card generateValidCard(Integer id) {
		Card card = new Card();
		card.setId(id);
		//card.setArquetipo("Arquetipo Teste");
		card.setAtk(2000);
		//card.setAtributo("EARTH");
		card.setCategoria("Monster");
		//card.setCodArchetype(10);
		card.setDef(1500);
		card.setDescr_pendulum("Descricao Pendulum Teste");
		card.setEscala(8);
		card.setNumero(15060L);
		card.setImagem("mocked imagem");
		card.setNome("Test Name");
		
		return card;
	}
	
	public static Deck generateValidDeck() {
		
		Deck newDeck = new Deck();
		   
		newDeck.setImagem("Imagem Deck");
		newDeck.setNome("Deck Teste");
		newDeck.setNomePortugues("Deck teste Portugues");
		newDeck.setSetType("DECK");
		newDeck.setDt_criacao(new Date());
 		newDeck.setIsSpeedDuel(false);
 		newDeck.setIsBasedDeck(false);
	  
	  return newDeck;  

	}
	
	public static UserDeck generateValidUserDeck() {
		
		UserDeck newDeck = new UserDeck();
		   
		newDeck.setImagem("Imagem Deck");
		newDeck.setNome("Deck Teste");
		newDeck.setSetType("DECK");
		newDeck.setDtCriacao(new Date());
 		newDeck.setIsSpeedDuel(false);
	  return newDeck;  

	}
	
	
	
	public static DeckDTO generateValidDeckDTO(Integer id) {
		DeckDTO dto = new DeckDTO();
		dto.setId(id);
		dto.setCard_numero(123456);
		dto.setCard_price(1.99);
		dto.setCard_raridade("Rare");
		dto.setCard_set_code("000-yyyy");
		
		return dto;
	}
	


	
}
