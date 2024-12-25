package com.naicson.yugioh.data.dto.set;

import java.util.Date;

import javax.persistence.Tuple;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.naicson.yugioh.entity.Deck;
import com.naicson.yugioh.entity.sets.SetCollection;
import com.naicson.yugioh.entity.sets.UserSetCollection;
import com.naicson.yugioh.repository.UserSetCollectionRepository;
import com.naicson.yugioh.service.deck.UserDeckServiceImpl;
import com.naicson.yugioh.util.GeneralFunctions;

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

	public static DeckSummaryDTO dtoFromSetCollection(SetCollection set, UserSetCollectionRepository userSetRepository) {
		DeckSummaryDTO deck = new DeckSummaryDTO();

		deck.setId(set.getId().longValue());
		deck.setLancamento(set.getReleaseDate());
		deck.setNome(set.getName());
		deck.setSetType(set.getSetCollectionType().toString());
		deck.setImagem(set.getImgurUrl());
		deck.setNomePortugues(set.getPortugueseName());
		deck.setQuantityUserHave(userSetRepository
				.countQuantityOfASetUserHave(set.getId(), GeneralFunctions.userLogged().getId())
		);

		return deck;
	}

	public static DeckSummaryDTO convertDeckToDTO(Deck originalDeck, UserDeckServiceImpl userDeckService){
		DeckSummaryDTO deck = new DeckSummaryDTO();
		deck.setId(originalDeck.getId());
		deck.setLancamento(originalDeck.getLancamento());
		deck.setNome(originalDeck.getNome());
		deck.setSetType(originalDeck.getSetType());
		deck.setImagem(originalDeck.getImgurUrl() != null ? originalDeck.getImgurUrl() : originalDeck.getImagem());
		deck.setNomePortugues(originalDeck.getNomePortugues());
		deck.setQuantityUserHave(userDeckService.countQuantityOfADeckUserHave(originalDeck.getId()));
		return deck;
	}

	public static DeckSummaryDTO convertSetCollectionToDTO(UserSetCollection set){
		DeckSummaryDTO deck = new DeckSummaryDTO();
		deck.setId(set.getId());
		deck.setLancamento(set.getReleaseDate());
		deck.setNome(set.getName());
		deck.setSetType(set.getSetCollectionType().toString());
		deck.setImagem(set.getImgurUrl());
		deck.setNomePortugues(set.getPortugueseName());
		deck.setQuantityUserHave(0);

		return deck;
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
