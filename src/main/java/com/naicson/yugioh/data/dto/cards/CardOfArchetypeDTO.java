package com.naicson.yugioh.data.dto.cards;

import com.naicson.yugioh.entity.Card;

public class CardOfArchetypeDTO {
	
	private Integer id;
	private Long numero;
	private String nome;
	private String descricao;
	private int qtdUserHave;
	
	public CardOfArchetypeDTO(Card card) {
		this.id = card.getId();
		this.numero = card.getNumero();
		this.nome = card.getNome();
		this.descricao = card.getDescricao();
	}
	
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
	public String getNome() {
		return nome;
	}
	public void setNome(String nome) {
		this.nome = nome;
	}
	public String getDescricao() {
		return descricao;
	}
	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}

	public int getQtdUserHave() {
		return qtdUserHave;
	}

	public void setQtdUserHave(int qtdUserHave) {
		this.qtdUserHave = qtdUserHave;
	}
	
	
	
}
