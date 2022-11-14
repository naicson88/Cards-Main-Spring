package com.naicson.yugioh.service.deck;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

import javax.persistence.EntityNotFoundException;
import javax.persistence.Tuple;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.naicson.yugioh.data.builders.DeckBuilder;
import com.naicson.yugioh.data.builders.UserDeckBuilder;
import com.naicson.yugioh.data.dao.DeckDAO;
import com.naicson.yugioh.data.dto.RelUserCardsDTO;
import com.naicson.yugioh.data.dto.set.DeckAndSetsBySetTypeDTO;
import com.naicson.yugioh.data.dto.set.DeckDTO;
import com.naicson.yugioh.data.dto.set.UserSetCollectionDTO;
import com.naicson.yugioh.entity.Card;
import com.naicson.yugioh.entity.Deck;
import com.naicson.yugioh.entity.RelDeckCards;
import com.naicson.yugioh.entity.UserRelDeckCards;
import com.naicson.yugioh.entity.sets.UserDeck;
import com.naicson.yugioh.repository.sets.UserDeckRepository;
import com.naicson.yugioh.service.setcollection.UserSetCollectionServiceImpl;
import com.naicson.yugioh.util.GeneralFunctions;
import com.naicson.yugioh.util.enums.SetType;
import com.naicson.yugioh.util.exceptions.ErrorMessage;

@Service
public class UserDeckServiceImpl {

	@Autowired
	UserDeckRepository userDeckRepository;
	
	@Autowired
	DeckServiceImpl deckService;
	
	@Autowired
	RelDeckCardsServiceImpl relDeckCardsService;
	
	@Autowired
	UserRelDeckCardsServiceImpl userRelService;

	@Autowired
	DeckDAO dao;

	Logger logger = LoggerFactory.getLogger(UserDeckServiceImpl.class);

	public Deck editUserDeck(Long deckId) {
		
		logger.info("Starting edit User Deck: " + deckId);
		
		if (deckId == null || deckId == 0)
			throw new IllegalArgumentException("Invalid Deck Id: " + deckId);

		 UserDeck deckUser = this.userDeckRepository.findById(deckId)
				 .orElseThrow(() -> new EntityNotFoundException("UserDeck id = " + deckId));

		if (GeneralFunctions.userLogged().getId() != deckUser.getUserId())
			throw new RuntimeException("This Deck dont belong to user: " + GeneralFunctions.userLogged().getId() + " Deck ID: " + deckUser.getId());

		Deck deck = createDeckObject(deckId, deckUser);
		
		this.validSumCards(deck, deckId);
		
		logger.info("Ending edit User Deck: " + deckId);
		
		return deck;
	}
	
	
	private void validSumCards(Deck deck, Long deckId) {
		int sumDecks = deck.getCards().size() + deck.getExtraDeck().size() + deck.getSideDeckCards().size();

		if (sumDecks != deck.getRel_deck_cards().size())
			throw new RuntimeException("Cards quantity don't match relation quantity." + "Param received: deckId = "
					+ deckId + " setType = User" + " SumDecks: " + sumDecks + " Deck Rel.Total: " + deck.getRel_deck_cards().size());
	}

	private Deck createDeckObject(Long deckId, UserDeck deckUser) {
		
		return DeckBuilder.builder()
				.nome(deckUser.getNome())			
				.dt_criacao(new Date())
				.extraDeckList(this.consultExtraDeckCards(deckId, "User"))
				.id(deckUser.getId())
				.imagem(deckUser.getImagem())
				.imgurUrl(deckUser.getImagem())
				.extraDeckList(this.consultExtraDeckCards(deckId, "User"))
				.cardsList(this.consultMainDeck(deckId))
				.relDeckCards(this.relDeckUserCards(deckId))
				.sideDeckList(this.consultSideDeckCards(deckId, "User"))
				.buildForUserDeck();
	}

	private List<Card> consultMainDeck(Long deckId) {
		if (deckId == null || deckId == 0)
			throw new IllegalArgumentException("Invalid Deck ID");

		List<Card> mainDeck = dao.consultMainDeck(deckId);

		if (mainDeck != null && !mainDeck.isEmpty())
			mainDeck = this.sortMainDeckCards(mainDeck);

		return mainDeck;

	}
	
	private List<Card> consultSideDeckCards(Long deckId, String deckSource) {

		if (deckId == null || deckId == 0
				|| (!"user".equalsIgnoreCase(deckSource) && !"konami".equalsIgnoreCase(deckSource)))
			throw new IllegalArgumentException("Invalid Deck Id or Deck Source");

		List<Card> extraDeckCards = dao.consultSideDeckCards(deckId, deckSource);

		return extraDeckCards;
	}

	private List<Card> consultExtraDeckCards(Long deckId, String userOrKonamiDeck) {
		
		if (deckId == null || deckId == 0)
			throw new IllegalArgumentException("Invalid Deck ID");

		List<Card> extraDeckCards = dao.consultExtraDeckCards(deckId, userOrKonamiDeck);

		return extraDeckCards;
	}
	
	private List<RelDeckCards> relDeckUserCards(Long deckUserId) {
		
		if (deckUserId == null || deckUserId == 0)
			throw new IllegalArgumentException("Deck User Id informed is invalid");

		List<RelDeckCards> relation = dao.relDeckUserCards(deckUserId);

		if (relation.isEmpty())
			return Collections.emptyList();

		return relation;
	}

	private List<Card> sortMainDeckCards(List<Card> cardList) {

		if (cardList == null || cardList.isEmpty())
			throw new IllegalArgumentException("Card List is empty");

		List<Card> sortedCardList = new ArrayList<>();
		try {
			
			//  primeiro os cards do tipo Monstro
			cardList.stream().filter(card -> card.getNivel() != null || card.getGenericType() == null) // .sorted(Comparator.comparing(Card::getNome))
					.collect(Collectors.toCollection(() -> sortedCardList));

			// Insere o restante das cartas
			cardList.stream().filter(card -> card.getNivel() == null)
					.sorted((c1, c2) -> c1.getGenericType().compareTo(c2.getGenericType()))
					.collect(Collectors.toCollection(() -> sortedCardList));
			
		} catch(Exception e) {
			logger.error("Error when trying to sort Main Deck");
			return cardList;
		}

		return sortedCardList;
	}

	@Transactional(rollbackFor = Exception.class)
	public int removeSetFromUsersCollection(Long setId) {
		logger.info("Starting removing Set from user collection. ID {}", setId);
		
		int qtdRemoved = 0;

		UserDeck setOrigem = userDeckRepository
				.findById(setId).orElseThrow(() -> new NoSuchElementException("Set not found with this code. ID = " + setId));
		
		if(dao.relDeckUserCards(setId).size() > 0) {
			 qtdRemoved = dao.removeCardsFromUserSet(setId);

			if (qtdRemoved <= 0)
				throw new ErrorMessage("It was not possible remove cards from Deck: " + setId);
		}

		userDeckRepository.deleteById(setOrigem.getId());
		
		logger.info("Cards removed from User Deck: {}", setId);
		
		return qtdRemoved;
	}

	@Transactional(rollbackFor = {Exception.class, ErrorMessage.class})
	public UserDeck saveUserdeck(Deck deck, List<UserRelDeckCards> listRel) {
		logger.info("Starting saving UserDeck...");
		
		UserDeck userDeck = new UserDeck();
		
		this.validUserDeck(deck);

		// Check if it is a new deck or a existing deck
		if (deck.getId() != null && deck.getId() != 0)
			userDeck = updateDeck(deck);
		else
			userDeck = createNewUserDeck(deck);
		
		// FUTURAMENTE COLOCAR PARA EDITAR IMAGEM DO DECK
		if ((userDeck = userDeckRepository.save(userDeck)) == null)
			throw new ErrorMessage(HttpStatus.INTERNAL_SERVER_ERROR, "It was not possible create/update the Deck");
		
		this.saveRelDeckCardsOnSavingUserDeck(listRel, deck);
		
		logger.info("User Deck was saved successfully! ID: {}", deck.getId());
		
		return userDeck;

	}


	private UserDeck updateDeck(Deck deck) {	
		UserDeck userDeck;
		dao.deleteCardsDeckuserByDeckId(deck.getId());
		userDeck = userDeckRepository.getOne(deck.getId());
		userDeck.setNome(deck.getNome());
		return userDeck;
	}
	
	
	private void validUserDeck(Deck deck) {

		if (deck.getNome() == null || deck.getNome().equals(""))
			throw new IllegalArgumentException("UserDeck name cannot be null or empty");

		if (deck.getRel_deck_cards() == null || deck.getRel_deck_cards().isEmpty())
			throw new IllegalArgumentException("There is no card in this deck");
		
		SetType.valueOf(deck.getSetType().toUpperCase());
		
	}
	
	private void saveRelDeckCardsOnSavingUserDeck(List<UserRelDeckCards> listRel, Deck deck) {
		//If its null means dont came from a SetCollection
		if(listRel == null) {
			listRel = new ArrayList<>();
			listRel = deck.getRel_deck_cards().stream().map(rel -> {
				UserRelDeckCards userRel = new UserRelDeckCards();
				BeanUtils.copyProperties(rel, userRel);
				return userRel;
			}).collect(Collectors.toList());			
		}
			
		userRelService.saveAll(listRel);
	}

	private UserDeck createNewUserDeck(Deck deck) {		
		return  UserDeckBuilder
				.builder()
				.userId(GeneralFunctions.userLogged().getId())
				.dtCriacao(new Date())
				.setType("DECK")
				.nome(deck.getNome())
				.imagem(GeneralFunctions.getRandomDeckCase())
				.build();
	}
	
	@Transactional(rollbackFor = {Exception.class, ErrorMessage.class})
	public UserDeck saveUserDeck(UserDeck userDeck) {
		
	userDeck = UserDeckBuilder.builder(userDeck).build();
		
	 UserDeck userDeckSaved = userDeckRepository.save(userDeck);
	 
	 if (userDeckSaved == null)
			throw new ErrorMessage(HttpStatus.INTERNAL_SERVER_ERROR, "It was not possible create/update the User Deck");
	 	
	 if(userDeck.getRelDeckCards() != null && userDeck.getRelDeckCards().size() > 0) 
		userRelService.saveAll(userDeck.getRelDeckCards());
	
	 	logger.info("Save User Deck and RelDeckCards of UserDeck: {}", userDeckSaved.getId());
		
	 	return userDeckSaved;
	}
	
	

//	
//	@Transactional(rollbackFor = Exception.class)
//	private List<UserRelDeckCards> saveRelDeckCardsFromUserDeck(Deck deck, UserDeck userDeck) {
//		
//		List<UserRelDeckCards> listUserRel = deck.getRel_deck_cards().stream().map(rel ->{			
//			return createUserRelDeckCardsObj(userDeck, rel);
//			
//		}).collect(Collectors.toList());
//		
//		return userRelService.saveAll(listUserRel);
//	}
//
//	private UserRelDeckCards createUserRelDeckCardsObj(UserDeck userDeck, RelDeckCards rel) {
//		
//		UserRelDeckCards userRel = new UserRelDeckCards();
//		BeanUtils.copyProperties(rel, userRel);
//		
//		userRel.setCard_price(rel.getCard_price() == null ? 0.00 : rel.getCard_price());
//		userRel.setCard_raridade(rel.getCard_raridade() == null ? ECardRarity.UNKNOWN.getCardRarity() : rel.getCard_raridade());
//		userRel.setCardSetCode(rel.getCard_set_code() == null  ? "Not Defined" : rel.getCard_set_code());			
//		userRel.setQuantity(1);
//		userRel.setDeckId(userDeck.getId());
//		userRel.setId(null);
//		userRel.setDt_criacao(new Date());
//		return userRel;	
//	}
	
	@Transactional(rollbackFor = {Exception.class, ErrorMessage.class})
	public UserDeck addSetToUserCollection(Long originalDeckId) {
		
		logger.info("Starting copy Konami Deck to User's collection");

		Deck deckOrigem = deckService.findById(originalDeckId);

		UserDeck newDeck = new UserDeck();
		newDeck = newDeck.userDeckCopiedFromKonamiDeck(deckOrigem);
		newDeck.setNome(this.customizeDeckName(deckOrigem.getNome()));
		newDeck.setSetType(SetType.DECK.toString());
		UserDeck generatedDeckUser = userDeckRepository.save(newDeck);

		if (generatedDeckUser == null)
			throw new ErrorMessage("It was not possible add Deck to user. Original Deck ID: " + originalDeckId);

		List<UserRelDeckCards> listUserRelCards = userRelService.addCardsToUserDeck(originalDeckId, generatedDeckUser.getId());

		if (listUserRelCards == null || listUserRelCards.size() <= 0)
				throw new ErrorMessage("It was not possible add cards to the new Deck. Original Deck ID: " + originalDeckId);

		if (this.addOrRemoveCardsToUserCollection(originalDeckId, GeneralFunctions.userLogged().getId(), "A") < 1)
				throw new RuntimeException("Unable to include Cards for User's Collection! Original Deck ID: " + originalDeckId);
		
		logger.info("Konami Deck has been copied to User's collection: {}", deckOrigem.getNome());

		return generatedDeckUser;

	}
	
	private String customizeDeckName(String deckName) {

		if (deckName == null || deckName.isBlank())
			throw new IllegalArgumentException("Invalid Deck name, can't be customized!");

		String customizedDeckName = deckName + "_" + GeneralFunctions.momentAsString();

		return customizedDeckName;

	}
	
	
//	@Transactional(rollbackFor = Exception.class)
//	public int addCardsToUserDeck(Long originalDeckId, Long generatedDeckId) {
//
//		if (originalDeckId == null && generatedDeckId == null)
//			new IllegalArgumentException("Original deck or generated deck is invalid.");
//		
//		int cardsAddedToDeck = dao.addCardsToDeck(originalDeckId, generatedDeckId);
//
//		return cardsAddedToDeck;
//
//	}
	
	@Transactional(rollbackFor = Exception.class)
	public int addOrRemoveCardsToUserCollection(Long originalDeckId, long userId, String flagAddOrRemove) {

		int qtdCardsAddedOrRemoved = 0;

		List<DeckDTO> relDeckAndCards = dao.relationDeckAndCards(originalDeckId);

		if (relDeckAndCards == null || relDeckAndCards.size() == 0) 
			return qtdCardsAddedOrRemoved;
		
		if (!flagAddOrRemove.equals("A") && !flagAddOrRemove.equals("R"))
			throw new IllegalArgumentException("Check the Add or Remove parameter sent!");

		for (DeckDTO relation : relDeckAndCards) {
			
			if (dao.verifyIfUserAleadyHasTheCard(userId, relation.getCard_set_code())) {
				
				if (dao.changeQuantityOfEspecifCardUserHas(userId, relation.getCard_set_code(),flagAddOrRemove) < 1)
					throw new NoSuchElementException("It was not possible to manege card to the user's collection!");
				
				qtdCardsAddedOrRemoved++;

			} else {
				
				RelUserCardsDTO rel = new RelUserCardsDTO();
				rel.setUserId(userId);
				rel.setCardNumero(relation.getCard_numero());
				rel.setCardSetCode(relation.getCard_set_code());
				rel.setQtd(1);
				rel.setDtCriacao(new Date());

				if (dao.insertCardToUserCollection(rel) < 1)
					throw new NoSuchElementException("It was not possible to add this Card to the user's collection.");

				qtdCardsAddedOrRemoved++;
			}
		}
				
		return qtdCardsAddedOrRemoved;

	}
	
//	@Transactional(rollbackFor = Exception.class)
//	public int ImanegerCardsToUserCollection(Long originalDeckId, String flagAddOrRemove) {
//
//		long userId = GeneralFunctions.userLogged().getId();
//		int itemAtualizado;
//
//		Integer alreadyHasThisDeck = dao.verifyIfUserAleadyHasTheDeck(originalDeckId, userId);
//
//		// Se o usuário não tiver o Deck e for passado parametro para remover esse deck.
//		if (alreadyHasThisDeck != null && alreadyHasThisDeck == 0 && flagAddOrRemove.equals("R"))
//			return 0;
//
//		if (alreadyHasThisDeck != null && alreadyHasThisDeck == 0) {
//			itemAtualizado = dao.addDeckToUserCollection(originalDeckId, userId);
//
//			if (itemAtualizado < 1)
//				throw new NoSuchElementException(
//						"Unable to include Deck for User! Original Deck ID: " + originalDeckId.toString());
//
//		} else {
//			itemAtualizado = dao.changeQuantitySpecificDeckUserHas(originalDeckId, userId, flagAddOrRemove);
//
//			if (itemAtualizado < 1)
//				throw new NoSuchElementException(
//						"Unable to manege Deck for User! Original Deck ID: " + originalDeckId.toString());
//
//		}
//
//		int qtdAddedOrRemoved = this.addOrRemoveCardsToUserCollection(originalDeckId, userId, flagAddOrRemove);
//
//		if (qtdAddedOrRemoved < 1)
//			throw new NoSuchElementException(
//					"Unable to include Cards for User! Original Deck ID: " + originalDeckId.toString());
//
//		return qtdAddedOrRemoved;
//
//	}

	
	public Page<UserDeck> findAll(Pageable pageable) {
		Page<UserDeck> decks = userDeckRepository.findAll(pageable);

		return decks;
	}


	public Page<UserDeck> findAllBySetType(Pageable pageable, String setType) {
		Page<UserDeck> decks = userDeckRepository.findAllBySetTypeOrderByDtCriacaoDesc(pageable, setType);
		return decks;
	}
	
	public Integer countQuantityOfADeckUserHave(Long konamiDeckId) {
		if(konamiDeckId == null || konamiDeckId == 0)
			throw new IllegalArgumentException("Invalid Deck ID for consulting Quantity of a Deck");
		
		return userDeckRepository.countQuantityOfADeckUserHave(konamiDeckId, GeneralFunctions.userLogged().getId());
		
	}

	public List<DeckAndSetsBySetTypeDTO> getAllDecksName() {
		Long userId = GeneralFunctions.userLogged().getId();
			List<Tuple> tuple =	userDeckRepository.getAllDecksName(userId);
			
			List<DeckAndSetsBySetTypeDTO> listDto = tuple.stream().map(t -> {
				DeckAndSetsBySetTypeDTO dto = new DeckAndSetsBySetTypeDTO(
						Long.parseLong(String.valueOf(t.get(0))),
						String.valueOf(t.get(1))
				);
				return dto;
			}).collect(Collectors.toList());
			
			return listDto;
	}


	public UserSetCollectionDTO getDeckAndCardsForTransfer(Long deckId) {
		UserDeck userDeck = userDeckRepository
				.findById(deckId).orElseThrow(() -> new IllegalArgumentException("User Deck not found! ID: " + deckId));
		
		List<Tuple> tupleCards = userDeckRepository.consultCardsForTransfer(deckId);
		
		UserSetCollectionDTO dto = new UserSetCollectionDTO();
		UserSetCollectionServiceImpl userSetService = new UserSetCollectionServiceImpl();
		
		dto.setId(userDeck.getId());
		dto.setName(userDeck.getNome());
		dto.setImage(userDeck.getImgurUrl());
		dto.setCards(userSetService.transformTupleInCardSetCollectionDTO(tupleCards));
		dto.setCards(userSetService.orderByGenericType(dto.getCards()));
		dto.setRarities(userSetService.countRarities(dto.getCards()));
		dto.setTotalPrice(userSetService.calculateTotalPrice(dto.getCards()));
				
		return dto;
	}

	@Transactional(rollbackFor = {Exception.class, ErrorMessage.class})
	public Long createBasedDeck(Long konamiDeckId) {
		if(konamiDeckId == null)
			throw new IllegalArgumentException("Invalid Konamid Deck ID for create a Based Deck");
		
		UserDeck generatedDeckUser = this.addSetToUserCollection(konamiDeckId);
		
		return generatedDeckUser.getId();
		
	}

}
