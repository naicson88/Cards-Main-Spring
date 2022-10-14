package com.naicson.yugioh.service.card;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.persistence.EntityManager;
import javax.persistence.EntityNotFoundException;
import javax.persistence.Query;
import javax.persistence.Tuple;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.naicson.yugioh.data.dao.CardDAO;
import com.naicson.yugioh.data.dto.RelUserCardsDTO;
import com.naicson.yugioh.data.dto.cards.CardDetailsDTO;
import com.naicson.yugioh.data.dto.cards.CardOfArchetypeDTO;
import com.naicson.yugioh.data.dto.cards.CardOfUserDetailDTO;
import com.naicson.yugioh.data.dto.cards.CardsSearchDTO;
import com.naicson.yugioh.data.dto.cards.KonamiSetsWithCardDTO;
import com.naicson.yugioh.data.dto.set.CardsOfUserSetsDTO;
import com.naicson.yugioh.entity.Card;
import com.naicson.yugioh.entity.RelDeckCards;
import com.naicson.yugioh.repository.CardAlternativeNumberRepository;
import com.naicson.yugioh.repository.CardRepository;
import com.naicson.yugioh.repository.DeckRepository;
import com.naicson.yugioh.repository.RelDeckCardsRepository;
import com.naicson.yugioh.service.HomeServiceImpl;
import com.naicson.yugioh.service.interfaces.CardDetailService;
import com.naicson.yugioh.service.user.UserDetailsImpl;
import com.naicson.yugioh.util.GeneralFunctions;
import com.naicson.yugioh.util.exceptions.ErrorMessage;
import com.naicson.yugioh.util.search.CardSpecification;
import com.naicson.yugioh.util.search.SearchCriteria;

@Service
public class CardServiceImpl implements CardDetailService {
	
	@Autowired
	private CardRepository cardRepository;
	@Autowired
	private RelDeckCardsRepository relDeckCardsRepository;
	@Autowired
	EntityManager em;	
	@Autowired
	CardDAO dao;
	@Autowired
	CardOfUserDetailDTO cardUserDTO;
	@Autowired
	CardAlternativeNumberRepository alternativeRepository;
	@Autowired
	CardPriceInformationServiceImpl cardPriceService;
	@Autowired
	CardViewsInformationServiceImpl viewsService;
	
	Logger logger = LoggerFactory.getLogger(HomeServiceImpl.class);	
	
	public CardServiceImpl(CardRepository cardRepository, CardDAO dao, RelDeckCardsRepository relDeckCardsRepository, DeckRepository deckRepository) {
		this.cardRepository = cardRepository;
		this.dao = dao;
		this.relDeckCardsRepository = relDeckCardsRepository;
	}

	public CardServiceImpl() {
		
	}

	//Trazer o card para mostrar os detalhes;
	public Card cardDetails(Integer id) {
		Query query = em.createNativeQuery("SELECT * FROM TAB_CARDS WHERE ID = :deckId", Card.class);
		Card card = (Card) query.setParameter("deckId", id).getSingleResult();
		return card;
	}
	
	
	@Override
	public List<RelUserCardsDTO> searchForCardsUserHave(int[] cardsNumbers) {
				
		UserDetailsImpl user = GeneralFunctions.userLogged();
		
		if(user.getId() == 0) {
			 new ErrorMessage("Unable to query user cards, user ID not entered");
		}
		
		if(cardsNumbers == null || cardsNumbers.length == 0) {
			 new ErrorMessage("Unable to query user cards, decks IDs not entered");
		}
		
	     String cardsNumbersString = "";
	     
	     for(int id: cardsNumbers) {
	    	 cardsNumbersString += id;
	    	 cardsNumbersString += ",";
	     }	     
	     cardsNumbersString += "0";
	     
	     List<RelUserCardsDTO> relUserCardsList = dao.searchForCardsUserHave(user.getId(), cardsNumbersString);
		
	     return relUserCardsList;
	     
	}


	@Override
	public Card listarNumero(Long numero) {
		return cardRepository.findByNumero(numero)
				.orElseThrow(() -> new EntityNotFoundException("Card not found with number: " + numero));
	}
	

	@Override
	public List<CardOfArchetypeDTO> findCardByArchetype(Integer archId) {
		
		List<Card> cardsOfArchetype = cardRepository.findByArchetype(archId)
				.orElseThrow(() -> new NoSuchElementException("It was not possible found cards of Archetype: " + archId));
		
		List<CardOfArchetypeDTO> listDTO = new ArrayList<>();
		
		  cardsOfArchetype.stream().forEach(card -> {
			CardOfArchetypeDTO dto = new CardOfArchetypeDTO(card);
			dto.setQtdUserHave(cardRepository.findQtdUserHave(card.getId()));
			listDTO.add(dto);
		});
		
		return listDTO;
	}

	@Override
	public CardOfUserDetailDTO cardOfUserDetails(Integer cardId) {
		
			if(cardId == null || cardId == 0)
				throw new IllegalArgumentException("Invalid card ID: " + cardId + " #cardOfUserDetails");
			
			UserDetailsImpl user = GeneralFunctions.userLogged();
								
			cardUserDTO = this.getCardOfUserDetailDTO(cardId);
			
			List<Tuple> cardsDetails = dao.listCardOfUserDetails(cardId, user.getId());
			
			if(cardsDetails != null ) {
				//Mapeia o Tuple e preenche o objeto de acordo com as colunas da query
				List<CardsOfUserSetsDTO> listCardsSets = cardsDetails.stream().map(c -> new CardsOfUserSetsDTO(											
						c.get(0, String.class),
						c.get(1, String.class),
						c.get(2, String.class),
						c.get(3, Double.class),
						Integer.parseInt(String.valueOf(c.get(4))),
						Integer.parseInt(String.valueOf(c.get(5))),
						c.get(6, String.class)
						)).collect(Collectors.toList());
				
				Map<String, Integer> mapRarity = new HashMap<>();
						
					listCardsSets.stream().forEach(r ->{
					//Verifica se ja tem essa raridade inserida no map
						if(!mapRarity.containsKey(r.getRarity())) {
							mapRarity.put(r.getRarity(), 1);
						} 
						else {
							//... se tiver acrescenta mais um 
							mapRarity.merge(r.getRarity(), 1, Integer::sum);
						}
				});
				
				cardUserDTO.setRarity(mapRarity);
				cardUserDTO.setSetsWithThisCard(listCardsSets);
	
			}
			
			return cardUserDTO;
			
	}
	
	private CardOfUserDetailDTO getCardOfUserDetailDTO(Integer cardId) {
		
		Card card = cardRepository.findById(cardId)
				.orElseThrow(() -> new EntityNotFoundException("No Cards found with id: " + cardId + " #cardOfUserDetails"));
					
		cardUserDTO = new CardOfUserDetailDTO();
		cardUserDTO.setCardImage(card.getImagem());
		cardUserDTO.setCardName(card.getNome());
		cardUserDTO.setCardNumber(card.getNumero());
		
		return cardUserDTO;
	}

	@Override
	public CardDetailsDTO findCardByNumberWithDecks(Long cardNumero) {
		
		Card card = cardRepository.findByNumero(cardNumero)
				.orElseThrow(() ->  new EntityNotFoundException("It was not possible find card with number: " + cardNumero));
		
		card.setAlternativeCardNumber(alternativeRepository.findAllByCardId(card.getId()));	
		
		CardDetailsDTO dto = new CardDetailsDTO();
		
		dto.setKonamiSets(this.setAllSetsWithThisCard(card));
		dto.setCard(card);
		dto.setQtdUserHaveByKonamiCollection(this.findQtdCardUserHaveByCollection(card.getId(), "konami"));
		dto.setQtdUserHaveByUserCollection(this.findQtdCardUserHaveByCollection(card.getId(), "user"));
		dto.setPrices(cardPriceService.getAllPricesOfACardById(card.getId()));
		
		dto.setViews(viewsService.updateCardViewsOrInsertInDB(cardNumero));	
		
		return dto;
		
	}

	@Override
	public Map<String, Integer> findQtdCardUserHaveByCollection(Integer cardId, String collectionSource) {
		UserDetailsImpl user = GeneralFunctions.userLogged();
		
		Map<String, Integer> mapCardSetAndQuantity = new HashMap<>();
		List<Tuple> total = null;
		
		if("konami".equalsIgnoreCase(collectionSource))
			total = cardRepository.findQtdUserHaveByKonamiCollection(cardId, user.getId());
		else if("user".equalsIgnoreCase(collectionSource))
			total = cardRepository.findQtdUserHaveByUserCollection(cardId, user.getId());
		else
			throw new IllegalArgumentException("Invalid collection source");
		
		if(total != null) {
			
			total.stream().forEach(relation -> {
				System.out.println(relation.get(1));
				mapCardSetAndQuantity.put(relation.get(1, String.class), relation.get(0, BigInteger.class).intValue());
			});			
			
		} else {
			return Collections.emptyMap();
		}
		
		return mapCardSetAndQuantity;
	
	}
	
	private List<KonamiSetsWithCardDTO> setAllSetsWithThisCard(Card card) {
		
		List<Tuple> listKonamiSets = cardRepository.setsOfCard(card.getId());
		List<KonamiSetsWithCardDTO> listCardSets = new ArrayList<>();
			
		listKonamiSets.stream().forEach(c -> {	
			Boolean hasOnList = false;
			BigInteger id = c.get(0, BigInteger.class);
			String setType = c.get(1, String.class);
			
			for (KonamiSetsWithCardDTO cardSet : listCardSets) {
				if(cardSet.getId().equals(id) && cardSet.getSetType().equals(setType)) {
					hasOnList = true;
					List<BigDecimal> listDecimals = new ArrayList<>(cardSet.getPrice());
					listDecimals.addAll(List.of(c.get(6, BigDecimal.class)));
					cardSet.setPrice(listDecimals);
					
					List<String> listRarity = new ArrayList<>(cardSet.getRarity());
					listRarity.addAll(List.of(c.get(5, String.class)));
					cardSet.setRarity(listRarity);
				}
			}
			
			if(!hasOnList) {
				listCardSets.add(new KonamiSetsWithCardDTO(
						id,
						setType,
						c.get(2, String.class),
						c.get(3, String.class),
						c.get(4, String.class),
						List.of(c.get(5, String.class)),
						List.of(c.get(6, BigDecimal.class))));
				
				};
			});
		
					
	return listCardSets;
}

	
//	private Card setAllDecksAndAlternativeNumbers(Long cardNumero, Card card) {
//		
//		card.setSets(dao.cardDecks(card.getId()));
//					
//		if(card.getSets() != null && card.getSets().size() > 0) {			
//			card.getSets().stream().forEach(deck -> 
//				deck.setRel_deck_cards(relDeckCardsRepository.findByDeckIdAndCardNumber(deck.getId(), cardNumero)));
//		}
//		
//		card.setAlternativeCardNumber(alternativeRepository.findAllByCardId(card.getId()));
//		
//		return card;
//	}
	
	@Override
	public List<CardsSearchDTO> getByGenericType(Pageable page, String genericType, long userId) {
		
		if(page == null || genericType == null || userId == 0)
			throw new IllegalArgumentException("Page, Generic Type or User Id is invalid.");
		
		Page<Card> list = cardRepository.getByGenericType(page, genericType, userId);
		
		if( list == null || list.isEmpty())
			return Collections.emptyList();
		
		List<CardsSearchDTO> dtoList = list.stream()
				.filter(card -> card != null)
				.map(card -> CardsSearchDTO.transformInDTO(card))
				.collect(Collectors.toList());
				
		return dtoList;		
		
	}

	@Override
	public Page<Card> findAll(CardSpecification spec, Pageable pageable) {
		
		if(spec == null )
			throw new IllegalArgumentException("No specification for card search");
		
		Page<Card> list = cardRepository.findAll(spec, pageable);
		
		return list;
	}
	
	@Override
	public List<CardsSearchDTO> cardSearch(List<SearchCriteria> criterias, String join, Pageable pageable) {
		
		CardSpecification spec = new CardSpecification();
		
		 criterias.stream().forEach(criterio -> 
			spec.add( new SearchCriteria(criterio.getKey(), criterio.getOperation(), criterio.getValue())));
		 			
		Page<Card> list = this.findAll(spec, pageable);
		
		List<CardsSearchDTO> dtoList = list.stream()
				.filter(card -> card != null)
				.map(card -> CardsSearchDTO.transformInDTO(card))
				.collect(Collectors.toList());
		
		if(!dtoList.isEmpty())
			dtoList.get(0).setTotalFound(list.getTotalElements());
		
		return dtoList;
			
	}
	
	@Override
	public Page<Card> searchCardDetailed(List<SearchCriteria> criterias, String join, Pageable pageable) {
		
		if(criterias == null || criterias.isEmpty())
			throw new IllegalArgumentException("Criterias is invalid");
		
		CardSpecification spec = new CardSpecification();
		
		criterias.stream().forEach(criterio ->
		spec.add(new SearchCriteria(criterio.getKey(), criterio.getOperation(), criterio.getValue())));
		
		Page<Card> list = this.findAll(spec, pageable);
		
		return list;
	}
	
	
	@Override
	public List<CardsSearchDTO> cardSearchByNameUserCollection(String cardName, Pageable pageable) {
		
		if(cardName == null || cardName.isEmpty())
			throw new IllegalArgumentException("Card name invalid for search");
		
		UserDetailsImpl user = GeneralFunctions.userLogged();
		
		Page<Card> cardsList = cardRepository.cardSearchByNameUserCollection(cardName, user.getId(), pageable);

		List<CardsSearchDTO> dtoList = new ArrayList<>();
		
		if(cardsList != null && !cardsList.isEmpty()) {
			
			dtoList = cardsList.stream()
					.filter(card -> card != null)
					.map(card -> CardsSearchDTO.transformInDTO(card))
					.collect(Collectors.toList());
		} else {
			dtoList = Collections.emptyList();
		}
		
		return dtoList;
	}

	@Override
	public List<Card> randomCardsDetailed()  {
			
		List<Card> cards = cardRepository.findRandomCards();
		
		if(cards == null || cards.isEmpty())
			 new ErrorMessage("Can't find random cards");		
	
		return cards;		
	}

	@Override
	public List<RelDeckCards> findAllRelDeckCardsByCardNumber(Integer cardId) {
		
		if(cardId == null || cardId == 0)
			throw new IllegalArgumentException("Card Id is invalid: " + cardId);
		
		List<RelDeckCards> list = this.relDeckCardsRepository.findByCardId(cardId);
		
		
		
		if(list == null)
			list = Collections.emptyList();
		
		return list;
		
	}

	@Override
	public List<Long> findCardsNotRegistered(List<Long> cardsNumber) {
		
		if(cardsNumber == null || cardsNumber.isEmpty()) 
			throw new IllegalArgumentException("List with card numbers is invalid");
				
		List<Long> cardsRegistered = cardRepository.findAllCardsByListOfCardNumbers(cardsNumber);
		
		List<Long> cardsNotRegistered = new ArrayList<>();
		
		if(cardsRegistered == null || cardsRegistered.isEmpty()) 
			cardsNotRegistered = cardsNumber;			
		else 
			cardsNotRegistered = this.verifyCardsNotRegistered(cardsNumber, cardsRegistered);
		
		
		return cardsNotRegistered;
	}

	private List<Long> verifyCardsNotRegistered(List<Long> cardsNumber, List<Long> cardsRegistered) {
		
		List<Long> allCards = new LinkedList<>(cardsNumber);
		
		if(allCards == null || allCards.isEmpty())			
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
		
		Card card = cardRepository.findByNome(nome.trim());
		
		return card;
	}


}
