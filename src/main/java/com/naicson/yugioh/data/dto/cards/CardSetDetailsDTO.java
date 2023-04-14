package com.naicson.yugioh.data.dto.cards;

import java.util.Date;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.naicson.yugioh.entity.Archetype;
import com.naicson.yugioh.entity.Atributo;
import com.naicson.yugioh.entity.Card;
import com.naicson.yugioh.entity.CardAlternativeNumber;
import com.naicson.yugioh.entity.Deck;
import com.naicson.yugioh.entity.TipoCard;

public class CardSetDetailsDTO {
	
	private Integer id;
	private Long numero;
	private String categoria;
	private String nome;
	@JsonIgnore
	private String nomePortugues;
	private Atributo atributo;
	private String propriedade;
	private Integer nivel;
	private Integer atk;
	private Integer def;
	private String descricao;
	@JsonIgnore
	private String descricaoPortugues;
	private String imagem;
	private Integer escala;
	private String descr_pendulum;
	private String descr_pendulum_pt;
	private String qtd_link;
	private List<Deck> sets;
	private String genericType;
	private TipoCard tipo;
	private Archetype archetype;
	private Date registryDate;
	private List<CardAlternativeNumber> alternativeCardNumber;
	private List<CardRarityDTO> listCardRarity;

	private Long cardNumber;
	private Boolean isSideDeck;
	private Boolean isSpeedDuel;

	public CardSetDetailsDTO() {}

	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public Long getNumero() {
		return numero;
	}
	public void setNumero(Long numero) {
		this.numero = numero;
	}
	public String getCategoria() {
		return categoria;
	}
	public void setCategoria(String categoria) {
		this.categoria = categoria;
	}
	public String getNome() {
		return nome;
	}
	public void setNome(String nome) {
		this.nome = nome;
	}
	public String getNomePortugues() {
		return nomePortugues;
	}
	public void setNomePortugues(String nomePortugues) {
		this.nomePortugues = nomePortugues;
	}
	public Atributo getAtributo() {
		return atributo;
	}
	public void setAtributo(Atributo atributo) {
		this.atributo = atributo;
	}
	public String getPropriedade() {
		return propriedade;
	}
	public void setPropriedade(String propriedade) {
		this.propriedade = propriedade;
	}
	public Integer getNivel() {
		return nivel;
	}
	public void setNivel(Integer nivel) {
		this.nivel = nivel;
	}
	public Integer getAtk() {
		return atk;
	}
	public void setAtk(Integer atk) {
		this.atk = atk;
	}
	public Integer getDef() {
		return def;
	}
	public void setDef(Integer def) {
		this.def = def;
	}
	public String getDescricao() {
		return descricao;
	}
	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}
	public String getDescricaoPortugues() {
		return descricaoPortugues;
	}
	public void setDescricaoPortugues(String descricaoPortugues) {
		this.descricaoPortugues = descricaoPortugues;
	}
	public String getImagem() {
		return imagem;
	}
	public void setImagem(String imagem) {
		this.imagem = imagem;
	}
	public Integer getEscala() {
		return escala;
	}
	public void setEscala(Integer escala) {
		this.escala = escala;
	}
	public String getDescr_pendulum() {
		return descr_pendulum;
	}
	public void setDescr_pendulum(String descr_pendulum) {
		this.descr_pendulum = descr_pendulum;
	}
	public String getDescr_pendulum_pt() {
		return descr_pendulum_pt;
	}
	public void setDescr_pendulum_pt(String descr_pendulum_pt) {
		this.descr_pendulum_pt = descr_pendulum_pt;
	}
	public String getQtd_link() {
		return qtd_link;
	}
	public void setQtd_link(String qtd_link) {
		this.qtd_link = qtd_link;
	}
	public List<Deck> getSets() {
		return sets;
	}
	public void setSets(List<Deck> sets) {
		this.sets = sets;
	}
	public String getGenericType() {
		return genericType;
	}
	public void setGenericType(String genericType) {
		this.genericType = genericType;
	}
	public TipoCard getTipo() {
		return tipo;
	}
	public void setTipo(TipoCard tipo) {
		this.tipo = tipo;
	}
	public Archetype getArchetype() {
		return archetype;
	}
	public void setArchetype(Archetype archetype) {
		this.archetype = archetype;
	}
	public Date getRegistryDate() {
		return registryDate;
	}
	public void setRegistryDate(Date registryDate) {
		this.registryDate = registryDate;
	}
	public List<CardAlternativeNumber> getAlternativeCardNumber() {
		return alternativeCardNumber;
	}
	public void setAlternativeCardNumber(List<CardAlternativeNumber> alternativeCardNumber) {
		this.alternativeCardNumber = alternativeCardNumber;
	}
	public Long getCardNumber() {
		return cardNumber;
	}
	public void setCardNumber(Long cardNumber) {
		this.cardNumber = cardNumber;
	}
	
	public Boolean getIsSideDeck() {
		return isSideDeck;
	}
	public void setIsSideDeck(Boolean isSideDeck) {
		this.isSideDeck = isSideDeck;
	}
	public Boolean getIsSpeedDuel() {
		return isSpeedDuel;
	}
	public void setIsSpeedDuel(Boolean isSpeedDuel) {
		this.isSpeedDuel = isSpeedDuel;
	}

	public List<CardRarityDTO> getListCardRarity() {
		return listCardRarity;
	}

	public void setListCardRarity(List<CardRarityDTO> listCardRarity) {
		this.listCardRarity = listCardRarity;
	}

}
