package com.naicson.yugioh.data.dto.set;

import java.util.Date;
import java.util.List;

import com.naicson.yugioh.entity.RelDeckCards;

public class SetEditDTO {
	
	public Long id;
	private String nome;
	private String imagem;
	private String description;	
	private Date lancamento;	
	private String setType;
	private Boolean isSpeedDuel;
	private Boolean isBasedDeck;
	private String setCode;
	private List<RelDeckCards> relDeckCards;
	private List<SetEditDTO> insideDecks;
	
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
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public Date getLancamento() {
		return lancamento;
	}
	public void setLancamento(Date lancamento) {
		this.lancamento = lancamento;
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
	public Boolean getIsBasedDeck() {
		return isBasedDeck;
	}
	public void setIsBasedDeck(Boolean isBasedDeck) {
		this.isBasedDeck = isBasedDeck;
	}
	public String getSetCode() {
		return setCode;
	}
	public void setSetCode(String setCode) {
		this.setCode = setCode;
	}
	public List<RelDeckCards> getRelDeckCards() {
		return relDeckCards;
	}
	public void setRelDeckCards(List<RelDeckCards> relDeckCards) {
		this.relDeckCards = relDeckCards;
	}
	public List<SetEditDTO> getInsideDecks() {
		return insideDecks;
	}
	public void setInsideDecks(List<SetEditDTO> insideDecks) {
		this.insideDecks = insideDecks;
	}
	
	
}
