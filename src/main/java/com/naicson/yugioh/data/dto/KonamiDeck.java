package com.naicson.yugioh.data.dto;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import org.springframework.stereotype.Component;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.naicson.yugioh.entity.Card;
import com.naicson.yugioh.entity.RelDeckCards;

@Component
public class KonamiDeck implements Serializable{
	
	private static final long serialVersionUID = 1L;
	private Long id;
	private String requestSource;
	private String imagem;
	private String nome;
	private String nomePortugues;
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy")
	private Date lancamento;
	private String setType;
	private List<CardYuGiOhAPI> cardsToBeRegistered;
	@JsonProperty("relDeckCards")
	private List<RelDeckCards> relDeckCards;
	private Integer setCollectionId;
	private Boolean isSpeedDuel;
	
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getImagem() {
		return imagem;
	}
	public void setImagem(String imagem) {
		this.imagem = imagem;
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
	
	public List<RelDeckCards> getListRelDeckCards() {
		return relDeckCards;
	}
	public void setListRelDeckCards(List<RelDeckCards> listRelDeckCards) {
		this.relDeckCards = listRelDeckCards;
	}

	public List<CardYuGiOhAPI> getCardsToBeRegistered() {
		return cardsToBeRegistered;
	}
	public void setCardsToBeRegistered(List<CardYuGiOhAPI> cardsToBeRegistered) {
		this.cardsToBeRegistered = cardsToBeRegistered;
	}

	public Integer getSetCollectionId() {
		return setCollectionId;
	}
	public void setSetCollectionId(Integer setCollectionId) {
		this.setCollectionId = setCollectionId;
	}
	
	public String getRequestSource() {
		return requestSource;
	}
	public void setRequestSource(String requestSource) {
		this.requestSource = requestSource;
	}
	public Boolean getIsSpeedDuel() {
		return isSpeedDuel;
	}
	public void setIsSpeedDuel(Boolean isSpeedDuel) {
		this.isSpeedDuel = isSpeedDuel;
	}
	
	@Override
	public String toString() {
		return "KonamiDeck [id=" + id + ", requestSource=" + requestSource + ", imagem=" + imagem + ", nome=" + nome
				+ ", nomePortugues=" + nomePortugues + ", lancamento=" + lancamento + ", setType=" + setType
				+ ", cardsToBeRegistered=" + cardsToBeRegistered + ", relDeckCards=" + relDeckCards
				+ ", setCollectionId=" + setCollectionId + ", isSpeedDuel=" + isSpeedDuel + "]";
	}
	
	
	
}
