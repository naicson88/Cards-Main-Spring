package com.naicson.yugioh.data.dto.set;

import java.util.Date;

import javax.persistence.Tuple;

import com.fasterxml.jackson.annotation.JsonFormat;

public class DeckSummaryDTO {
	private Long id;
	private String nome;
	private String nomePortugues;
	private String imagem;
	@JsonFormat(pattern="MM-dd-yyyy")
	private Date lancamento;
	private String setType;

	private int quantityUserHave;
	
	public DeckSummaryDTO() {}
	
	
	
	public DeckSummaryDTO(Tuple set) {
		this.id = Long.parseLong(String.valueOf(set.get(0)));
		this.nome = set.get(1, String.class);
		this.nomePortugues = set.get(2, String.class);
		this.imagem = set.get(3, String.class);
		this.lancamento = set.get(4, Date.class);
		this.setType = set.get(5, String.class);
		this.quantityUserHave = Integer.parseInt(String.valueOf(set.get(6)));
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

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}
	
	
}
