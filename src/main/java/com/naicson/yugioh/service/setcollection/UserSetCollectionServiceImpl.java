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
import com.naicson.yugioh.entity.UserRelDeckCards;
import com.naicson.yugioh.entity.sets.SetCollection;
import com.naicson.yugioh.entity.sets.UserDeck;
import com.naicson.yugioh.entity.sets.UserSetCollection;
import com.naicson.yugioh.repository.UserSetCollectionRepository;
import com.naicson.yugioh.service.deck.DeckServiceImpl;
import com.naicson.yugioh.service.deck.RelDeckCardsServiceImpl;
import com.naicson.yugioh.service.deck.UserDeckServiceImpl;
import com.naicson.yugioh.service.deck.UserRelDeckCardsServiceImpl;
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

	@Autowired
	private UserRelDeckCardsServiceImpl userRelDeckCardsService;

	@Autowired
	private DeckServiceImpl deckService;

	Logger logger = LoggerFactory.getLogger(UserSetCollectionServiceImpl.class);

	@Transactional(rollbackFor = Exception.class)
	public UserSetCollection addSetCollectionInUsersCollection(Integer setId) {

		SetCollection set = setService.findById(setId);

		UserSetCollection userSet = UserSetCollection.convertToUserSetCollection(set);

		userSet.getUserDeck().forEach(deck -> {
			deck.setId(userDeckService.saveUserDeck(deck).getId());
		});

		UserSetCollection setSaved = userSetRepository.save(userSet);

		if (setSaved == null || setSaved.getId() == null || setSaved.getId() == 0)
			throw new ErrorMessage("It was not possible save User SetCollection");

		logger.info("Saving Decks from SetCollection... {}", LocalDateTime.now());

		return setSaved;
	}

	@Transactional(rollbackFor = Exception.class)
	public void removeSetCollectionInUsersCollection(Long setId) {

		if (setId == null || setId == 0)
			throw new IllegalArgumentException("Invalid Set ID:" + setId);

		UserSetCollection set = userSetRepository.findById(setId)
				.orElseThrow(() -> new EntityNotFoundException("Set not found with ID: " + setId));

		userSetRepository.deleteSetUserDeckRelation(setId);

		if (set.getUserDeck() != null) {
			set.getUserDeck().stream().forEach(deck -> {
				userDeckService.removeSetFromUsersCollection(deck.getId());
			});
		}

		userSetRepository.delete(set);

		logger.info("Deleted User SetCollection ID: " + setId);

	}

	public UserSetCollectionDTO consultUserSetCollection(Long setId) {
		if (setId == null || setId == 0)
			throw new IllegalArgumentException("Invalid Set Id for consulting: " + setId);

		UserSetCollection set = userSetRepository.findById(setId)
				.orElseThrow(() -> new EntityNotFoundException("Set not found with ID: " + setId));

		UserSetCollectionDTO dto = new UserSetCollectionDTO();

		dto.setId(set.getId());
		dto.setName(set.getName());
		dto.setCards(this.cardsOfSetCollection(set));
		dto.setRarities(this.countRarities(dto.getCards()));
		dto.setSetCodes(this.listSetCodes(dto.getCards()));
		dto.setTotalPrice(this.calculateTotalPrice(dto.getCards()));
		dto.setImage(set.getImgurUrl());
		dto.setBasedDeck(this.getBasedDeck(set.getKonamiSetCopied()));

		return dto;

	}

	private Map<Long, String> getBasedDeck(Integer konamiSet) {

		if (konamiSet == null || konamiSet == 0)
			return null;

		List<Long> deckIds = setService.getSetDeckRelationId(konamiSet);
		Map<Long, String> mapBasedDecks = new HashMap<>();

		if (deckIds != null && !deckIds.isEmpty()) {
			List<Deck> listDecks = deckService.findAllByIds(deckIds);

			if (listDecks != null && !listDecks.isEmpty()) {
				listDecks.stream().filter(deck -> deck.getIsBasedDeck()).forEach(deck -> {
					mapBasedDecks.put(deck.getId(), deck.getNome());
				});

			} else {
				return null;
			}
		}
		
		return mapBasedDecks.isEmpty() ? null : mapBasedDecks;
	}

	public Double calculateTotalPrice(List<CardSetCollectionDTO> cards) {

		Double totalPrice = cards.stream().filter(c -> c.getQuantityUserHave() > 0)
				.mapToDouble(c -> c.getRelDeckCards().getCard_price() * c.getQuantityUserHave()).sum();

		return new BigDecimal(totalPrice).setScale(2, RoundingMode.HALF_UP).doubleValue();

	}

	private List<String> listSetCodes(List<CardSetCollectionDTO> cards) {

		List<String> listSetCodes = new ArrayList<>();
		cards.stream().filter(c -> !"Not Defined".equalsIgnoreCase(c.getRelDeckCards().getCardSetCode())).forEach(c -> {
			String setCode = c.getRelDeckCards().getCardSetCode().split("-")[0];

			if (!listSetCodes.contains(setCode))
				listSetCodes.add(setCode);
		});

		return listSetCodes;

	}

	public Map<String, Integer> countRarities(List<CardSetCollectionDTO> cards) {
		Map<String, Integer> mapRarity = new HashMap<>();

		cards.stream().forEach(c -> {
			String rarity = c.getRelDeckCards().getCard_raridade();
			rarity = rarity.replace(" ", "_");

			if (rarity != null && !rarity.isEmpty()) {
				if (!mapRarity.containsKey(rarity))
					mapRarity.put(rarity, 1);
				else
					mapRarity.put(rarity.toString(), mapRarity.get(rarity) + 1);
			}
		});

		return mapRarity;
	}

	public List<CardSetCollectionDTO> cardsOfSetCollection(UserSetCollection set) {

		List<Long> deckId = userSetRepository.consultSetUserDeckRelation(set.getId());

		if (deckId == null || deckId.size() == 0)
			throw new ErrorMessage("There is no UserDeck for UserSetCollection: " + set.getId());

		List<Tuple> tuple = userSetRepository.consultUserSetCollection(deckId.get(0),
				GeneralFunctions.userLogged().getId(), set.getKonamiSetCopied());

		List<CardSetCollectionDTO> cardsList = transformTupleInCardSetCollectionDTO(tuple);

		return cardsList;
	}

	public List<CardSetCollectionDTO> transformTupleInCardSetCollectionDTO(List<Tuple> tuple) {
		return tuple.stream().map(c -> {
			String vlr = String.valueOf(c.get(3));

			RelDeckCards rel = new RelDeckCards(c.get(4, String.class), Double.parseDouble(vlr), c.get(5, String.class),
					c.get(6, String.class), c.get(7, String.class));

			CardSetCollectionDTO card = new CardSetCollectionDTO(Integer.parseInt(String.valueOf(c.get(0))),
					Integer.parseInt(String.valueOf(c.get(1))), c.get(2, String.class),
					Integer.parseInt(String.valueOf(c.get(8))), Integer.parseInt(String.valueOf(c.get(9))), rel,
					Boolean.parseBoolean(String.valueOf(c.get(10))), c.get(11, String.class));

			card.setListSetCode(List.of(card.getRelDeckCards().getCard_set_code()));
			return card;

		}).collect(Collectors.toList());
	}

	@Transactional(rollbackFor = Exception.class)
	public String saveUserSetCollection(UserSetCollectionDTO userCollection) {
		if (userCollection == null)
			throw new IllegalArgumentException("Invalid User SetCollection to be saved!");

		Long deckId = userCollection.getId() > 0 ? this.saveExistingDeck(userCollection)
				: this.createNewSetCollection(userCollection);

		List<UserRelDeckCards> listRel = this.createRelDeckCardsOfSetCollection(userCollection, deckId);

		userRelDeckCardsService.saveAll(listRel);

		logger.info("User SetCollection was successfully saved! {} ", userCollection.getName());

		return "User SetCollection was successfully saved!";
	}

	private Long createNewSetCollection(UserSetCollectionDTO userCollection) {
		UserDeck userDeck = UserDeck.userDeckFromUserSetCollectionDTO(userCollection);
		userDeck.setSetType(SetType.USER_NEW_COLLECTION.getType());
		userDeck = userDeckService.saveUserDeck(userDeck);

		if (userDeck == null || userDeck.getId() == 0)
			throw new ErrorMessage(HttpStatus.INTERNAL_SERVER_ERROR, "It was not possible create the Set User Deck");

		UserSetCollection userSet = UserSetCollection.convertFromUserSetCollectionDTO(userCollection, userDeck);
		userSet.setImgPath(GeneralFunctions.getRandomCollectionCase());
		userSet.setImgurUrl(userSet.getImgPath());

		userSet = userSetRepository.save(userSet);

		if (userSet == null || userSet.getId() == 0)
			throw new ErrorMessage(HttpStatus.INTERNAL_SERVER_ERROR, "It was not possible create the Set Collection");

		return userDeck.getId();
	}

	private Long saveExistingDeck(UserSetCollectionDTO userCollection) {

		UserSetCollection set = userSetRepository.findById(userCollection.getId())
				.orElseThrow(() -> new EntityNotFoundException("Set not found with ID: " + userCollection.getId()));

//		if(!set.getName().equals(userCollection.getName())) {
//			set.setName(userCollection.getName());
//			userSetRepository.save(set);
//			logger.info("Set Name changed. ID: {}", set.getId());
//		}

		Long deckId = userSetRepository.consultSetUserDeckRelation(set.getId()).get(0);

		relService.removeRelUserDeckByDeckId(deckId);

		return deckId;
	}

	private List<UserRelDeckCards> createRelDeckCardsOfSetCollection(UserSetCollectionDTO set, Long deckId) {

		Map<String, UserRelDeckCards> mapCards = new HashMap<String, UserRelDeckCards>();

		set.getCards().stream().filter(card -> card.getQuantityUserHave() > 0).forEach(card -> {

			String setCode = card.getRelDeckCards().getCard_set_code() + "-" + card.getCardId();

			if (!mapCards.containsKey(setCode)) {
				UserRelDeckCards rel = this.createRelObject(deckId, card);
				mapCards.put(setCode, rel);
			} else {
				UserRelDeckCards rel = mapCards.get(setCode);
				rel.setQuantity(rel.getQuantity() + card.getQuantityUserHave());
				mapCards.put(setCode, rel);
			}
		});

		List<UserRelDeckCards> listRel = new ArrayList<UserRelDeckCards>(mapCards.values());

		return listRel;
	}

	private UserRelDeckCards createRelObject(Long deckId, CardSetCollectionDTO cardSet) {
		UserRelDeckCards rel = new UserRelDeckCards();
		rel.setCard_price(cardSet.getRelDeckCards().getCard_price());
		rel.setCard_raridade(cardSet.getRelDeckCards().getCard_raridade().trim());
		rel.setCardSetCode(cardSet.getRelDeckCards().getCard_set_code().trim());
		rel.setCardId(cardSet.getCardId());
		rel.setCardNumber(cardSet.getNumber().longValue());
		rel.setDeckId(deckId);
		rel.setDt_criacao(new Date());
		rel.setIsSpeedDuel(cardSet.isSpeedDuel());
		rel.setQuantity(cardSet.getQuantityUserHave());
		rel.setIsSideDeck(false);
		rel.setSetRarityCode(cardSet.getRelDeckCards().getSetRarityCode());
		rel.setRarityDetails(cardSet.getRelDeckCards().getRarityDetails());
		return rel;
	}

	public List<DeckAndSetsBySetTypeDTO> getAllSetsBySetType(String setType) {
		if (setType == null || setType.isEmpty())
			throw new IllegalArgumentException("#getAllSetsBySetType - Invalid SetType!");

		Long userId = GeneralFunctions.userLogged().getId();

		List<Tuple> tuple = userSetRepository.getAllSetsBySetType(setType, userId);

		List<DeckAndSetsBySetTypeDTO> listDto = tuple.stream().map(c -> {
			DeckAndSetsBySetTypeDTO dto = new DeckAndSetsBySetTypeDTO(Long.parseLong(String.valueOf(c.get(0))),
					String.valueOf(c.get(1)));
			return dto;
		}).collect(Collectors.toList());

		return listDto;
	}

	public List<CardSetCollectionDTO> orderByGenericType(List<CardSetCollectionDTO> cards) {

		for (CardSetCollectionDTO card : cards) {
			card.setSortOrder(GenericTypesCards.getOrderByGenerycType(card.getGenericType()));
		}

		List<CardSetCollectionDTO> cardsSorted = cards.stream()
				.sorted(Comparator.comparingInt(CardSetCollectionDTO::getSortOrder)).collect(Collectors.toList());

		return cardsSorted;
	}

	public UserSetCollectionDTO getUserSetCollectionForTransfer(Integer setId) {
		UserSetCollectionDTO setCollection = this.consultUserSetCollection(setId.longValue());

		List<CardSetCollectionDTO> cardsFiltered = setCollection.getCards().stream()
				.filter(set -> set.getQuantityUserHave() > 0).collect(Collectors.toList());

		setCollection.setCards(this.orderByGenericType(cardsFiltered));

		return setCollection;
	}

	@Transactional(rollbackFor = Exception.class)
	public String saveTransfer(List<UserSetCollectionDTO> setsToBeSaved) {
		if (setsToBeSaved == null || setsToBeSaved.size() < 2)
			throw new IllegalArgumentException("There is necessary TWO sets for transfer to be saved!");

		for (UserSetCollectionDTO userSet : setsToBeSaved) {
			if (!userSet.getSetType().equalsIgnoreCase("Deck")) {
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

		userDeckService.saveUserdeck(deck, this.createRelDeckCardsOfSetCollection(userSet, userSet.getId()));
	}
}
