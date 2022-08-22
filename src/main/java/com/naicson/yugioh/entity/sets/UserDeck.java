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

import com.naicson.yugioh.entity.Deck;
import com.naicson.yugioh.entity.RelDeckCards;
import com.naicson.yugioh.util.GeneralFunctions;

@Entity
@Table(name = "tab_user_deck")
public class UserDeck {
	
	@Id
	@Column
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
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
	private List<RelDeckCards> relDeckCards;
	

	public static UserDeck userDeckFromDeck(Deck deck) {
		UserDeck userDeck = new UserDeck();
		BeanUtils.copyProperties(deck, userDeck);
		
		//userDeck.setNome(userDeck.getNome()+"_"+GeneralFunctions.momentAsString());
		userDeck.setUserId(GeneralFunctions.userLogged().getId());
		userDeck.setDtCriacao(new Date());
		userDeck.setKonamiDeckCopied(deck.getId());
		//userDeck.setRelDeckCards(deck.getRel_deck_cards());
		
		return userDeck;
	
	}
	
	public List<RelDeckCards> getRelDeckCards() {
		return relDeckCards;
	}

	public void setRelDeckCards(List<RelDeckCards> relDeckCards) {
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
