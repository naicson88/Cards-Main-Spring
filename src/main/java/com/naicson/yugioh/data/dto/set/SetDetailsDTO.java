package com.naicson.yugioh.data.dto.set;

import java.util.Date;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonFormat;

import com.naicson.yugioh.entity.DeckRarityQuantity;
import com.naicson.yugioh.entity.Deck;
import com.naicson.yugioh.util.enums.CardAttributes;

public class SetDetailsDTO {

	private Long id;
	private String nome;
	private String imagem;
	private String description;	
	@JsonFormat(pattern="MM-dd-yyyy")
	private Date lancamento;	
	private String setType;
	private Date dt_criacao;
	private Boolean isSpeedDuel;
	private List<InsideDeckDTO> insideDecks;	
	private String imgurUrl;
	private int quantityUserHave;
	private Map<String, Long> quantity;
	private String setCode;
	private SetStatsDTO setStats;
	
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
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public String getSetCode() {
		return setCode;
	}
	public void setSetCode(String setCode) {
		this.setCode = setCode;
	}
	public SetStatsDTO getSetStats() {
		return setStats;
	}
	public void setSetStats(SetStatsDTO setStats) {
		this.setStats = setStats;
	}

}
