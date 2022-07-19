package com.naicson.yugioh.service.setcollection;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collector;
import java.util.stream.Collectors;

import javax.persistence.EntityNotFoundException;
import javax.persistence.Tuple;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.naicson.yugioh.data.dto.cards.CardSetCollectionDTO;
import com.naicson.yugioh.data.dto.set.UserSetCollectionDTO;
import com.naicson.yugioh.entity.sets.SetCollection;
import com.naicson.yugioh.entity.sets.UserSetCollection;
import com.naicson.yugioh.repository.UserSetCollectionRepository;
import com.naicson.yugioh.service.deck.RelDeckCardsServiceImpl;
import com.naicson.yugioh.service.deck.UserDeckServiceImpl;
import com.naicson.yugioh.util.GeneralFunctions;
import com.naicson.yugioh.util.enums.CardRarity;
import com.naicson.yugioh.util.exceptions.ErrorMessage;


@Service
public class UserSetCollectionServiceImpl {
	
	@Autowired
	private SetCollectionServiceImpl setService;
	
	@Autowired
	private RelDeckCardsServiceImpl relService;
	
	@Autowired
	private UserSetCollectionRepository userSetRepository;
	
	@Autowired
	private UserDeckServiceImpl userDeckService;
	
	Logger logger = LoggerFactory.getLogger(UserSetCollectionServiceImpl.class);
	
	@Transactional(rollbackFor = Exception.class)
	public UserSetCollection addSetCollectionInUsersCollection(Integer setId) {
			
		SetCollection set = setService.findById(setId);
		
//		set.getDecks().stream().forEach(deck -> {
//			deck.setRel_deck_cards(relService.findRelByDeckId(deck.getId()));
//		});
//		
		UserSetCollection userSet = UserSetCollection.convertToUserSetCollection(set);
		
		userSet.getUserDeck().forEach(deck -> {
			deck.setId(userDeckService.saveUserDeck(deck).getId());}
		);
				
		UserSetCollection setSaved = userSetRepository.save(userSet);
		
		setSaved.getUserDeck().stream().forEach(deck -> {
			userSetRepository.saveSetUserDeckRelation(setSaved.getId(), deck.getId());
		});
		
		if(setSaved == null || setSaved.getId() == null || setSaved.getId() == 0)
			throw new ErrorMessage("It was not possible save User SetCollection");
		
		logger.info("Saving Decks from SetCollection... {}", LocalDateTime.now());
	
		return setSaved;
	}
	
	@Transactional(rollbackFor = Exception.class)
	public void removeSetCollectionInUsersCollection(Long setId) {
		
		if(setId == null || setId ==  0)
			throw new IllegalArgumentException("Invalid Set ID:" +  setId);
		
		UserSetCollection set = userSetRepository.findById(setId).orElseThrow(() -> new  EntityNotFoundException("Set not found with ID: " + setId));
		
		userSetRepository.deleteSetUserDeckRelation(setId);
		
		if(set.getUserDeck() != null) {
			set.getUserDeck().stream().forEach(deck -> { 
				userDeckService.removeSetFromUsersCollection(deck.getId());
			});	
		}
		
		userSetRepository.delete(set);
		
		logger.info("Deleted User SetCollection ID: " + setId);
		
	}

	public UserSetCollectionDTO consultUserSetCollection(Long setId) {
		if(setId == null || setId == 0)
			throw new IllegalArgumentException("Invalid Set Id for consulting: " + setId);
		
		UserSetCollection set = userSetRepository
				.findById(setId).orElseThrow(() -> new  EntityNotFoundException("Set not found with ID: " + setId));
		
		UserSetCollectionDTO dto = new UserSetCollectionDTO();
		
		dto.setId(set.getId());
		dto.setName(set.getName());
		dto.setCards(this.cardsOfSetCollection(set));
		dto.setRarities(this.countRarities(dto.getCards()));
		dto.setSetCodes(this.listSetCodes(dto.getCards()));
		dto.setTotalPrice(this.calculateTotalPrice(dto.getCards()));
		
		return dto;
			
	}
	
	private Double calculateTotalPrice(List<CardSetCollectionDTO> cards) {
		
		Double totalPrice = cards.stream()
				.filter(c -> c.getQuantityUserHave() > 0)
				.mapToDouble(c -> c.getPrice() * c.getQuantityUserHave())
				.sum();
		
		return new BigDecimal(totalPrice).setScale(2, RoundingMode.HALF_UP).doubleValue();
		
	}
	private List<String> listSetCodes(List<CardSetCollectionDTO> cards) {
		
		List<String> listSetCodes = new ArrayList<>();		
			cards.stream().filter(c -> !"Not Defined".equalsIgnoreCase(c.getCardSetCode()))				
			.forEach(c -> {
				String setCode = c.getCardSetCode().split("-")[0];	
				
				if(!listSetCodes.contains(setCode))
					listSetCodes.add(setCode);
			});
			
			return listSetCodes;

	}

	private Map<String, Integer> countRarities(List<CardSetCollectionDTO> cards) {
		Map<String, Integer> mapRarity = new HashMap<>();
		
		cards.stream().forEach(c -> {
			String rarity = c.getRarity();
			rarity = rarity.replace(" ", "_");
			
			if(rarity != null && !rarity.isEmpty()) {
				if(!mapRarity.containsKey(rarity))
					mapRarity.put(rarity, 1);
				else
					mapRarity.put(rarity.toString(), mapRarity.get(rarity) + 1);
			}				
		});
		
		return mapRarity;
	}

	private List<CardSetCollectionDTO> cardsOfSetCollection(UserSetCollection set){
		
		List<Long> deckId = userSetRepository.consultSetUserDeckRelation(set.getId());
		
		if(deckId == null || deckId.size() == 0)
			throw new ErrorMessage("There is no UserDeck for UserSetCollection: " + set.getId());
		
		List<Tuple> tuple = userSetRepository
				.consultUserSetCollection(deckId.get(0), GeneralFunctions.userLogged().getId(), set.getKonamiSetCopied());
		
		List<CardSetCollectionDTO> cardsList = tuple.stream().map(c -> {
			
			CardSetCollectionDTO card = new CardSetCollectionDTO(
					
			Integer.parseInt(String.valueOf(c.get(0))),
			Integer.parseInt(String.valueOf(c.get(1))),
			c.get(2, String.class), 
			Double.parseDouble(String.valueOf(c.get(3))),
			c.get(4, String.class), 
			c.get(5, String.class), 
			Integer.parseInt(String.valueOf(c.get(6))), 
			Integer.parseInt(String.valueOf(c.get(7)))
			);			
			return card;
					
		}).collect(Collectors.toList());
		
		return cardsList;
	}
}
