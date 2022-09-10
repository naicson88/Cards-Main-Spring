package com.naicson.yugioh.service.setcollection;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.persistence.EntityNotFoundException;
import javax.persistence.Tuple;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.naicson.yugioh.data.dto.cards.CardSetDetailsDTO;
import com.naicson.yugioh.data.dto.set.DeckAndSetsBySetTypeDTO;
import com.naicson.yugioh.data.dto.set.InsideDeckDTO;
import com.naicson.yugioh.data.dto.set.SetDetailsDTO;
import com.naicson.yugioh.entity.Deck;
import com.naicson.yugioh.entity.sets.SetCollection;
import com.naicson.yugioh.entity.sets.UserSetCollection;
import com.naicson.yugioh.repository.SetCollectionRepository;
import com.naicson.yugioh.repository.UserSetCollectionRepository;
import com.naicson.yugioh.service.deck.DeckServiceImpl;
import com.naicson.yugioh.service.interfaces.SetCollectionService;
import com.naicson.yugioh.util.enums.SetType;

@Service
public class SetCollectionServiceImpl implements SetCollectionService{
	
	@Autowired
	SetCollectionRepository setColRepository;
	
	@Autowired
	UserSetCollectionRepository userSetRepository;
	
	@Autowired
	DeckServiceImpl deckService;
	
	@Autowired
	SetsUtils utils;
	
	Logger logger = LoggerFactory.getLogger(SetCollectionServiceImpl.class);

	@Override
	@Transactional(rollbackFor = Exception.class)
	public SetCollection saveSetCollection(SetCollection setCollection) {
		
		validSetCollection(setCollection);
	
		SetCollection collectionSaved = setColRepository.save(setCollection);
		
		return collectionSaved;
	}

	@Override
	public SetDetailsDTO setCollectionDetailsAsDeck(Long setId, String source) {
		
		validSetSource(setId, source);
		
		SetDetailsDTO setDetailsDto = "KONAMI".equalsIgnoreCase(source) ? this.konamiSetDetailsDTO(setId) : userSetDetailsDTO(setId);
		
		if(setDetailsDto.getInsideDeck() != null && setDetailsDto.getInsideDeck().size() > 0)	
			setDetailsDto = utils.getSetStatistics(setDetailsDto);
		
		return setDetailsDto;
	}
	
	private SetDetailsDTO userSetDetailsDTO(Long setId) {
		SetCollection setCollection = new SetCollection();

		UserSetCollection userSet = userSetRepository.findById(setId)
				.orElseThrow(() -> new EntityNotFoundException("User Set Collection not found! ID: " + setId));
		
		BeanUtils.copyProperties(userSet, setCollection);			
		setCollection.setId(userSet.getId().intValue());		
		setCollection.setDecks(List.of(Deck.deckFromDeckUser(userSet.getUserDeck().get(0))));

		 SetDetailsDTO setDetailsDto = this.convertSetCollectionToDeck(setCollection, "USER");	
		 
		 return setDetailsDto;
	}
	
	private SetDetailsDTO konamiSetDetailsDTO(Long setId) {
		SetCollection setCollection = new SetCollection();
		SetDetailsDTO setDetailsDto = new SetDetailsDTO();

		setCollection = setColRepository.findById(setId.intValue())
			.orElseThrow(() -> new EntityNotFoundException("Set Collection not found! ID: " + setId));
		
		setDetailsDto = this.convertSetCollectionToDeck(setCollection, "KONAMI");
		
		return setDetailsDto;
	}

	private void validSetSource(Long setId, String source) {
		if(setId == null)
			throw new IllegalArgumentException("Invalid Set Id");
		
		if(source == null || source.isBlank())
			throw new IllegalArgumentException("Invalid Source");
	}
	
	private SetDetailsDTO convertSetCollectionToDeck(SetCollection set, String deckSource) {
		
		SetDetailsDTO setDetailsDto = convertBasicSetToSetDetailsDTO(set);
		
		List<InsideDeckDTO> listInsideDeck = new ArrayList<>();		
		set = getCardsForEachDeck(set, deckSource);
		
		// Iterate over Deck	
		set.getDecks().stream().forEach(d -> { 			
			InsideDeckDTO insideDeck = new InsideDeckDTO();	
			Map<Integer, CardSetDetailsDTO> mapCardSetDetails = new HashMap<>();
			
			insideDeck.setInsideDeckName(d.getNome());
			insideDeck.setInsideDeckImage(d.getImgurUrl());	
			//Iterate over Cards
			List<CardSetDetailsDTO> listSetDetails = d.getCards().stream().map(c -> {				
				CardSetDetailsDTO cardDetail = new CardSetDetailsDTO();				
				BeanUtils.copyProperties(c, cardDetail);
				mapCardSetDetails.put(cardDetail.getId(), cardDetail);
				return cardDetail;	
				
			}).collect(Collectors.toList()); ;			
			//Iterate over Rel. Deck Cards
			d.getRel_deck_cards().forEach(r -> {
				CardSetDetailsDTO detail =  mapCardSetDetails.get(r.getCardId());		
				BeanUtils.copyProperties(r, detail);
			});	
			
			insideDeck.setCards(listSetDetails);
			listInsideDeck.add(insideDeck);				
		});
		
		setDetailsDto.setInsideDecks(listInsideDeck);

		set.getDecks().stream().forEach(d -> {
			setDetailsDto.setQtd_cards(setDetailsDto.getQtd_cards() + d.getQtd_cards());
			setDetailsDto.setQtd_comuns(setDetailsDto.getQtd_comuns() + d.getQtd_comuns());
			setDetailsDto.setQtd_raras(setDetailsDto.getQtd_raras() + d.getQtd_raras());
			setDetailsDto.setQtd_secret_raras(setDetailsDto.getQtd_secret_raras() + d.getQtd_secret_raras());
			setDetailsDto.setQtd_super_raras(setDetailsDto.getQtd_super_raras() + d.getQtd_super_raras());
			setDetailsDto.setQtd_ultra_raras(setDetailsDto.getQtd_ultra_raras() + d.getQtd_ultra_raras());
		});
			
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
		deck.setNomePortugues(set.getPortugueseName());
		deck.setSetType(set.getSetCollectionType().toString());
		deck.setImgurUrl(set.getImgurUrl());
		return deck;
		
	}

	private SetCollection getCardsForEachDeck(SetCollection set, String deckSource) {
		
		set.getDecks().stream().forEach(d -> { 		
			Deck deckAux = deckService.returnDeckWithCards(d.getId(), deckSource);	
			d.setCards(deckAux.getCards());
			d.setRel_deck_cards(deckAux.getRel_deck_cards());		
		});
		
		return set;
	}
	
			
	@Override
	public SetCollection findById(Integer id) {	
		SetCollection col = setColRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("SetCollection not found"));		
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

}
