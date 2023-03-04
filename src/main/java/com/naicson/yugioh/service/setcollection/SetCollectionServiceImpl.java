package com.naicson.yugioh.service.setcollection;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.persistence.EntityNotFoundException;
import javax.persistence.Tuple;
import javax.validation.Valid;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.naicson.yugioh.data.bridge.source.SourceTypes;
import com.naicson.yugioh.data.dto.cards.CardSetDetailsDTO;
import com.naicson.yugioh.data.dto.set.AssociationDTO;
import com.naicson.yugioh.data.dto.set.DeckAndSetsBySetTypeDTO;
import com.naicson.yugioh.data.dto.set.InsideDeckDTO;
import com.naicson.yugioh.data.dto.set.SetDetailsDTO;
import com.naicson.yugioh.data.dto.set.SetEditDTO;
import com.naicson.yugioh.entity.Deck;
import com.naicson.yugioh.entity.sets.SetCollection;
import com.naicson.yugioh.entity.sets.UserSetCollection;
import com.naicson.yugioh.repository.SetCollectionRepository;
import com.naicson.yugioh.repository.UserSetCollectionRepository;
import com.naicson.yugioh.service.deck.DeckServiceImpl;
import com.naicson.yugioh.service.interfaces.SetCollectionService;
import com.naicson.yugioh.util.GeneralFunctions;
import com.naicson.yugioh.util.enums.SetType;

@Service
public class SetCollectionServiceImpl implements SetCollectionService {
	
	@Autowired
	SetCollectionRepository setColRepository;
	
	@Autowired
	UserSetCollectionRepository userSetRepository;
	
	@Autowired
	DeckServiceImpl deckService;	
	@Autowired
	SetsUtils setsUtils;
	
	Logger logger = LoggerFactory.getLogger(SetCollectionServiceImpl.class);

	@Override
	@Transactional(rollbackFor = Exception.class)
	public SetCollection saveSetCollection(SetCollection setCollection) {
		
		validSetCollection(setCollection);
	
		SetCollection collectionSaved = setColRepository.save(setCollection);
		
		return collectionSaved;
	}
	
	public SetDetailsDTO convertSetCollectionToDeck(SetCollection set, SourceTypes deckSource, String table) {
		
		SetDetailsDTO setDetailsDto = convertBasicSetToSetDetailsDTO(set);
		
		List<InsideDeckDTO> listInsideDeck = new ArrayList<>();
		
		set = getCardsForEachDeck(set, deckSource, table);
		
		// Iterate over Deck	
		set.getDecks().stream().forEach(d -> { 			
			InsideDeckDTO insideDeck = new InsideDeckDTO();			
			insideDeck.setInsideDeckName(d.getNome());
			insideDeck.setInsideDeckImage(d.getImgurUrl());
			
			//Iterate over Cards
			List<CardSetDetailsDTO> listSetDetails = d.getCards().stream().map(c -> {			
				CardSetDetailsDTO cardDetail = new CardSetDetailsDTO();	
				BeanUtils.copyProperties(c, cardDetail);
				
				cardDetail.setListCardRarity(setsUtils.listCardRarity(cardDetail, d.getRel_deck_cards()));
				
				return cardDetail;		
				
			}).collect(Collectors.toList()); ;			
		
			insideDeck.setCards(listSetDetails);
			listInsideDeck.add(insideDeck);				
		});
		
		setDetailsDto.setInsideDecks(listInsideDeck);
		setDetailsDto.setQuantity(deckService.countDeckRarityQuantity(setDetailsDto));
		
		return setDetailsDto;
				
	}
	
	private SetDetailsDTO convertBasicSetToSetDetailsDTO(SetCollection set) {
		SetDetailsDTO deck = new SetDetailsDTO();
				
		deck.setDt_criacao(set.getRegistrationDate());
		deck.setId(set.getId().longValue());
		deck.setImagem(set.getImgurUrl());
		deck.setImgurUrl(set.getImgurUrl());
		deck.setIsSpeedDuel(set.getIsSpeedDuel());
		deck.setLancamento(set.getReleaseDate());
		deck.setNome(set.getName());
		deck.setDescription(set.getDescription());
		deck.setSetType(set.getSetCollectionType().toString());
		deck.setImgurUrl(set.getImgurUrl());
		return deck;
		
	}

	private SetCollection getCardsForEachDeck(SetCollection set, SourceTypes deckSource, String table) {
		
		set.getDecks().stream().forEach(d -> { 		
			Deck deckAux = deckService.returnDeckWithCards(d.getId(), deckSource, table);	
			d.setCards(deckAux.getCards());
			d.setRel_deck_cards(deckAux.getRel_deck_cards());		
		});
		
		return set;
	}
	
			
	@Override
	public SetCollection findById(Integer id) {	
		SetCollection col = setColRepository.findById(id)
				.orElseThrow(() -> new EntityNotFoundException("SetCollection not found"));		
		return col;
	}
	
	private void validSetCollection(SetCollection setCollection) {
		
		if(setCollection == null)
			throw new IllegalArgumentException("Invalid Set Collection.");
		
		if(setCollection.getIsSpeedDuel() == null)
			throw new IllegalArgumentException("Invalid Speed Duel definition.");
		
		if(StringUtils.isEmpty(setCollection.getImgPath()))
			throw new IllegalArgumentException("Invalid Image path for Set Collection.");
		
		if(StringUtils.isEmpty(setCollection.getName()))
			throw new IllegalArgumentException("Invalid Name for Set Collection.");
		
		if(setCollection.getRegistrationDate() == null)
			throw new IllegalArgumentException("Invalid Registration Date for Set Collection.");
		
		if(setCollection.getReleaseDate() == null)
			throw new IllegalArgumentException("Invalid Release Date for Set Collection.");
		
		SetType.valueOf(setCollection.getSetCollectionType().toString());
		
	}

	@Override
	public List<DeckAndSetsBySetTypeDTO> getAllSetsBySetType(String setType) {
		if(setType == null || setType.isEmpty())
			throw new IllegalArgumentException("#getAllSetsBySetType - Invalid SetType!");
		
		List<Tuple> tuple = setColRepository.getAllSetsBySetType(setType);
		
		List<DeckAndSetsBySetTypeDTO> listDto = tuple.stream().map(c-> {
			DeckAndSetsBySetTypeDTO dto = new DeckAndSetsBySetTypeDTO(
					Long.parseLong(String.valueOf(c.get(0))),
					String.valueOf(c.get(1))
			);		
			return dto;			
		}).collect(Collectors.toList());
		
		return listDto;
	}
	
	@Transactional(rollbackFor = Exception.class)
	public void saveSetDeckRelation(Long setId, Long deckId) {
		if(setId == null || deckId == null)
			throw new IllegalArgumentException("Invalid date to save relation Set - Deck");
		
		this.setColRepository.saveSetDeckRelation(setId, deckId);				
	}
	
	public List<Long> getSetDeckRelationId(Integer setId){
		
		if(setId == null)
			throw new IllegalArgumentException("Invalid date to save relation Set - Deck");	
		
		return this.setColRepository.getSetDeckRelationId(setId);
	}

	@Override
	@Transactional
	public AssociationDTO newAssociation(@Valid AssociationDTO dto) {
		
		logger.info("Starting creating new Association...");
		
		List<Long> deckId =  Optional.ofNullable(this.getSetDeckRelationId(dto.getSourceId()))
				.orElseThrow(() -> new EntityNotFoundException("Deck ID not found: " + dto.getSourceId()));
		
		for(Integer toAssociate : dto.getArrayToAssociate()) {
			this.saveSetDeckRelation(toAssociate.longValue(), deckId.get(0));
		}
		
		logger.info("Association Created!");
		
		return dto;
	}

	@Override
	public SetEditDTO editCollection(Integer setId) {
		SetCollection set = setColRepository.findById(setId)
				.orElseThrow(() -> new RuntimeException("Can't find Collection with ID: " + setId));
		
		SetEditDTO dto = new SetEditDTO();
		dto.setId(set.getId().longValue());
		dto.setImagem(set.getImgPath());
		dto.setLancamento(set.getReleaseDate());
		dto.setNome(set.getName());
		dto.setRelDeckCards(Collections.emptyList());
		dto.setSetType(set.getSetCollectionType().toString());
		dto.setDescription(set.getDescription());
		dto.setSetCode(set.getSetCode());
		dto.setIsSpeedDuel(set.getIsSpeedDuel());
		
		List<SetEditDTO> insideSets = set.getDecks().stream().map(deck -> {
			return deckService.getDeckToEdit(deck.getId().intValue());

		}).collect(Collectors.toList());
		
		dto.setInsideDecks(insideSets);
		
		return dto;
		
	}

}
