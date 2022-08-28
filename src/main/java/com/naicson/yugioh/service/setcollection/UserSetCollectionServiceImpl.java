package com.naicson.yugioh.service.setcollection;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.persistence.EntityNotFoundException;
import javax.persistence.Tuple;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.naicson.yugioh.data.dto.cards.CardSetCollectionDTO;
import com.naicson.yugioh.data.dto.set.DeckAndSetsBySetTypeDTO;
import com.naicson.yugioh.data.dto.set.UserSetCollectionDTO;
import com.naicson.yugioh.entity.Deck;
import com.naicson.yugioh.entity.RelDeckCards;
import com.naicson.yugioh.entity.sets.SetCollection;
import com.naicson.yugioh.entity.sets.UserDeck;
import com.naicson.yugioh.entity.sets.UserSetCollection;
import com.naicson.yugioh.repository.UserSetCollectionRepository;
import com.naicson.yugioh.service.deck.RelDeckCardsServiceImpl;
import com.naicson.yugioh.service.deck.UserDeckServiceImpl;
import com.naicson.yugioh.util.GeneralFunctions;
import com.naicson.yugioh.util.enums.GenericTypesCards;
import com.naicson.yugioh.util.enums.SetType;
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
		dto.setImage(set.getImgurUrl());
		
		return dto;
			
	}
	
	public Double calculateTotalPrice(List<CardSetCollectionDTO> cards) {
		
		Double totalPrice = cards.stream()
				.filter(c -> c.getQuantityUserHave() > 0)
				.mapToDouble(c -> c.getRelDeckCards().getCard_price() * c.getQuantityUserHave())
				.sum();
		
		return new BigDecimal(totalPrice).setScale(2, RoundingMode.HALF_UP).doubleValue();
		
	}
	private List<String> listSetCodes(List<CardSetCollectionDTO> cards) {
		
		List<String> listSetCodes = new ArrayList<>();		
			cards.stream().filter(c -> !"Not Defined".equalsIgnoreCase(c.getRelDeckCards().getCardSetCode()))				
			.forEach(c -> {
				String setCode = c.getRelDeckCards().getCardSetCode().split("-")[0];	
				
				if(!listSetCodes.contains(setCode))
					listSetCodes.add(setCode);
			});
			
			return listSetCodes;

	}

	public Map<String, Integer> countRarities(List<CardSetCollectionDTO> cards) {
		Map<String, Integer> mapRarity = new HashMap<>();
		
		cards.stream().forEach(c -> {
			String rarity = c.getRelDeckCards().getCard_raridade();
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

	public List<CardSetCollectionDTO> cardsOfSetCollection(UserSetCollection set){
		
		List<Long> deckId = userSetRepository.consultSetUserDeckRelation(set.getId());
		
		if(deckId == null || deckId.size() == 0)
			throw new ErrorMessage("There is no UserDeck for UserSetCollection: " + set.getId());
		
		List<Tuple> tuple = userSetRepository
				.consultUserSetCollection(deckId.get(0), GeneralFunctions.userLogged().getId(), set.getKonamiSetCopied());
		
		List<CardSetCollectionDTO> cardsList = transformTupleInCardSetCollectionDTO(tuple);
		
		return cardsList;
	}

	public List<CardSetCollectionDTO> transformTupleInCardSetCollectionDTO(List<Tuple> tuple) {
		return tuple.stream().map(c -> {
			
			RelDeckCards rel = new RelDeckCards(c.get(4, String.class),Double.parseDouble(String.valueOf(c.get(3))),c.get(5, String.class));
			
			CardSetCollectionDTO card = new CardSetCollectionDTO(					
					Integer.parseInt(String.valueOf(c.get(0))),
					Integer.parseInt(String.valueOf(c.get(1))),
					c.get(2, String.class), 
					Integer.parseInt(String.valueOf(c.get(6))), 
					Integer.parseInt(String.valueOf(c.get(7))),
					rel,
					Boolean.parseBoolean(String.valueOf(c.get(8))),
					c.get(9, String.class)
					);	
			
			card.setListSetCode(List.of(card.getRelDeckCards().getCard_set_code()));
			return card;
					
		}).collect(Collectors.toList());
	}
	
	@Transactional(rollbackFor = Exception.class)
	public String saveUserSetCollection(UserSetCollectionDTO userCollection) {
		if(userCollection == null)
			throw new IllegalArgumentException("Invalid User SetCollection to be saved!");
		
		Long deckId = userCollection.getId() > 0 ? this.saveExistingDeck(userCollection) : this.createNewSetCollection(userCollection);
				
		List<RelDeckCards> listRel = this.createRelDeckCardsOfSetCollection(userCollection, deckId);
		
		if(listRel != null && listRel.size() > 0)
			relService.saveAllRelDeckUserCards(listRel);
		
		return "User SetCollection was successfully saved!";			
	}
	
	private Long createNewSetCollection(UserSetCollectionDTO userCollection) {		
		UserDeck userDeck = UserDeck.userDeckFromUserSetCollectionDTO(userCollection);
		userDeck = userDeckService.saveUserDeck(userDeck);
		
		if(userDeck == null || userDeck.getId() ==0)
			throw new ErrorMessage(HttpStatus.INTERNAL_SERVER_ERROR, "It was not possible create the Set User Deck");
		
		UserSetCollection userSet = UserSetCollection.convertFromUserSetCollectionDTO(userCollection, userDeck);
		
		userSet = userSetRepository.save(userSet);
		
		if(userSet == null || userSet.getId() ==0)
			throw new ErrorMessage(HttpStatus.INTERNAL_SERVER_ERROR, "It was not possible create the Set Collection");
		
		return userDeck.getId();			
	}

	private Long saveExistingDeck(UserSetCollectionDTO userCollection) {
		
		UserSetCollection set = userSetRepository
				.findById(userCollection.getId()).orElseThrow(() -> new  EntityNotFoundException("Set not found with ID: " + userCollection.getId()));
		
		Long deckId = userSetRepository.consultSetUserDeckRelation(set.getId()).get(0);
		
		relService.removeRelUserDeckByDeckId(deckId);
		
		return deckId;
	}
	
	private List<RelDeckCards> createRelDeckCardsOfSetCollection(UserSetCollectionDTO set, Long deckId) {
		
		List<RelDeckCards> listRel =  new ArrayList<>();
		
		for(int i = 0; i < set.getCards().size(); i++) {
			
			CardSetCollectionDTO cardSet = set.getCards().get(i);
			
			if(set.getCards().get(i).getQuantityUserHave() > 0) {
				
				List<RelDeckCards> listFilter = listRel.stream()
						.filter(r -> r.getCard_set_code().equals(cardSet.getRelDeckCards().getCard_set_code()))
						.collect(Collectors.toList());
				
				if(listFilter != null && listFilter.size() >= 1 && !set.getSetType().equalsIgnoreCase("DECK")) {
					listRel.stream().filter(r -> r.getCard_set_code().equals(cardSet.getRelDeckCards().getCard_set_code())).forEach(r ->{
						r.setQuantity(r.getQuantity() + cardSet.getQuantityUserHave());
					});
				} else {
					RelDeckCards rel = createRelObject(deckId, cardSet);					
					listRel.add(rel);				
				}
			}
		}				
		return listRel;
	}

	private RelDeckCards createRelObject(Long deckId, CardSetCollectionDTO cardSet) {
		RelDeckCards rel = new RelDeckCards();
		rel.setCard_price(cardSet.getRelDeckCards().getCard_price());
		rel.setCard_raridade(cardSet.getRelDeckCards().getCard_raridade().trim());
		rel.setCard_set_code(cardSet.getRelDeckCards().getCard_set_code().trim());
		rel.setCardId(cardSet.getCardId());
		rel.setCardNumber(cardSet.getNumber().longValue());
		rel.setDeckId(deckId);
		rel.setDt_criacao(new Date());
		rel.setIsSpeedDuel(cardSet.isSpeedDuel());
		rel.setQuantity(cardSet.getQuantityUserHave());
		rel.setIsSideDeck(false);
		return rel;
	}

	public List<DeckAndSetsBySetTypeDTO> getAllSetsBySetType(String setType) {
		if(setType == null || setType.isEmpty())
			throw new IllegalArgumentException("#getAllSetsBySetType - Invalid SetType!");
		
		Long userId = GeneralFunctions.userLogged().getId();
		
		List<Tuple> tuple  = userSetRepository.getAllSetsBySetType(setType, userId);
		
		List<DeckAndSetsBySetTypeDTO> listDto = tuple.stream().map(c-> {
			DeckAndSetsBySetTypeDTO dto = new DeckAndSetsBySetTypeDTO(
					Long.parseLong(String.valueOf(c.get(0))),
					String.valueOf(c.get(1))
			);		
			return dto;			
		}).collect(Collectors.toList());
		
		return listDto;
	}
	
	public List<CardSetCollectionDTO> orderByGenericType(List<CardSetCollectionDTO> cards) {
		
		for(CardSetCollectionDTO card : cards) {
			card.setSortOrder(GenericTypesCards.getOrderByGenerycType(card.getGenericType()));
		}	
		
		List<CardSetCollectionDTO> cardsSorted = cards.stream()
				.sorted(Comparator.comparingInt(CardSetCollectionDTO::getSortOrder))
				.collect(Collectors.toList());	
		
		return cardsSorted;
	}

	public UserSetCollectionDTO getUserSetCollectionForTransfer(Integer setId) {
		UserSetCollectionDTO setCollection = this.consultUserSetCollection(setId.longValue());
		
		List<CardSetCollectionDTO> cardsFiltered = setCollection.getCards().stream()
				.filter(set -> set.getQuantityUserHave() > 0)
				.collect(Collectors.toList());
		
		setCollection.setCards(this.orderByGenericType(cardsFiltered));
		
		return setCollection;
	}
	
	@Transactional(rollbackFor = Exception.class)
	public String saveTransfer(List<UserSetCollectionDTO> setsToBeSaved) {
		if(setsToBeSaved == null || setsToBeSaved.size() < 2)
			throw new IllegalArgumentException("There is necessary TWO sets for transfer to be saved!");
		
		for(UserSetCollectionDTO userSet: setsToBeSaved) {
			if(!userSet.getSetType().equalsIgnoreCase("Deck")) {
				this.saveUserSetCollection(setsToBeSaved.get(0));
			} else {
				this.saveSetCollectionAsUserDeck(userSet);
			}
		}
		
		return "Sets were saved successfully!";
	}
	
	private void saveSetCollectionAsUserDeck(UserSetCollectionDTO userSet) {
		Deck deck = new Deck();
		deck.setNome(userSet.getName());
		deck.setId(userSet.getId());
		deck.setSetType(userSet.getSetType());
		deck.setRel_deck_cards(this.createRelDeckCardsOfSetCollection(userSet, userSet.getId()));
		
		userDeckService.saveUserdeck(deck);
	}
}
