package com.naicson.yugioh.data.dto.set;

import java.util.Date;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.naicson.yugioh.entity.DeckRarityQuantity;
import com.naicson.yugioh.util.enums.CardAttributes;

public class SetDetailsDTO {

	private Long id;
	private String nome;
	private String imagem;
	private String nomePortugues;	
	@JsonFormat(pattern="MM-dd-yyyy")
	private Date lancamento;	
	private String setType;
	private Date dt_criacao;
	private Boolean isSpeedDuel;
	private List<InsideDeckDTO> insideDecks;	
	private String imgurUrl;
	private int quantityUserHave;
	private Map<String, Long> quantity;
	
	private Map<CardAttributes, Integer> statsQuantityByAttribute;
	private Map<Integer, Integer> statsQuantityByLevel;
	private Map<String, Integer> statsQuantityByProperty;
	private Map<String, Integer> statsQuantityByGenericType;
	private Map<String, Integer> statsQuantityByType;
	private Map<Integer, Integer> statsAtk;
	private Map<Integer, Integer> statsDef;
	
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
	
	public List<InsideDeckDTO> getInsideDecks() {
		return insideDecks;
	}
	public void setInsideDecks(List<InsideDeckDTO> insideDecks) {
		this.insideDecks = insideDecks;
	}
	public Map<CardAttributes, Integer> getStatsQuantityByAttribute() {
		return statsQuantityByAttribute;
	}
	public void setStatsQuantityByAttribute(Map<CardAttributes, Integer> statsQuantityByAttribute) {
		this.statsQuantityByAttribute = statsQuantityByAttribute;
	}
	public Map<Integer, Integer> getStatsQuantityByLevel() {
		return statsQuantityByLevel;
	}
	public void setStatsQuantityByLevel(Map<Integer, Integer> statsQuantityByLevel) {
		this.statsQuantityByLevel = statsQuantityByLevel;
	}
	public Map<String, Integer> getStatsQuantityByProperty() {
		return statsQuantityByProperty;
	}
	public void setStatsQuantityByProperty(Map<String, Integer> statsQuantityByProperty) {
		this.statsQuantityByProperty = statsQuantityByProperty;
	}
	public Map<String, Integer> getStatsQuantityByGenericType() {
		return statsQuantityByGenericType;
	}
	public void setStatsQuantityByGenericType(Map<String, Integer> statsQuantityByGenericType) {
		this.statsQuantityByGenericType = statsQuantityByGenericType;
	}
	public Map<Integer, Integer> getStatsAtk() {
		return statsAtk;
	}
	public void setStatsAtk(Map<Integer, Integer> statsAtk) {
		this.statsAtk = statsAtk;
	}

	public Map<Integer, Integer> getStatsDef() {
		return statsDef;
	}
	public void setStatsDef(Map<Integer, Integer> statsDef) {
		this.statsDef = statsDef;
	}
	public Map<String, Integer> getStatsQuantityByType() {
		return statsQuantityByType;
	}
	public void setStatsQuantityByType(Map<String, Integer> statsQuantityByType) {
		this.statsQuantityByType = statsQuantityByType;
	}
	public String getImgurUrl() {
		return imgurUrl;
	}
	public void setImgurUrl(String imgurUrl) {
		this.imgurUrl = imgurUrl;
	}
	public Map<String, Long> getQuantity() {
		return quantity;
	}
	public void setQuantity(Map<String, Long> quantity) {
		this.quantity = quantity;
	}
	public int getQuantityUserHave() {
		return quantityUserHave;
	}
	public void setQuantityUserHave(int quantityUserHave) {
		this.quantityUserHave = quantityUserHave;
	}

}
