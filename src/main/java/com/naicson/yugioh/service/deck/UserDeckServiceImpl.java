package com.naicson.yugioh.service.deck;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.persistence.EntityNotFoundException;
import javax.persistence.Tuple;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.naicson.yugioh.data.dao.DeckDAO;
import com.naicson.yugioh.data.dto.RelUserCardsDTO;
import com.naicson.yugioh.data.dto.RelUserDeckDTO;
import com.naicson.yugioh.data.dto.set.DeckAndSetsBySetTypeDTO;
import com.naicson.yugioh.data.dto.set.DeckDTO;
import com.naicson.yugioh.entity.Card;
import com.naicson.yugioh.entity.Deck;
import com.naicson.yugioh.entity.RelDeckCards;
import com.naicson.yugioh.entity.sets.UserDeck;
import com.naicson.yugioh.repository.sets.UserDeckRepository;
import com.naicson.yugioh.service.user.UserDetailsImpl;
import com.naicson.yugioh.util.GeneralFunctions;
import com.naicson.yugioh.util.enums.CardRarity;
import com.naicson.yugioh.util.enums.SetType;
import com.naicson.yugioh.util.exceptions.ErrorMessage;

@Service
public class UserDeckServiceImpl {

	@Autowired
	UserDeckRepository userDeckRepository;
	
	@Autowired
	DeckServiceImpl deckService;

	@Autowired
	DeckDAO dao;

	Logger logger = LoggerFactory.getLogger(UserDeckServiceImpl.class);

	public Deck editUserDeck(Long deckId) {

		if (deckId == null || deckId == 0)
			throw new IllegalArgumentException("Invalid Deck Id: " + deckId);

		Deck deck = new Deck();

		List<Card> mainDeck = null;
		UserDeck deckUser = new UserDeck();

		deckUser = this.userDeckRepository.findById(deckId)
				.orElseThrow(() -> new EntityNotFoundException("UserDeck id = " + deckId));

		UserDetailsImpl user = GeneralFunctions.userLogged();

		if (user.getId() != deckUser.getUserId())
			throw new RuntimeException("This Deck dont belong to user: " + user.getId());

		deck.setNome(deckUser.getNome());
		deck.setImagem(deckUser.getImagem());
		deck.setDt_criacao(deckUser.getDtCriacao());
		deck.setId(deckUser.getId());

		mainDeck = this.consultMainDeck(deckId);

		List<Card> sideDeckCards = this.consultSideDeckCards(deckId, "User");
		List<Card> extraDeck = this.consultExtraDeckCards(deckId, "User");

		deck.setCards(mainDeck);
		deck.setExtraDeck(extraDeck);
		deck.setSideDeckCards(sideDeckCards);

		deck.setRel_deck_cards(this.relDeckUserCards(deckId));

		int sumDecks = deck.getCards().size() + deck.getExtraDeck().size() + deck.getSideDeckCards().size();

		if (sumDecks != deck.getRel_deck_cards().size())
			throw new RuntimeException("Cards quantity don't match relation quantity." + "Param received: deckId = "
					+ deckId + " setType = User" + " SumDecks: " + sumDecks + " Deck Rel.Total: " + deck.getRel_deck_cards().size());

		return deck;
	}


	private List<Card> consultMainDeck(Long deckId) {
		if (deckId == null || deckId == 0)
			throw new IllegalArgumentException("Invalid Deck ID");

		List<Card> mainDeck = dao.consultMainDeck(deckId);

		if (mainDeck == null || mainDeck.isEmpty())
			throw new NoSuchElementException("No cards found for Main Deck");

		mainDeck = this.sortMainDeckCards(mainDeck);

		return mainDeck;

	}

	private List<Card> sortMainDeckCards(List<Card> cardList) {

		if (cardList != null && cardList.isEmpty())
			throw new IllegalArgumentException("Card List is empty");

		List<Card> sortedCardList = new ArrayList<>();

		// Insere primeiro os cards do tipo Monstro
		cardList.stream().filter(card -> card.getNivel() != null) // .sorted(Comparator.comparing(Card::getNome))
				.collect(Collectors.toCollection(() -> sortedCardList));

		// Coloca o restante das cartas
		cardList.stream().filter(card -> card.getNivel() == null)
				.sorted((c1, c2) -> c1.getGenericType().compareTo(c2.getGenericType()))
				.collect(Collectors.toCollection(() -> sortedCardList));

		return sortedCardList;
	}

	@Transactional(rollbackFor = Exception.class)
	public int removeSetFromUsersCollection(Long setId) {
		logger.info("Starting removing Set from user collection. ID {}", setId);
		int qtdRemoved = 0;

		Optional<UserDeck> dk = userDeckRepository.findById(setId);

		if (dk.isEmpty())
			throw new NoSuchElementException("Set not found with this code. Id = " + setId);

		UserDeck setOrigem = dk.get();
		
		if(dao.relDeckUserCards(setId).size() > 0) {
			 qtdRemoved = dao.removeCardsFromUserSet(setId);

			if (qtdRemoved <= 0)
				throw new ErrorMessage("It was not possible remove cards from Deck: " + setId);
		}

		userDeckRepository.deleteById(setOrigem.getId());
		
		logger.info("Cards removed from User Deck: {}", setId);
		
		return qtdRemoved;
	}

	public List<Card> consultSideDeckCards(Long deckId, String deckSource) {

		if (deckId == null || deckId == 0
				|| (!"user".equalsIgnoreCase(deckSource) && !"konami".equalsIgnoreCase(deckSource)))
			throw new IllegalArgumentException("Invalid Deck Id or Deck Source");

		List<Card> extraDeckCards = dao.consultSideDeckCards(deckId, deckSource);

		return extraDeckCards;
	}

	public List<Card> consultExtraDeckCards(Long deckId, String userOrKonamiDeck) {
		if (deckId == null || deckId == 0)
			throw new IllegalArgumentException("Invalid Deck ID");

		List<Card> extraDeckCards = dao.consultExtraDeckCards(deckId, userOrKonamiDeck);

		return extraDeckCards;
	}

	public List<RelDeckCards> relDeckUserCards(Long deckUserId) {
		if (deckUserId == null || deckUserId == 0)
			throw new IllegalArgumentException("Deck User Id informed is invalid");

		List<RelDeckCards> relation = dao.relDeckUserCards(deckUserId);

		if (relation.isEmpty())
			return Collections.emptyList();

		return relation;
	}
	
	@Transactional
	public void saveUserdeck(Deck deck) {
		UserDeck userDeck = new UserDeck();
		
		this.validUserDeck(deck);

		// Check if it is a new deck or a existing deck
		if (deck.getId() != null && deck.getId() != 0) {
			dao.deleteCardsDeckuserByDeckId(deck.getId());
			userDeck = userDeckRepository.getOne(deck.getId());

		} else {
			UserDetailsImpl user = GeneralFunctions.userLogged();
			userDeck.setUserId(user.getId());
			userDeck.setDtCriacao(new Date());
			userDeck.setSetType("DECK");

		}

		// FUTURAMENTE COLOCAR PARA EDITAR IMAGEM DO DECK
		userDeck.setNome(deck.getNome());
		userDeck = userDeckRepository.save(userDeck);

		if (userDeck == null)
			throw new ErrorMessage(HttpStatus.INTERNAL_SERVER_ERROR, "It was not possible create/update the Deck");
			
		this.saveRelDeckCardsFromUserDeck(deck, userDeck);

		logger.info("User Deck was saved! ID: {}", deck.getId());

	}
	
	@Transactional
	public UserDeck saveUserDeck(UserDeck userDeck) {
		this.validUserDeck(userDeck);
		
	 UserDeck userDeckSaved = userDeckRepository.save(userDeck);
	 
	 if (userDeckSaved == null)
			throw new ErrorMessage(HttpStatus.INTERNAL_SERVER_ERROR, "It was not possible create/update the User Deck");
	 	
	 if(userDeck.getRelDeckCards() != null && userDeck.getRelDeckCards().size() > 0) {
		 userDeck.getRelDeckCards().stream().forEach(rel -> {
		 		dao.saveRelDeckUserCard(rel, userDeckSaved.getId());
		 	});
	 }
	 	 	
	 	logger.info("Save User Deck and RelDeckCards of UserDeck: {}", userDeckSaved.getId());
		
	 	return userDeckSaved;
	}


	private void validUserDeck(UserDeck userDeck) {
		
		if(userDeck == null)
			throw new IllegalArgumentException("Invalid UserDeck");
		
		if (userDeck.getNome() == null || userDeck.getNome().isBlank())
			throw new IllegalArgumentException("UserDeck name cannot be null or empty");

		if (userDeck.getImagem() == null || userDeck.getImagem().isBlank())
			throw new IllegalArgumentException("UserDeck Image cannot be null or empty");
		
		if (userDeck.getDtCriacao() == null)
			throw new IllegalArgumentException("UserDeck Creation Date cannot be null or empty");
		
		if (userDeck.getIsSpeedDuel() == null)
			throw new IllegalArgumentException("UserDeck IsSpeedDuel cannot be null or empty");
		
		SetType.valueOf(userDeck.getSetType());
		
	}


	private void saveRelDeckCardsFromUserDeck(Deck deck, UserDeck userDeck) {
		
		for (RelDeckCards rel : deck.getRel_deck_cards()) {

			if (rel.getCard_price() == null)
				rel.setCard_price(0.00);

			if (rel.getCard_raridade() == null || rel.getCard_raridade().isEmpty())
				rel.setCard_raridade(CardRarity.NOT_DEFINED.getCardRarity());

			if (rel.getCard_set_code() == null || rel.getCard_set_code().isEmpty())
				rel.setCard_set_code("Not Defined");
			
			rel.setQuantity(1);

			int isSaved = dao.saveRelDeckUserCard(rel, userDeck.getId());

			if (isSaved == 0)
				throw new ErrorMessage("It was not possible save the card " + rel.getCard_set_code());
		}
	}



	@Transactional(rollbackFor = Exception.class)
	public int addSetToUserCollection(Long originalDeckId) {

		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		UserDetailsImpl user = (UserDetailsImpl) authentication.getPrincipal();

		if (user == null)
			new EntityNotFoundException("Invalid user!");

		Deck deckOrigem = deckService.findById(originalDeckId);

		UserDeck newDeck = new UserDeck();
		newDeck.setImagem(deckOrigem.getImgurUrl());
		newDeck.setImgurUrl(deckOrigem.getImgurUrl());
		newDeck.setNome(this.customizeDeckName(deckOrigem.getNome()));
		newDeck.setKonamiDeckCopied(deckOrigem.getId());
		newDeck.setUserId(user.getId());
		newDeck.setDtCriacao(new Date());
		newDeck.setSetType(deckOrigem.getSetType());
		newDeck.setIsSpeedDuel(deckOrigem.getIsSpeedDuel());

		UserDeck generatedDeckUser = userDeckRepository.save(newDeck);

		if (generatedDeckUser == null)
			throw new EntityNotFoundException(
					"It was not possible add Deck to user. Original Deck ID: " + originalDeckId);

		// Adiciona os cards do Deck original ao novo Deck.
		int addCardsOnNewDeck = this.addCardsToUserDeck(originalDeckId, generatedDeckUser.getId());

		if (addCardsOnNewDeck < 1)
			throw new NoSuchElementException(
					"It was not possible add cards to the new Deck. Original Deck ID: " + originalDeckId);

		// Adiciona os cards a coleção do usuário.
		int addCardsToUsersCollection = this.addOrRemoveCardsToUserCollection(originalDeckId, user.getId(), "A");

		if (addCardsToUsersCollection < 1)
			throw new RuntimeException(
					"Unable to include Cards for User's Collection! Original Deck ID: " + originalDeckId);

		return addCardsToUsersCollection;

	}
	
	private String customizeDeckName(String deckName) {

		if (deckName == null || deckName.isBlank())
			throw new IllegalArgumentException("Invalid Deck name, can't be customized!");

		String customizedDeckName = deckName + "_" + GeneralFunctions.momentAsString();

		return customizedDeckName;

	}
	
	@Transactional(rollbackFor = Exception.class)
	public int addCardsToUserDeck(Long originalDeckId, Long generatedDeckId) {

		if (originalDeckId == null && generatedDeckId == null)
			new IllegalArgumentException("Original deck or generated deck is invalid.");

		int cardsAddedToDeck = dao.addCardsToDeck(originalDeckId, generatedDeckId);

		return cardsAddedToDeck;

	}
	
	@Transactional(rollbackFor = Exception.class)
	public int addOrRemoveCardsToUserCollection(Long originalDeckId, long userId, String flagAddOrRemove) {

		int qtdCardsAddedOrRemoved = 0;

		List<DeckDTO> relDeckAndCards = dao.relationDeckAndCards(originalDeckId);

		if (relDeckAndCards != null && relDeckAndCards.size() > 0) {
			if (flagAddOrRemove.equals("A") || flagAddOrRemove.equals("R")) {

				for (DeckDTO relation : relDeckAndCards) {
					// Verifica se o usuário ja possui essa carta.
					boolean alreadyHasThisCard = dao.verifyIfUserAleadyHasTheCard(userId, relation.getCard_set_code());

					if (alreadyHasThisCard == true) {
						// Remove ou adiciona a qtd desta carta de acordo com a flag passada.
						int manegeQtd = dao.changeQuantityOfEspecifCardUserHas(userId, relation.getCard_set_code(),
								flagAddOrRemove);

						if (manegeQtd < 1)
							throw new NoSuchElementException(
									"It was not possible to manege card to the user's collection!");

						qtdCardsAddedOrRemoved++;

					} else {
						// Caso o usuário não tenha o Card, simplesmente da um insert desse card na
						// coleção do usuário.
						RelUserCardsDTO rel = new RelUserCardsDTO();
						rel.setUserId(userId);
						rel.setCardNumero(relation.getCard_numero());
						rel.setCardSetCode(relation.getCard_set_code());
						rel.setQtd(1);
						rel.setDtCriacao(new Date());

						int insertCard = dao.insertCardToUserCollection(rel);

						if (insertCard < 1)
							throw new NoSuchElementException(
									"It was not possible to add this Card to the user's collection.");

						qtdCardsAddedOrRemoved++;
					}
				}

			} else
				throw new IllegalArgumentException("Check the Add or Remove parameter sent!");

		}

		return qtdCardsAddedOrRemoved;

	}
	
//
//	public List<RelUserDeckDTO> searchForDecksUserHave(Long[] decksIds) {
//
//		if (decksIds == null || decksIds.length == 0)
//			throw new IllegalArgumentException("Invalid Array with Deck's Ids");
//
//		UserDetailsImpl user = GeneralFunctions.userLogged();
//
//		String decksIdsString = GeneralFunctions.transformArrayInStringForLong(decksIds);
//
//		if (decksIdsString == null || decksIdsString.isBlank() || decksIdsString.equals("0"))
//			throw new RuntimeException("String with deck Ids is invalid: " + decksIdsString);
//
//		List<RelUserDeckDTO> relUserDeckList = dao.searchForDecksUserHave(user.getId(), decksIdsString);
//
//		return relUserDeckList;
//
//	}
	
	@Transactional(rollbackFor = Exception.class)
	public int ImanegerCardsToUserCollection(Long originalDeckId, String flagAddOrRemove) {

		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		UserDetailsImpl user = (UserDetailsImpl) authentication.getPrincipal();
		int itemAtualizado;

		Integer alreadyHasThisDeck = dao.verifyIfUserAleadyHasTheDeck(originalDeckId, user.getId());

		// Se o usuário não tiver o Deck e for passado parametro para remover esse deck.
		if (alreadyHasThisDeck != null && alreadyHasThisDeck == 0 && flagAddOrRemove.equals("R"))
			return 0;

		if (alreadyHasThisDeck != null && alreadyHasThisDeck == 0) {
			itemAtualizado = dao.addDeckToUserCollection(originalDeckId, user.getId());

			if (itemAtualizado < 1)
				throw new NoSuchElementException(
						"Unable to include Deck for User! Original Deck ID: " + originalDeckId.toString());

		} else {
			itemAtualizado = dao.changeQuantitySpecificDeckUserHas(originalDeckId, user.getId(), flagAddOrRemove);

			if (itemAtualizado < 1)
				throw new NoSuchElementException(
						"Unable to manege Deck for User! Original Deck ID: " + originalDeckId.toString());

		}

		int qtdAddedOrRemoved = this.addOrRemoveCardsToUserCollection(originalDeckId, user.getId(), flagAddOrRemove);

		if (qtdAddedOrRemoved < 1)
			throw new NoSuchElementException(
					"Unable to include Cards for User! Original Deck ID: " + originalDeckId.toString());

		return qtdAddedOrRemoved;

	}
	
	private void validUserDeck(Deck deck) {

		if (deck.getNome() == null || deck.getNome().equals(""))
			throw new IllegalArgumentException("UserDeck name cannot be null or empty");

		if (deck.getRel_deck_cards() == null || deck.getRel_deck_cards().isEmpty())
			throw new IllegalArgumentException("There is no card in this deck");
		
		SetType.valueOf(deck.getSetType());
		
	}
	
	public Page<UserDeck> findAll(Pageable pageable) {
		Page<UserDeck> decks = userDeckRepository.findAll(pageable);

		return decks;
	}


	public Page<UserDeck> findAllBySetType(Pageable pageable, String setType) {
		Page<UserDeck> decks = userDeckRepository.findAllBySetType(pageable, setType);
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


}
