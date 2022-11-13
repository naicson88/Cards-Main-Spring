package com.naicson.yugioh.data.builders;

import java.util.Date;
import java.util.List;
import java.util.Objects;

import com.naicson.yugioh.entity.Card;
import com.naicson.yugioh.entity.Deck;
import com.naicson.yugioh.entity.RelDeckCards;

public class DeckBuilder {

	private Deck deck;

	public DeckBuilder() {
		this.deck = new Deck();
	}

	public static DeckBuilder builder() {
		return new DeckBuilder();
	}

	public DeckBuilder addNome(String nome) {
		this.deck.setNome(Objects.requireNonNull(nome, "Name is required to create a Deck"));
		return this;
	}

	public DeckBuilder addImagem(String img) {
		this.deck.setImagem(img);
		return this;

	}

	public DeckBuilder addImgurUrl(String imgurImg) {
		this.deck.setImgurUrl(imgurImg);
		return this;
	}

	public DeckBuilder addDt_criacao() {
		this.deck.setDt_criacao(new Date());
		return this;

	}

	public DeckBuilder addId(Long id) {
		this.deck.setId(id);
		return this;
	}

	public DeckBuilder addCards(List<Card> cards) {
		this.deck.setCards(cards);
		return this;

	}

	public DeckBuilder addExtraDeck(List<Card> extraDeckCards) {
		this.deck.setExtraDeck(extraDeckCards);
		return this;

	}

	public DeckBuilder addSideDeckCards(List<Card> sideDeckCards) {
		this.deck.setSideDeckCards(sideDeckCards);
		return this;
	}

	public DeckBuilder addRel_deck_cards(List<RelDeckCards> listRelDeckCards) {
		this.deck.setRel_deck_cards(listRelDeckCards);
		return this;

	}

	public Deck build() {
		return this.deck;
	}

}
