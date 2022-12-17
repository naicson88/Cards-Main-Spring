package com.naicson.yugioh.data.builders;

import java.util.Date;

import org.apache.commons.lang3.StringUtils;

import com.naicson.yugioh.entity.Archetype;
import com.naicson.yugioh.entity.Atributo;
import com.naicson.yugioh.entity.Card;
import com.naicson.yugioh.entity.TipoCard;

public class CardBuilder {
	
	private Card card;
	
	public CardBuilder () {
		this.card = new Card();
	}
	
	public static CardBuilder builder() {
		return new CardBuilder();
	}
	
	public CardBuilder numero(Long numero) {
		this.card.setNumero(numero);
		return this;
	}
	
	public CardBuilder categoria(String categoria) {
		this.card.setCategoria(categoria);
		return this;
	}
	
	public CardBuilder nome(String nome) {
		this.card.setNome(nome);
		return this;
	}
	
	public CardBuilder registryDate(Date date) {
		this.card.setRegistryDate(date);
		return this;
	}
	
	public CardBuilder archetype(Archetype archetype) {
		this.card.setArchetype(archetype);
		return this;
	}
	
	public CardBuilder atributo(Atributo attr) {
		this.card.setAtributo(attr);
		return this;
	}
	
	public CardBuilder propriedade(String prop) {
		this.card.setPropriedade(prop);
		return this;
	}
	
	
	public CardBuilder nivel(Integer nivel) {
		this.card.setNivel(nivel);
		return this;
	}
	
	public CardBuilder atk(Integer atk) {
		this.card.setAtk(atk);
		return this;
	}
	
	public CardBuilder def(Integer def) {
		this.card.setDef(def);
		return this;
	}
	
	
	public CardBuilder qtdLink(String qtdLink) {
		this.card.setQtd_link(qtdLink);
		return this;
	}
	
	
	public CardBuilder escala(Integer escala) {
		this.card.setEscala(escala);
		return this;
	}
	
	public CardBuilder tipo(TipoCard tipo) {
		this.card.setTipo(tipo);
		return this;
	}
	
	public CardBuilder genericType(String  generic) {
		this.card.setGenericType(generic);
		return this;
	}
	
	public CardBuilder descricao(String  desc) {
		this.card.setDescricao(desc);
		return this;
	}
	
	public CardBuilder descricaoPendulum(String  desc) {
		this.card.setDescr_pendulum(desc);
		return this;
	}
	
	public Card build() {
		this.validCardBeforeSave(this.card);	
		return this.card;	
	}

	
	private void validCardBeforeSave(Card cardToBeRegistered) {

		if (cardToBeRegistered.getNumero() == null || cardToBeRegistered.getNumero() == 0)
			throw new IllegalArgumentException("Invalid card number.");

		if (StringUtils.isBlank(cardToBeRegistered.getCategoria()))
			throw new IllegalArgumentException("Invalid Card category. " + card.getNome());

		if (StringUtils.isBlank(cardToBeRegistered.getNome()))
			throw new IllegalArgumentException("Invalid Card name. " + card.getNome());
		
		if(cardToBeRegistered.getRegistryDate() == null)
			throw new IllegalArgumentException("Invalid Registry Date of Card");

		if (StringUtils.containsIgnoreCase(cardToBeRegistered.getCategoria(), "link")
				&& cardToBeRegistered.getQtd_link().isBlank() || cardToBeRegistered.getQtd_link().equals("0")) {
				throw new IllegalArgumentException("Invalid Link Quantity. " + card.getNome());
		}
		
		if (StringUtils.containsIgnoreCase(cardToBeRegistered.getCategoria(), "pendulum")) {
			
			if (cardToBeRegistered.getEscala() == null)
				throw new IllegalArgumentException("Invalid Card Scale. " + cardToBeRegistered.toString());
			
			if(cardToBeRegistered.getDescr_pendulum() == null && cardToBeRegistered.getDescricao() == null)
				throw new IllegalArgumentException("Invalid Card Description. " + card.getNome());
		}

	}
}
