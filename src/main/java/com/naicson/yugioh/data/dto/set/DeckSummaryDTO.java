package com.naicson.yugioh.data.dto.set;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;

public class DeckSummaryDTO {
	
	private String nome;
	private String imagem;
	@JsonFormat(pattern="MM-dd-yyyy")
	private Date lancamento;
	private String setType;
	private String nomePortugues;
	private int quantityUserHave;
	
	public DeckSummaryDTO() {}
	
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
	public String getNomePortugues() {
		return nomePortugues;
	}
	public void setNomePortugues(String nomePortugues) {
		this.nomePortugues = nomePortugues;
	}

	public int getQuantityUserHave() {
		return quantityUserHave;
	}

	public void setQuantityUserHave(int quantityUserHave) {
		this.quantityUserHave = quantityUserHave;
	}
	
	
}
