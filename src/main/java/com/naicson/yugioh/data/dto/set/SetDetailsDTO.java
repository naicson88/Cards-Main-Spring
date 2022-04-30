package com.naicson.yugioh.data.dto.set;

import java.util.Date;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.naicson.yugioh.entity.Deck;
import com.naicson.yugioh.util.enums.CardAttributes;

public class SetDetailsDTO {

	private Long id;
	private String nome;
	private String imagem;
	private String nomePortugues;	
	private long qtd_cards;
	private long qtd_comuns;
	private long qtd_raras;
	private long qtd_super_raras;
	private long qtd_ultra_raras;
	private long qtd_secret_raras;
	@JsonFormat(pattern="MM-dd-yyyy")
	private Date lancamento;	
	private String setType;
	private Date dt_criacao;
	private Boolean isSpeedDuel;
	private List<InsideDeckDTO> insideDecks;	
	
	private Map<CardAttributes, Integer> statisticQuantityByAttribute;
	private Map<Integer, Integer> statisticQuantityByLevel;
	private Map<String, Integer> staticQuantityByProperty;
	
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
	public void setQtd_ultra_raras(long qtd_ultra_raras) {
		this.qtd_ultra_raras = qtd_ultra_raras;
	}
	public long getQtd_secret_raras() {
		return qtd_secret_raras;
	}
	public void setQtd_secret_raras(long qtd_secret_raras) {
		this.qtd_secret_raras = qtd_secret_raras;
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
	public Date getDt_criacao() {
		return dt_criacao;
	}
	public void setDt_criacao(Date dt_criacao) {
		this.dt_criacao = dt_criacao;
	}

	public Boolean getIsSpeedDuel() {
		return isSpeedDuel;
	}
	public void setIsSpeedDuel(Boolean isSpeedDuel) {
		this.isSpeedDuel = isSpeedDuel;
	}
	
	public List<InsideDeckDTO> getInsideDeck() {
		return insideDecks;
	}
	public void setInsideDecks(List<InsideDeckDTO> insideDecks) {
		this.insideDecks = insideDecks;
	}
	public Map<CardAttributes, Integer> getStatisticQuantityByAttribute() {
		return statisticQuantityByAttribute;
	}
	public void setStatisticQuantityByAttribute(Map<CardAttributes, Integer> statisticQuantityByAttribute) {
		this.statisticQuantityByAttribute = statisticQuantityByAttribute;
	}
	public Map<Integer, Integer> getStatisticQuantityByLevel() {
		return statisticQuantityByLevel;
	}
	public void setStatisticQuantityByLevel(Map<Integer, Integer> statisticQuantityByLevel) {
		this.statisticQuantityByLevel = statisticQuantityByLevel;
	}
	public Map<String, Integer> getStaticQuantityByProperty() {
		return staticQuantityByProperty;
	}
	public void setStaticQuantityByProperty(Map<String, Integer> staticQuantityByProperty) {
		this.staticQuantityByProperty = staticQuantityByProperty;
	}
	
		
	
}
