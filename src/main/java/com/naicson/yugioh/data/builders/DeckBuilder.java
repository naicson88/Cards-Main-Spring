package com.naicson.yugioh.data.builders;

import java.util.Date;
import java.util.List;
import java.util.Objects;

import org.apache.commons.lang3.StringUtils;
import org.springframework.util.CollectionUtils;

import com.naicson.yugioh.entity.Card;
import com.naicson.yugioh.entity.Deck;
import com.naicson.yugioh.entity.RelDeckCards;
import com.naicson.yugioh.util.enums.SetType;

public class DeckBuilder {

	private Deck deck;

	public DeckBuilder() {
		this.deck = new Deck();
	}

	public static DeckBuilder builder() {
		return new DeckBuilder();
	}

	public DeckBuilder nome(String nome) {
		this.deck.setNome(Objects.requireNonNull(nome, "Name is required to create a Deck"));
		return this;
	}
	
	public DeckBuilder isSpeedDuel(Boolean isSpeedDuel) {
		this.deck.setIsSpeedDuel(isSpeedDuel);
		return this;
	}
	
	public DeckBuilder setCode(String setCode) {
		this.deck.setSetCode(setCode);
		return this;
	}
	
	public DeckBuilder isBasedDeck(Boolean isBasedDeck) {
		this.deck.setIsBasedDeck(isBasedDeck);
		return this;
	}
	
	public DeckBuilder setType(SetType setType) {
		this.deck.setSetType(setType.toString());
		return this;
	}
	
	public DeckBuilder lancamento(Date lancamento) {
		this.deck.setLancamento(lancamento);
		return this;
	}

	public DeckBuilder imagem(String img) {
		this.deck.setImagem(img);
		return this;

	}

	public DeckBuilder imgurUrl(String imgurImg) {
		this.deck.setImgurUrl(imgurImg);
		return this;
	}

	public DeckBuilder dt_criacao(Date date) {
		this.deck.setDt_criacao(date);
		return this;

	}

	public DeckBuilder id(Long id) {
		this.deck.setId(id);
		return this;
	}

	public DeckBuilder cardsList(List<Card> cards) {
		this.deck.setCards(cards);
		return this;

	}

	public DeckBuilder extraDeckList(List<Card> extraDeckCards) {
		this.deck.setExtraDeck(extraDeckCards);
		return this;

	}

	public DeckBuilder sideDeckList(List<Card> sideDeckCards) {
		this.deck.setSideDeckCards(sideDeckCards);
		return this;
	}

	public DeckBuilder relDeckCards(List<RelDeckCards> listRelDeckCards) {
		this.deck.setRel_deck_cards(listRelDeckCards);
		return this;
	}

	public Deck build() {
		validateDeck(this.deck);
		return this.deck;
	}
	
	public Deck buildForUserDeck() {
		return this.deck;
	}
	
	private void validateDeck(Deck deck) {
		
		if(deck == null)
			throw new IllegalArgumentException("Invalid Deck to build");
		if(deck.getIsSpeedDuel() == null)
			throw new IllegalArgumentException("Invalid Speed Duel info to build");
		if(CollectionUtils.isEmpty(deck.getRel_deck_cards()))
			throw new IllegalArgumentException("Invalid Card List to build");
		if(deck.getDt_criacao() == null)
			throw new IllegalArgumentException("Invalid Date Criacao info to build");
		if(StringUtils.isBlank(deck.getImagem()))
			throw new IllegalArgumentException("Invalid Image info to build");
		if(StringUtils.isBlank(deck.getNome()))
			throw new IllegalArgumentException("Invalid Nome info to build");
		if(StringUtils.isBlank(deck.getSetCode()))
			throw new IllegalArgumentException("Invalid Set Code info to build");
		if(StringUtils.isBlank(deck.getSetType()))
			throw new IllegalArgumentException("Invalid Set Type info to build");

	}

}
