package com.naicson.yugioh.entity;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.Table;
import javax.persistence.Transient;
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
	
	@Column(name = "qtd_cards")
	private long qtd_cards;
	private long qtd_comuns;
	private long qtd_raras;
	private long qtd_super_raras;
	private long qtd_ultra_raras;
	private long qtd_secret_raras;
	private long qtd_ultimate_raras;
	private long qtd_gold_raras;
	private long qtd_parallel_raras;
	private long qtd_ghost_raras;
		
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
	
	public Deck( Long id) {
		this.id = id;
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

	public long getQtd_cards() {
		return qtd_cards;
	}

	public void setQtd_cards(long qtd_cards) {
		this.qtd_cards = qtd_cards;
	}

	public long getQtd_comuns() {
		return qtd_comuns;
	}

	public void setQtd_comuns(long qtd_comuns) {
		this.qtd_comuns = qtd_comuns;
	}

	public long getQtd_raras() {
		return qtd_raras;
	}

	public void setQtd_raras(long qtd_raras) {
		this.qtd_raras = qtd_raras;
	}

	public long getQtd_super_raras() {
		return qtd_super_raras;
	}

	public void setQtd_super_raras(long qtd_super_raras) {
		this.qtd_super_raras = qtd_super_raras;
	}

	public long getQtd_ultra_raras() {
		return qtd_ultra_raras;
	}

	public void setQtd_ultra_raras(long qtd_ulta_raras) {
		this.qtd_ultra_raras = qtd_ulta_raras;
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

	public long getQtd_secret_raras() {
		return qtd_secret_raras;
	}

	public void setQtd_secret_raras(long qtd_secret_raras) {
		this.qtd_secret_raras = qtd_secret_raras;
	}

	public String getImgurUrl() {
		return imgurUrl;
	}

	public void setImgurUrl(String imgurUrl) {
		this.imgurUrl = imgurUrl;
	}

	public long getQtd_ultimate_raras() {
		return qtd_ultimate_raras;
	}

	public void setQtd_ultimate_raras(long qtd_ultimate_raras) {
		this.qtd_ultimate_raras = qtd_ultimate_raras;
	}

	public long getQtd_gold_raras() {
		return qtd_gold_raras;
	}

	public void setQtd_gold_raras(long qtd_gold_raras) {
		this.qtd_gold_raras = qtd_gold_raras;
	}

	public long getQtd_parallel_raras() {
		return qtd_parallel_raras;
	}

	public void setQtd_parallel_raras(long qtd_parallel_raras) {
		this.qtd_parallel_raras = qtd_parallel_raras;
	}

	public long getQtd_ghost_raras() {
		return qtd_ghost_raras;
	}

	public void setQtd_ghost_raras(long qtd_ghost_raras) {
		this.qtd_ghost_raras = qtd_ghost_raras;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}
	
	

		
}
