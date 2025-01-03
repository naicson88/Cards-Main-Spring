package com.naicson.yugioh.entity.sets;

import java.util.Date;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.springframework.beans.BeanUtils;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.naicson.yugioh.data.builders.UserDeckBuilder;
import com.naicson.yugioh.data.dto.set.UserSetCollectionDTO;
import com.naicson.yugioh.entity.Deck;
import com.naicson.yugioh.entity.RelDeckCards;
import com.naicson.yugioh.entity.UserRelDeckCards;
import com.naicson.yugioh.util.GeneralFunctions;
import com.naicson.yugioh.util.enums.SetType;

@Entity
@Table(name = "tab_user_deck")
public class UserDeck {
	
	@Id
	@Column
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	@Column(unique = true)
	private String nome;
	private String imagem;
	@Column(name = "konami_deck_copied")
	private Long konamiDeckCopied;
	private Long userId;
	private Date dtCriacao;
	private String setType;
	private Boolean isSpeedDuel;
	private String imgurUrl;
	@Transient
	@JsonAlias("rel_deck_cards")
	private List<UserRelDeckCards> relDeckCards;
	

	public static UserDeck userDeckFromDeck(Deck deck) {
		UserDeck userDeck = new UserDeck();
		BeanUtils.copyProperties(deck, userDeck);		

		userDeck.setUserId(GeneralFunctions.userLogged().getId());
		userDeck.setDtCriacao(new Date());
		userDeck.setKonamiDeckCopied(deck.getId());
	
		return userDeck;
	}
	
	public UserDeck userDeckCopiedFromKonamiDeck(Deck deckOrigem) {
		UserDeck newDeck = new UserDeck();
		newDeck.setImagem(deckOrigem.getImgurUrl());
		newDeck.setImgurUrl(deckOrigem.getImgurUrl());
		newDeck.setKonamiDeckCopied(deckOrigem.getId());
		newDeck.setUserId(GeneralFunctions.userLogged().getId());
		newDeck.setDtCriacao(new Date());
		newDeck.setSetType(deckOrigem.getSetType());
		newDeck.setIsSpeedDuel(deckOrigem.getIsSpeedDuel());
		
		return newDeck;
	}
	
	public static UserDeck userDeckFromUserSetCollectionDTO(UserSetCollectionDTO userCollection) {
		
		return UserDeckBuilder.builder()
			.dtCriacao(new Date())
			.imagem(GeneralFunctions.getRandomDeckCase())
			.isSpeedDuel(false)
			.konamiDeckCopied(null)
			.nome(userCollection.getName())
			.setType(SetType.USER_NEW_COLLECTION.getType())
			.userId(GeneralFunctions.userLogged().getId())
			.build();

	}
	
	public List<UserRelDeckCards> getRelDeckCards() {
		return relDeckCards;
	}

	public void setRelDeckCards(List<UserRelDeckCards> relDeckCards) {
		this.relDeckCards = relDeckCards;
	}

	public UserDeck() {}
	
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getNome() {
		return nome;
	}
	public void setNome(String nome) {
		this.nome = nome;
	}
	public String getImagem() {
		return imagem;
	}
	public void setImagem(String imagem) {
		this.imagem = imagem;
	}
	public Long getKonamiDeckCopied() {
		return konamiDeckCopied;
	}
	public void setKonamiDeckCopied(Long konamiDeckCopied) {
		this.konamiDeckCopied = konamiDeckCopied;
	}
	public long getUserId() {
		return userId;
	}
	public void setUserId(long userId) {
		this.userId = userId;
	}
	public Date getDtCriacao() {
		return dtCriacao;
	}
	public void setDtCriacao(Date dtCriacao) {
		this.dtCriacao = dtCriacao;
	}

	public String getSetType() {
		return setType;
	}

	public void setSetType(String setType) {
		this.setType = setType;
	}


	public Boolean getIsSpeedDuel() {
		return isSpeedDuel;
	}


	public void setIsSpeedDuel(Boolean isSpeedDuel) {
		this.isSpeedDuel = isSpeedDuel;
	}


	public String getImgurUrl() {
		return imgurUrl;
	}


	public void setImgurUrl(String imgurUrl) {
		this.imgurUrl = imgurUrl;
	}

	
}
