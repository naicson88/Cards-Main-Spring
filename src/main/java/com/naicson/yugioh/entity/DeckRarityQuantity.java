package com.naicson.yugioh.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.MapsId;
import javax.persistence.OneToOne;
import javax.persistence.Table;

@Entity
@Table(name = "tab_rarity_quantity")
public class DeckRarityQuantity {
	
	@Id
	@Column
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	@OneToOne(mappedBy = "quantity", fetch = FetchType.EAGER)
	private Deck deck;
	private long total;
	private long common;
	private long rare;
	private long superRare;
	private long ultraRare;
	private long secretRare;
	private long ultimateRare;
	private long goldRare;
	private long parallelRare;
	private long ghostRare;
	
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public long getTotal() {
		return total;
	}
	public void setTotal(long total) {
		this.total = total;
	}
	public long getCommon() {
		return common;
	}
	public void setCommon(long common) {
		this.common = common;
	}
	public long getRare() {
		return rare;
	}
	public void setRare(long rare) {
		this.rare = rare;
	}
	public long getSuperRare() {
		return superRare;
	}
	public void setSuperRare(long superRare) {
		this.superRare = superRare;
	}
	public long getUltraRare() {
		return ultraRare;
	}
	public void setUltraRare(long ultraRare) {
		this.ultraRare = ultraRare;
	}
	public long getSecreRare() {
		return secretRare;
	}
	public void setSecretRare(long secreRare) {
		this.secretRare = secreRare;
	}
	public long getUltimateRare() {
		return ultimateRare;
	}
	public void setUltimateRare(long ultimateRare) {
		this.ultimateRare = ultimateRare;
	}
	public long getGoldRare() {
		return goldRare;
	}
	public void setGoldRare(long goldRare) {
		this.goldRare = goldRare;
	}
	public long getParallelRare() {
		return parallelRare;
	}
	public void setParallelRare(long parallelRare) {
		this.parallelRare = parallelRare;
	}
	public long getGhostRare() {
		return ghostRare;
	}
	public void setGhostRare(long ghostRare) {
		this.ghostRare = ghostRare;
	}
	public Deck getDeck() {
		return deck;
	}
	public void setDeck(Deck deck) {
		this.deck = deck;
	}
	
	
}
