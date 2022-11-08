package com.naicson.yugioh.data.builders;

import java.util.Date;
import java.util.List;

import com.naicson.yugioh.entity.Card;
import com.naicson.yugioh.entity.RelDeckCards;

public interface IDeckBuilder {
	void addNome(String nome);
	void addImagem(String img);
	void addImgurUrl(String imgurImg);
	void addDt_criacao(Date date);
	void addId(Long id);	
	void addCards(List<Card> cards);
	void addExtraDeck(List<Card> extraDeckCards);
	void addSideDeckCards(List<Card> sideDeckCards);
	void addRel_deck_cards(List<RelDeckCards> listRelDeckCards);
}
