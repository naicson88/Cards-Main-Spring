package com.naicson.yugioh.data.builders;

import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import com.naicson.yugioh.entity.UserRelDeckCards;
import com.naicson.yugioh.entity.sets.UserDeck;
import com.naicson.yugioh.util.enums.SetType;

public class UserDeckBuilder {
	
	private UserDeck userDeck;
	
	private UserDeckBuilder() {
		this.userDeck = new UserDeck();
	}
	
	private UserDeckBuilder(UserDeck ud) {
		this.userDeck = ud;
	}
	
	public static UserDeckBuilder builder() {
		return new UserDeckBuilder();
	}
	
	public static UserDeckBuilder builder(UserDeck ud) {
		return new UserDeckBuilder(ud);
	}
	
	public UserDeckBuilder id(Long id) {
		this.userDeck.setId(id);
		return this;
	}
	
	public UserDeckBuilder nome(String nome) {
		this.userDeck.setNome(nome);
		return this;
	}
	
	public UserDeckBuilder imagem(String imagem) {
		this.userDeck.setImagem(imagem);
		return this;
	}
	
	public UserDeckBuilder konamiDeckCopied(Long konamiDeckCopied) {
		this.userDeck.setKonamiDeckCopied(konamiDeckCopied);
		return this;
	}
	
	public UserDeckBuilder userId(Long userId) {
		this.userDeck.setUserId(userId);
		return this;
	}
	
	public UserDeckBuilder dtCriacao(Date dtCriacao) {
		this.userDeck.setDtCriacao(dtCriacao);
		return this;
	}
	
	public UserDeckBuilder setType(String setType) {
		this.userDeck.setSetType(setType);
		return this;
	}
	
	public UserDeckBuilder isSpeedDuel(Boolean isSpeedDuel) {
		this.userDeck.setIsSpeedDuel(isSpeedDuel);
		return this;
	}
	
	public UserDeckBuilder imgurUrl(String imgurUrl) {
		this.userDeck.setImgurUrl(imgurUrl);
		return this;
	}
	
	public UserDeckBuilder relDeckCards(List<UserRelDeckCards> relDeckCards) {
		this.userDeck.setRelDeckCards(relDeckCards);
		return this;
	}
	
	public UserDeck build() {
		this.userDeck.setImgurUrl(userDeck.getImagem());
		validUserDeck(this.userDeck);
		return this.userDeck;
	}
	
	private void validUserDeck(UserDeck userDeck) {	
		if(userDeck == null)
			throw new IllegalArgumentException("Invalid UserDeck");
		
		if (StringUtils.isBlank(userDeck.getNome()))
			throw new IllegalArgumentException("UserDeck name cannot be null or empty");

		if (StringUtils.isBlank(userDeck.getImagem()))
			throw new IllegalArgumentException("UserDeck Image cannot be null or empty");
		
		if (userDeck.getDtCriacao() == null)
			throw new IllegalArgumentException("UserDeck Creation Date cannot be null or empty");
		
		if (userDeck.getIsSpeedDuel() == null)
			throw new IllegalArgumentException("UserDeck IsSpeedDuel cannot be null or empty");
		
		if(userDeck.getUserId() == 0)
			throw new IllegalArgumentException("UserDeck ID invalid");
		
	     if(SetType.getByType(userDeck.getSetType()) == null)
	    	 throw new IllegalArgumentException("Invalid SetType: " + userDeck.getSetType());		
	}
	
	
}


