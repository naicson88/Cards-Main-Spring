package com.naicson.yugioh.entity;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.naicson.yugioh.entity.sets.SetCollection;
import com.naicson.yugioh.entity.sets.UserDeck;

@Entity
@Table(name = "tab_decks")
public class Deck implements Serializable {

	private static final long serialVersionUID = 1L;
	
	@Id
	@Column
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	@Column(unique = true)
	private String nome;
	private String imagem;
	private String nomePortugues;	
	@JsonFormat(pattern="MM-dd-yyyy")
	private Date lancamento;	
	@Column(name = "set_type")
	private String setType;
	@Column(name = "dt_criacao")
	private Date dt_criacao;
	private Boolean isSpeedDuel;
	private String imgurUrl;
	private boolean isBasedDeck;
	private String setCode;	
	@Column(length = 6000)
	private String description;
	
	@NotNull
	@OneToOne(cascade=CascadeType.PERSIST, fetch = FetchType.EAGER)
    @JoinColumn(name = "quantity_id", referencedColumnName = "id")
    private DeckRarityQuantity quantity;	
	
	@ManyToMany(mappedBy = "decks")
	private List<SetCollection> setCollection;	
	@Transient
	private List<Card> cards;
	@Transient
	private List<Card> sideDeckCards;
	@Transient
	private List<Card> extraDeckCards;
	@Transient
	private List<RelDeckCards> rel_deck_cards;
		
	public Deck() {
		
	}
	
	public static Deck deckFromDeckUser(UserDeck deckUser) {
		Deck deck = new Deck();
		deck.setId(deckUser.getId());
		deck.setNome(deckUser.getNome());
		deck.setImagem(deckUser.getImgurUrl());
		deck.setImgurUrl(deckUser.getImgurUrl());
		return deck;		
	}
	
	public List<Card> getExtraDeck() {
		return extraDeckCards;
	}

	public void setExtraDeck(List<Card> extraDeck) {
		this.extraDeckCards = extraDeck;
	}

	public List<Card> getSideDeckCards() {
		return sideDeckCards;
	}

	public void setSideDeckCards(List<Card> sideDeckCards) {
		this.sideDeckCards = sideDeckCards;
	}

	public List<RelDeckCards> getRel_deck_cards() {
		return rel_deck_cards;
	}

	public void setRel_deck_cards(List<RelDeckCards> rel_deck_cards) {
		this.rel_deck_cards = rel_deck_cards;
	}

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

	public String getNomePortugues() {
		return nomePortugues;
	}

	public void setNomePortugues(String nomePortugues) {
		this.nomePortugues = nomePortugues;
	}



	public Date getLancamento() {
		return lancamento;
	}

	public void setLancamento(Date lancamento) {
		this.lancamento = lancamento;
	}

	public List<Card> getCards() {
		return cards;
	}
	
	public void setCards(List<Card> cards) {
		this.cards = cards;
	}
	
	public String getSetType() {
		return setType;
	}

	public void setSetType(String setType) {
		this.setType = setType;
	}

	public List<Card> getExtraDeckCards() {
		return extraDeckCards;
	}

	public void setExtraDeckCards(List<Card> extraDeckCards) {
		this.extraDeckCards = extraDeckCards;
	}

	public Date getDt_criacao() {
		return dt_criacao;
	}

	public void setDt_criacao(Date dt_criacao) {
		this.dt_criacao = dt_criacao;
	}

	public List<SetCollection> getSetCollection() {
		return setCollection;
	}

	public void setSetCollection(List<SetCollection> setCollection) {
		this.setCollection = setCollection;
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

	public static long getSerialversionuid() {
		return serialVersionUID;
	}

	public boolean getIsBasedDeck() {
		return isBasedDeck;
	}

	public void setIsBasedDeck(boolean isBasedDeck) {
		this.isBasedDeck = isBasedDeck;
	}

	public String getSetCode() {
		return setCode;
	}

	public void setSetCode(String setCode) {
		this.setCode = setCode;
	}

	public DeckRarityQuantity getQuantity() {
		return quantity;
	}

	public void setQuantity(DeckRarityQuantity quantity) {
		this.quantity = quantity;
	}

	public void setBasedDeck(boolean isBasedDeck) {
		this.isBasedDeck = isBasedDeck;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}
	
	
		
}
