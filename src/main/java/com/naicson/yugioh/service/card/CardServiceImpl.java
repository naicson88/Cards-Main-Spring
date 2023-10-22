package com.naicson.yugioh.service.card;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import javax.persistence.EntityManager;
import javax.persistence.EntityNotFoundException;
import javax.persistence.Query;
import javax.persistence.Tuple;

import com.naicson.yugioh.util.CardServicesUtil;
import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.naicson.yugioh.data.dao.CardDAO;
import com.naicson.yugioh.data.dto.RelUserCardsDTO;
import com.naicson.yugioh.data.dto.cards.CardDetailsDTO;
import com.naicson.yugioh.data.dto.cards.CardOfArchetypeDTO;
import com.naicson.yugioh.data.dto.cards.CardOfUserDetailDTO;
import com.naicson.yugioh.data.dto.cards.CardsSearchDTO;
import com.naicson.yugioh.data.dto.cards.KonamiSetsWithCardDTO;
import com.naicson.yugioh.data.dto.set.CardsOfUserSetsDTO;
import com.naicson.yugioh.entity.Card;
import com.naicson.yugioh.entity.CardAlternativeNumber;
import com.naicson.yugioh.entity.RelDeckCards;
import com.naicson.yugioh.repository.CardRepository;
import com.naicson.yugioh.repository.RelDeckCardsRepository;
import com.naicson.yugioh.service.user.UserDetailsImpl;
import com.naicson.yugioh.util.GeneralFunctions;
import com.naicson.yugioh.util.search.GeneralSpecification;
import com.naicson.yugioh.util.search.SearchCriteria;

@Service
public class CardServiceImpl  {
	
	@Autowired
	private CardRepository cardRepository;
	@Autowired
	private RelDeckCardsRepository relDeckCardsRepository;
	@Autowired
	EntityManager em;	
	@Autowired
	CardDAO dao;
	@Autowired
	CardAlternativeNumberService alternativeService;

	@Autowired
	private CardPriceInformationServiceImpl cardServicesUtil;
	@Autowired
	CardViewsInformationServiceImpl viewsService;
	
	Logger logger = LoggerFactory.getLogger(CardServiceImpl.class);	
	
	public CardServiceImpl(CardRepository cardRepository, CardDAO dao, RelDeckCardsRepository relDeckCardsRepository) {
		this.cardRepository = cardRepository;
		this.dao = dao;
		this.relDeckCardsRepository = relDeckCardsRepository;
	}

	public CardServiceImpl() {
		
	}

	public Card cardDetails(Integer id) {
		Query query = em.createNativeQuery("SELECT * FROM TAB_CARDS WHERE ID = :cardId", Card.class);
		Card card = (Card) query.setParameter("cardId", id).getSingleResult();
		
		if(card == null)
			throw new IllegalArgumentException("Cannot find Card with ID: " + id);
		
		return card;
	}
	
	public List<RelUserCardsDTO> searchForCardsUserHave(int[] cardsNumbers) {
				
		if(cardsNumbers == null || cardsNumbers.length == 0) 
			 throw new IllegalArgumentException("Unable to query user cards, decks IDs not informed");
				
	     String cardsNumbersString = "";
	     
	     for(int id: cardsNumbers) {
	    	 cardsNumbersString += id;
	    	 cardsNumbersString += ",";
	     }	     
	     cardsNumbersString += "0";
	     
	     return dao.searchForCardsUserHave(GeneralFunctions.userLogged().getId(), cardsNumbersString);
	}

	public Card findByNumero(Long numero) {
		return cardRepository.findByNumero(numero)
				.orElseThrow(() -> new EntityNotFoundException("Card not found with number: " + numero));
	}
	
	public List<CardOfArchetypeDTO> findCardByArchetype(Integer archId) {		
		List<Card> cardsOfArchetype = cardRepository.findByArchetype(archId)
				.orElseThrow(() -> new NoSuchElementException("It was not possible found cards of Archetype: " + archId));
		
		List<CardOfArchetypeDTO> listDTO = new LinkedList<>();
		
		  cardsOfArchetype.stream()
		    .sorted(Comparator.comparing(Card::getNome))
		  	.forEach(card -> {
			CardOfArchetypeDTO dto = new CardOfArchetypeDTO(card);
			dto.setQtdUserHave(cardRepository.findQtdUserHave(card.getId()));
			listDTO.add(dto);
		});
		
		return listDTO;
	}


	public CardOfUserDetailDTO cardOfUserDetails(Integer cardId) {
		
			if(cardId == null || cardId == 0)
				throw new IllegalArgumentException("Invalid card ID: " + cardId + " #cardOfUserDetails");
			
			CardOfUserDetailDTO cardUserDTO = getCardOfUserDetailDTO(cardId);
			
			List<Tuple> cardsDetails = dao.listCardOfUserDetails(cardId, GeneralFunctions.userLogged().getId());
			
			if(cardsDetails == null ) 
				return cardUserDTO;
			
			List<CardsOfUserSetsDTO> listCardsSets = createCardsOfUserDTOList(cardsDetails);
			
			Map<String, Long> mapRarity = listCardsSets.stream()
					.collect(Collectors.groupingBy(
							CardsOfUserSetsDTO::getRarity, Collectors.counting()
					));
			
			cardUserDTO.setRarity(mapRarity);
			cardUserDTO.setSetsWithThisCard(listCardsSets);
			
			return cardUserDTO;
			
	}

	protected List<CardsOfUserSetsDTO> createCardsOfUserDTOList(List<Tuple> cardsDetails) {
		if(cardsDetails == null || cardsDetails.isEmpty())
			throw new IllegalArgumentException("Invalid Tuple List of CardsOfUserSetsDTO");
		
		return cardsDetails.stream().map(c -> new CardsOfUserSetsDTO(											
				c.get(0, String.class),
				c.get(1, String.class),
				c.get(2, String.class),
				c.get(3, Double.class),
				Integer.parseInt(String.valueOf(c.get(4))),
				Integer.parseInt(String.valueOf(c.get(5))),
				c.get(6, String.class)
				)).collect(Collectors.toList());	
	}
	
	private CardOfUserDetailDTO getCardOfUserDetailDTO(Integer cardId) {				
		Card card = cardRepository.findById(cardId)
				.orElseThrow(() -> new EntityNotFoundException("No Cards found with id: " + cardId + " #cardOfUserDetails"));
		
		return new CardOfUserDetailDTO(card.getNumero(),card.getImagem(), card.getNome());
	}

	public CardDetailsDTO findCardByNumberWithDecks(Long cardNumero) {
		
		Card card = cardRepository.findByNumero(cardNumero)
				.orElseThrow(() ->  new EntityNotFoundException("It was not possible find card with number: " + cardNumero));
		
		card.setAlternativeCardNumber(alternativeService.findAllByCardId(card.getId()));	
		
		CardDetailsDTO dto = new CardDetailsDTO();
		
		dto.setKonamiSets(this.setAllSetsWithThisCard(card));
		dto.setCard(card);
		dto.setQtdUserHaveByUserCollection(this.findQtdCardUserHaveByCollection(card.getId()));
		dto.setPrices(cardServicesUtil.getAllPricesOfACardById(card.getId()));
		
		dto.setViews(viewsService.updateCardViewsOrInsertInDB(cardNumero));	
		
		return dto;
		
	}

	public Map<String, List<String>> findQtdCardUserHaveByCollection(Integer cardId) {

	    List<Tuple> total  = cardRepository.findQtdUserHaveByUserCollection(cardId, GeneralFunctions.userLogged().getId());
		
		if(total == null) 
			return Collections.emptyMap();
		
		Map<String, List<String>> mapCardSetAndQuantity = new HashMap<>();
		
		total.stream().forEach(cardSetCode -> {
			String setCode = cardSetCode.get(0, String.class);
			String setName = cardSetCode.get(1, String.class);
			
			if(!mapCardSetAndQuantity.containsKey(setCode)) {
				mapCardSetAndQuantity.put(setCode,  new ArrayList<>());
				mapCardSetAndQuantity.get(setCode).add(setName);							
			}			
			else {				
				mapCardSetAndQuantity.get(setCode).add(setName);			
			}		
				
		});			
		return mapCardSetAndQuantity;
	}
	
	private List<KonamiSetsWithCardDTO> setAllSetsWithThisCard(Card card) {
		
		List<Tuple> listKonamiSets = Optional.of(cardRepository.setsOfCard(card.getId()))
				.orElse(Collections.emptyList());
		
		return listKonamiSets.stream().map(KonamiSetsWithCardDTO::new).collect(Collectors.toList());
		
//		return listKonamiSets.stream().map(set -> {
//			return new KonamiSetsWithCardDTO(set);
//		}).collect(Collectors.toList());
						
	}

	public List<CardsSearchDTO> getByGenericType(Pageable page, String genericType, long userId) {
		
		if(page == null || genericType == null || userId == 0)
			throw new IllegalArgumentException("Page, Generic Type or User Id is invalid.");
		
		Page<Card> list = cardRepository.getByGenericType(page, genericType, userId);
		
		if( list == null || list.isEmpty())
			return Collections.emptyList();
		
		return list.stream()
				.map(CardsSearchDTO::transformInDTO)
				.collect(Collectors.toList());	
	}

	public Page<Card> findAll(GeneralSpecification spec, Pageable pageable) {		
		if(spec == null )
			throw new IllegalArgumentException("No specification for card search");
		
		return cardRepository.findAll(spec, pageable);
	}
	
	public List<CardsSearchDTO> cardSearch(List<SearchCriteria> criterias, String join, Pageable pageable) {

		GeneralSpecification spec = new GeneralSpecification();

		criterias.forEach(criterio ->
			spec.add( new SearchCriteria(criterio.getKey(), criterio.getOperation(), criterio.getValue()))
		);
		 			
		Page<Card> list = this.findAll(spec, pageable);
		
		List<CardsSearchDTO> dtoList = list.stream()
				.filter(Objects::nonNull)
				.map(CardsSearchDTO::transformInDTO)
				.collect(Collectors.toList());
		
		if(!dtoList.isEmpty())
			dtoList.get(0).setTotalFound(list.getTotalElements());
		
		return dtoList;
			
	}

	public Page<Card> searchCardDetailed(List<SearchCriteria> criterias, String join, Pageable pageable) {
		
		if(criterias == null || criterias.isEmpty())
			throw new IllegalArgumentException("Criterias is invalid");
		
		GeneralSpecification spec = new GeneralSpecification();
		
		criterias.stream().forEach(criterio ->
			spec.add(new SearchCriteria(criterio.getKey(), criterio.getOperation(), criterio.getValue()))
			);
		
		return this.findAll(spec, pageable);
	}
	
	

	public List<CardsSearchDTO> cardSearchByNameUserCollection(String cardName, Pageable pageable) {
		
		if(cardName == null || cardName.isEmpty())
			throw new IllegalArgumentException("Card name invalid for search");
		
		UserDetailsImpl user = GeneralFunctions.userLogged();
		
		Page<Card> cardsList = cardRepository.cardSearchByNameUserCollection(cardName, user.getId(), pageable);
		
		if(cardsList == null || cardsList.isEmpty())
			return Collections.emptyList();
		
		return cardsList.stream()
				.filter(Objects::nonNull)
				.map(CardsSearchDTO::transformInDTO)
				.collect(Collectors.toList());
		
	}

	public List<Card> randomCardsDetailed()  {
			
		List<Card> cards = cardRepository.findRandomCards();
		
		if(cards == null || cards.isEmpty())
			 throw new IllegalArgumentException("Can't find Random cards");
	
		return cards;		
	}

	public List<RelDeckCards> findAllRelDeckCardsByCardNumber(Integer cardId) {
		
		if(cardId == null || cardId == 0)
			throw new IllegalArgumentException("Card Id is invalid: " + cardId);
		
		List<RelDeckCards> list = this.relDeckCardsRepository.findByCardId(cardId);

		if(list == null)
			return Collections.emptyList();
		
		return list;
		
	}

	public List<Long> findCardsNotRegistered(List<Long> cardsNumber) {
		
		if(cardsNumber == null || cardsNumber.isEmpty()) 
			throw new IllegalArgumentException("List with card numbers is invalid");
				
		List<Long> cardsRegistered = cardRepository.findAllCardsByListOfCardNumbers(cardsNumber);
		
		if(cardsRegistered == null || cardsRegistered.isEmpty()) 
			return cardsNumber;			
		else 
			return this.verifyCardsNotRegistered(cardsNumber, cardsRegistered);
	}

	private List<Long> verifyCardsNotRegistered(List<Long> cardsNumber, List<Long> cardsRegistered) {
		
		List<Long> allCards = new LinkedList<>(cardsNumber);
		
		if(allCards.isEmpty())			
			throw new IllegalArgumentException("List with card numbers is invalid");		
		
		if(cardsRegistered == null || cardsRegistered.isEmpty())			
			throw new IllegalArgumentException("There is no list of comparison since cards registered is empty");
		
		
		if(cardsRegistered.containsAll(allCards))
			return Collections.emptyList();
			
			allCards.removeAll(cardsRegistered);
			
			return allCards;		
	}
	
	public Card findByCardNome(String nome) {
		if(nome == null || nome.isBlank())
			throw new IllegalArgumentException("Invalid Card name to find by");
		
		return cardRepository.findByNome(nome.trim());
	}
	
	@Transactional(rollbackFor = Exception.class)
	public void updateCardsImages(String cardImagesJson) {
		logger.info("Start register new Alternative Card Numbers...");
		
		if(cardImagesJson == null || cardImagesJson.isBlank())
			throw new IllegalArgumentException("JSON with card images is empty");
		
		JSONObject card = new JSONObject(cardImagesJson);
		HashSet<Long> imagesList = transforJsonArrayInList(card.getJSONArray("images"));

		String cardName = (String) Optional.ofNullable(card.get("cardName"))
				.orElseThrow(() -> new IllegalArgumentException("Invalid Card Name to update Images"));
		
		Card cardEntity = Optional.ofNullable(this.findByCardNome(cardName.trim()))
				.orElseThrow(() -> new EntityNotFoundException("Card Not Found with name: " + cardName));
		
		List<CardAlternativeNumber> alternativeNumbers = Optional.ofNullable(alternativeService.findAllByCardId(cardEntity.getId()))
				.orElseThrow(() -> new EntityNotFoundException("Card Alternative Number not found with Card ID: " + cardEntity.getId()));
		
		alternativeNumbers.stream().forEach(alt -> {		
			if(imagesList.contains(alt.getCardAlternativeNumber()))
				imagesList.remove(alt.getCardAlternativeNumber());
		});	

		if(!imagesList.isEmpty())
			saveNewAlternativeImages(cardEntity.getId(), imagesList);
		
		logger.info("Finish register new Alternative Card Numbers for {}" , cardName);
				
	}
	
	@Transactional(rollbackFor = Exception.class)
	public void saveNewAlternativeImages(Integer cardId, Set<Long> numbers) {
		numbers.stream().forEach(num -> {
			CardAlternativeNumber alt = new CardAlternativeNumber(cardId, num);
			alternativeService.save(alt);
			logger.info("New Alternative Number Saved: {}" , num);
		});
	}
	
	private HashSet<Long> transforJsonArrayInList(JSONArray array) {		
		if(array == null || array.isEmpty())
			throw new IllegalArgumentException("Array with Card images is empty");
		
		HashSet<Long> list = new HashSet<>();		
		for(int i = 0; i < array.length(); i++) { 
			list.add(array.getLong(i));		
		}		
		return list;
	}

	public List<CardsSearchDTO> getRandomCards() {		
		List<Card> list = cardRepository.findRandomCards();
		
		return list.stream().map(CardsSearchDTO::transformInDTO).collect(Collectors.toList());
	
	}

	public List<Long> getAlternativeArts(Integer cardId) {
		if(cardId == null || cardId == 0)
			throw new IllegalArgumentException("Invalid Card ID: " + cardId);
		
		List<CardAlternativeNumber> alternatives = alternativeService.findAllByCardId(cardId);
		
		if(alternatives == null || alternatives.isEmpty())
			return Collections.emptyList();
		
		return alternatives.stream().map(CardAlternativeNumber::getCardAlternativeNumber).collect(Collectors.toList());
		
	}

	public Map<Integer, String> getAllCardsNamesAndId() {
		List<Tuple> cards = cardRepository.findAllCardsNameAndId();
		
		 return cards.stream()
				 .collect(Collectors
				 .toMap(c -> Integer.parseInt(String.valueOf(c.get(0))), c -> c.get(1, String.class))
			
		);
	}
}