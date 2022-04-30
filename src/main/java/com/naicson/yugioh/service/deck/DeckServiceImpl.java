package com.naicson.yugioh.service.deck;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.persistence.EntityManager;
import javax.persistence.EntityNotFoundException;
import javax.persistence.PersistenceContext;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.naicson.yugioh.data.dao.DeckDAO;
import com.naicson.yugioh.data.dto.RelUserCardsDTO;
import com.naicson.yugioh.data.dto.RelUserDeckDTO;
import com.naicson.yugioh.data.dto.cards.CardSetDetailsDTO;
import com.naicson.yugioh.data.dto.set.DeckDTO;
import com.naicson.yugioh.data.dto.set.InsideDeckDTO;
import com.naicson.yugioh.data.dto.set.SetDetailsDTO;
import com.naicson.yugioh.entity.Card;
import com.naicson.yugioh.entity.Deck;
import com.naicson.yugioh.entity.RelDeckCards;
import com.naicson.yugioh.entity.sets.DeckUsers;
import com.naicson.yugioh.repository.DeckRepository;
import com.naicson.yugioh.repository.RelDeckCardsRepository;
import com.naicson.yugioh.repository.sets.DeckUsersRepository;
import com.naicson.yugioh.service.SetsUtils;
import com.naicson.yugioh.service.UserDetailsImpl;
import com.naicson.yugioh.service.interfaces.DeckDetailService;
import com.naicson.yugioh.util.GeneralFunctions;
import com.naicson.yugioh.util.enums.CardRarity;

@Service
public class DeckServiceImpl implements DeckDetailService {


	@PersistenceContext
	EntityManager em;
	
	@Autowired
	DeckDAO dao;
	
	@Autowired
	DeckRepository deckRepository;
	
	@Autowired 
	DeckUsersRepository deckUserRepository;	
	
	@Autowired
	RelDeckCardsRepository relDeckCardsRepository;	
	
	Logger logger = LoggerFactory.getLogger(DeckServiceImpl.class);
	
	public DeckServiceImpl(DeckRepository deckRepository, RelDeckCardsRepository relDeckCardsRepository, DeckDAO dao, DeckUsersRepository deckUserRepo) {
		this.deckRepository = deckRepository;
		this.relDeckCardsRepository = relDeckCardsRepository;
		this.dao = dao;
		this.deckUserRepository = deckUserRepo;
	}

	public DeckServiceImpl() {
	
	}

	@Override
	public Deck findById(Long Id) {		
			if(Id == null || Id == 0)
				throw new IllegalArgumentException("Deck Id informed is invalid.");
					
			 Deck deck =  deckRepository.findById(Id).orElseThrow(() -> new NoSuchElementException("Deck not found."));	
			 
			 return deck;
	}

	@Override
	public List<RelDeckCards> relDeckCards(Long deckId, String setSource) {

		if(deckId == null || deckId == 0)
			throw new IllegalArgumentException("Deck Id informed is invalid.");
		
		List<RelDeckCards> relation = new ArrayList<>();
		
		if("konami".equalsIgnoreCase(setSource))
			relation =	relDeckCardsRepository.findByDeckId(deckId);
		else if("user".equalsIgnoreCase(setSource))
			relation = dao.relDeckUserCards(deckId);
		else
			throw new IllegalArgumentException("Informed Set Source is invalid!" + setSource);
		
		if(relation == null || relation.size() == 0) {
			logger.error("Relation of cards is empty. Deck id: ".toUpperCase() + deckId);
			throw new NoSuchElementException("Relation of cards is empty. Deck id: " + deckId);
		}	
			
		return relation;
		
	}
	
	@Override
	public List<Card> cardsOfDeck(Long deckId, String table) {
		
		List<Card> cards = dao.cardsOfDeck(deckId, table);
		
		if(cards == null || cards.size() == 0)
			throw new IllegalArgumentException("Can't find cards of this Set.");
		
		return cards;
	}
	
	@Override
	public List<RelDeckCards> relDeckUserCards(Long deckUserId){
		if(deckUserId == null || deckUserId == 0)
			throw new IllegalArgumentException("Deck User Id informed is invalid");
		
		List<RelDeckCards> relation = dao.relDeckUserCards(deckUserId);
		
		if(relation.isEmpty())
			throw new NoSuchElementException("Can't find relation");
		
		return relation;
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public int addSetToUserCollection(Long originalDeckId) {

		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		UserDetailsImpl user = (UserDetailsImpl) authentication.getPrincipal();
		
		if (user == null)
			 new EntityNotFoundException("Invalid user!");
		
		Deck deckOrigem = deckRepository.findById(originalDeckId)
				.orElseThrow(() -> new EntityNotFoundException("No Set found with this code."));			
				
		DeckUsers newDeck = new DeckUsers();
		newDeck.setImagem(deckOrigem.getImagem());
		newDeck.setNome(this.customizeDeckName(deckOrigem.getNome()));
		newDeck.setKonamiDeckCopied(deckOrigem.getId());			
		newDeck.setUserId(user.getId());
		newDeck.setDtCriacao(new Date());
		newDeck.setSetType(deckOrigem.getSetType());				
		
		DeckUsers generatedDeckUser = deckUserRepository.save(newDeck);

		if (generatedDeckUser == null) 
			 throw new EntityNotFoundException("It was not possible add Deck to user. Original Deck ID: " + originalDeckId);
					
		//Adiciona os cards do Deck original ao novo Deck.
		int addCardsOnNewDeck = this.addCardsToUserDeck(originalDeckId, generatedDeckUser.getId());
		
		if(addCardsOnNewDeck < 1)
			 throw new NoSuchElementException("It was not possible add cards to the new Deck. Original Deck ID: " + originalDeckId);
					
		//Adiciona os cards a coleção do usuário.
		 int addCardsToUsersCollection = this.addOrRemoveCardsToUserCollection(originalDeckId, user.getId(), "A");

		if (addCardsToUsersCollection < 1) 
			throw new RuntimeException("Unable to include Cards for User's Collection! Original Deck ID: " + originalDeckId);
	

		return addCardsToUsersCollection;
	 
	}
	
	private String customizeDeckName(String deckName) {
		
		if(deckName == null || deckName.isBlank())
			throw new IllegalArgumentException("Invalid Deck name, can't be customized!");
		
		String customizedDeckName = deckName+"_"+GeneralFunctions.momentAsString();
	
		return customizedDeckName;
		
	}
	
	@Override
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
				throw new NoSuchElementException("Unable to include Deck for User! Original Deck ID: " + originalDeckId.toString());
			

		} else {
			itemAtualizado = dao.changeQuantitySpecificDeckUserHas(originalDeckId, user.getId(), flagAddOrRemove);

			if (itemAtualizado < 1) 
				throw new NoSuchElementException("Unable to manege Deck for User! Original Deck ID: " + originalDeckId.toString());
			
		}

		int qtdAddedOrRemoved = this.addOrRemoveCardsToUserCollection(originalDeckId, user.getId(),
				flagAddOrRemove);

		if (qtdAddedOrRemoved < 1)
			throw new NoSuchElementException("Unable to include Cards for User! Original Deck ID: " + originalDeckId.toString());
		
		return qtdAddedOrRemoved;

	}
	
	@Override
	@Transactional(rollbackFor = Exception.class)
	public int addOrRemoveCardsToUserCollection(Long originalDeckId, long userId, String flagAddOrRemove) {
			
			int qtdCardsAddedOrRemoved = 0;

			List<DeckDTO> relDeckAndCards = dao.relationDeckAndCards(originalDeckId);

			if (relDeckAndCards != null && relDeckAndCards.size() > 0) {
				if (flagAddOrRemove.equals("A") || flagAddOrRemove.equals("R")) {

					for (DeckDTO relation : relDeckAndCards) {
						// Verifica se o usuário ja possui essa carta.
						boolean alreadyHasThisCard = dao.verifyIfUserAleadyHasTheCard(userId,
								relation.getCard_set_code());

						if (alreadyHasThisCard == true) {
							// Remove ou adiciona a qtd desta carta de acordo com a flag passada.
							int manegeQtd = dao.changeQuantityOfEspecifCardUserHas(userId, relation.getCard_set_code(),
									flagAddOrRemove);

							if (manegeQtd < 1) 
								 throw new NoSuchElementException("It was not possible to manege card to the user's collection!");
							

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
								throw new NoSuchElementException("It was not possible to add this Card to the user's collection.");
										
							qtdCardsAddedOrRemoved++;
						}
					}

				} else 
					throw new IllegalArgumentException("Check the Add or Remove parameter sent!");
				
			} 

			return qtdCardsAddedOrRemoved;

	}
	
	@Override
	public List<RelUserDeckDTO> searchForDecksUserHave(Long[] decksIds) {
		
		if (decksIds == null || decksIds.length == 0)
			throw new IllegalArgumentException("Invalid Array with Deck's Ids");
					
		UserDetailsImpl user = GeneralFunctions.userLogged();

		String decksIdsString = GeneralFunctions.transformArrayInStringForLong(decksIds);

		if(decksIdsString == null || decksIdsString.isBlank() || decksIdsString.equals("0"))
			throw new RuntimeException("String with deck Ids is invalid: " + decksIdsString);

		List<RelUserDeckDTO> relUserDeckList = dao.searchForDecksUserHave(user.getId(), decksIdsString);
		
		return relUserDeckList;

	}
	
	@Override
	@Transactional(rollbackFor = Exception.class)
	public Long addDeck(Deck deck)  {

		if (deck == null)
			throw new IllegalArgumentException("Invalid Deck informed");
			
			dao = new DeckDAO();
			Long id = dao.addDeck(deck);
		
		return id;
	}
	
	@Override
	@Transactional(rollbackFor = Exception.class)
	public int addCardsToUserDeck(Long originalDeckId, Long generatedDeckId) {
				
			if (originalDeckId == null && generatedDeckId == null) 
				 new IllegalArgumentException("Original deck or generated deck is invalid.");
					
			int cardsAddedToDeck = dao.addCardsToDeck(originalDeckId, generatedDeckId);
			
			return cardsAddedToDeck;	
			
	}
	
	@Override
	@Transactional(rollbackFor = Exception.class)
	public int removeSetFromUsersCollection(Long setId) {
		
		Optional<DeckUsers> dk = deckUserRepository.findById(setId);
		
		if(dk.isEmpty()) 
			throw new NoSuchElementException("Set not found with this code. Id = " + setId);
			
		DeckUsers setOrigem = dk.get();

		int qtdRemoved = dao.removeCardsFromUserSet(setId);

		deckUserRepository.deleteById(setOrigem.getId());
		
		return qtdRemoved;	
	}
	
	@Override
	public SetDetailsDTO deckAndCards(Long deckId, String deckSource) {
		
		Deck deck = new Deck();
		
		SetsUtils utils = new SetsUtils();
		
		if(!("Konami").equalsIgnoreCase(deckSource) && !("User").equalsIgnoreCase(deckSource)) 
			throw new IllegalArgumentException("Deck Source invalid: " + deckSource);			
				
		deck =  this.returnDeckWithCards(deckId, deckSource);
		deck = this.countQtdCardRarityInTheDeck(deck);
		
		SetDetailsDTO dto = convertDeckToSetDetailsDTO(deck);
		
		dto = utils.getSetStatistics(dto);
		
		return dto;
	}
	
	private SetDetailsDTO convertDeckToSetDetailsDTO(Deck deck) {
		
		SetDetailsDTO dto  = new SetDetailsDTO();
		InsideDeckDTO insideDeck = new InsideDeckDTO();
	
		BeanUtils.copyProperties(deck, dto);
		
		Map<Long, CardSetDetailsDTO> mapCardSetDetails = new HashMap<>();
		
		List<CardSetDetailsDTO>	cardDetailsList = deck.getCards().stream().map(c -> {
			CardSetDetailsDTO cardDetail = new CardSetDetailsDTO();
			BeanUtils.copyProperties(c, cardDetail);
			
			mapCardSetDetails.put(cardDetail.getNumero(), cardDetail);
			
			return cardDetail;
				
		}).collect(Collectors.toList());
			
		deck.getRel_deck_cards().forEach(r -> {
			CardSetDetailsDTO detail =  mapCardSetDetails.get(r.getCardNumber());		
			BeanUtils.copyProperties(r, detail);
		});	
	
		insideDeck.setCards(cardDetailsList);
		
		dto.setInsideDecks(List.of(insideDeck));
		
		return dto;
	}
	
	@Override
	public Deck returnDeckWithCards(Long deckId, String deckSource) {
		
		if(deckId == null || deckId == 0)
			throw new  IllegalArgumentException("Invalid Deck Id. deckId = " + deckId);
		
		Deck deck = new Deck();
		List<Card> mainDeck = null;
		
		if("konami".equalsIgnoreCase(deckSource))
			deck = this.findById(deckId);
		
		else if("user".equalsIgnoreCase(deckSource)) {
			DeckUsers deckUser = deckUserRepository.findById(deckId).orElseThrow(() -> new EntityNotFoundException());
			deck = Deck.deckFromDeckUser(deckUser);
			
		} else
			throw new IllegalArgumentException("Invalid Deck Source: " + deckSource);
		
		if(deck == null)
			throw new EntityNotFoundException("Deck not found. Id informed: " + deckId);
				
		String table = ("konami").equalsIgnoreCase(deckSource) ? "tab_rel_deck_cards" : "tab_rel_deckusers_cards";
		
		mainDeck = this.cardsOfDeck(deckId, table);
		List<RelDeckCards> relDeckCards = this.relDeckCards(deckId, deckSource);
		
		deck.setCards(mainDeck);
		deck.setRel_deck_cards(relDeckCards);
		
		return deck;		
	}
	
	@Override
	public Deck editUserDeck(Long deckId) {
		
		if(deckId == null || deckId == 0)
			throw new  IllegalArgumentException("Invalid Deck Id: " + deckId);
		
		Deck deck = new Deck();
		
		List<Card> mainDeck = null;
		DeckUsers deckUser = new DeckUsers();
		
		deckUser = this.deckUserRepository.findById(deckId).orElseThrow(() -> new EntityNotFoundException("UserDeck id = " + deckId));
		
		UserDetailsImpl user = GeneralFunctions.userLogged();
		
		if(user.getId() != deckUser.getUserId()) 
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
		
		if(sumDecks != deck.getRel_deck_cards().size())
			throw new RuntimeException("Cards quantity don't match relation quantity." + 
					"Param received: deckId = " + deckId + " setType = User");
		
		return deck;
	}
	
	@Override
	public List<Card> consultSideDeckCards(Long deckId, String deckSource) {
		
		if(deckId == null || deckId == 0 || ( !"user".equalsIgnoreCase(deckSource) && !"konami".equalsIgnoreCase(deckSource)))
			throw new IllegalArgumentException("Invalid Deck Id or Deck Source");
		
		List<Card> extraDeckCards = dao.consultSideDeckCards(deckId, deckSource);
		
		return extraDeckCards;
	}
	
	@Override
	public List<Card> consultMainDeck(Long deckId) {
		if(deckId == null || deckId == 0)
			throw new IllegalArgumentException("Invalid Deck ID");
		
		List<Card> mainDeck = dao.consultMainDeck(deckId);
		
		if(mainDeck == null || mainDeck.isEmpty())
			throw new NoSuchElementException("No cards found for Main Deck");
	
		mainDeck = this.sortMainDeckCards(mainDeck);
		
		return mainDeck;
		
	}
	
	@Override
	public List<Card> consultExtraDeckCards(Long deckId, String userOrKonamiDeck) {
		if(deckId == null || deckId == 0)
			throw new IllegalArgumentException("Invalid Deck ID");
		
		List<Card> extraDeckCards = dao.consultExtraDeckCards(deckId, userOrKonamiDeck);
		
		return extraDeckCards;
	}
	
	@Override
	public List<Card> sortMainDeckCards(List<Card> cardList) {
		
		if(cardList != null && cardList.isEmpty())
			throw new IllegalArgumentException("Card List is empty");
		
		List<Card> sortedCardList = new ArrayList<>();
		
		//Insere primeiro os cards do tipo Monstro
		cardList.stream().filter(card -> card.getNivel() != null) //.sorted(Comparator.comparing(Card::getNome))
		.collect(Collectors.toCollection(() -> sortedCardList));
			
		//Coloca o restante das cartas
		cardList.stream().filter(card -> card.getNivel() == null)
		.sorted((c1, c2) -> c1.getGenericType().compareTo(c2.getGenericType()))
		.collect(Collectors.toCollection(() -> sortedCardList));
				
		return sortedCardList;
	}
	
  @Transactional
  @Override
  public void saveUserdeck(Deck deck) throws SQLException {
	DeckUsers userDeck = new DeckUsers();
	
	if(deck.getNome() == null || deck.getNome().equals("")) {
		logger.error("USERDECK NAME IS NULL OR EMPTY");
		throw new IllegalArgumentException("UserDeck name cannot be null or empty");		
	}
	
	if( deck.getRel_deck_cards() == null ||  deck.getRel_deck_cards().isEmpty()) {
		logger.error("DECK HAVE NO CARD INSERTED");
		throw new IllegalArgumentException("There is no card in this deck");	
	}
	
	//Check if it is a new deck or a existing deck
	if(deck.getId() != null && deck.getId() != 0) {
		dao.deleteCardsDeckuserByDeckId(deck.getId());
		userDeck = deckUserRepository.getOne(deck.getId());
		
	} else {
		UserDetailsImpl user = GeneralFunctions.userLogged();
		userDeck.setUserId(user.getId());
		userDeck.setDtCriacao(new Date());
		userDeck.setSetType("D");
	}		
	
		//FUTURAMENTE COLOCAR PARA EDITAR IMAGEM DO DECK
		userDeck.setNome(deck.getNome());
		userDeck = deckUserRepository.save(userDeck);
		
		if(userDeck == null) {
			logger.error("IT WAS NOT POSSIBLE INSERT A NEW USERDECK");
			throw new SQLException("It was not possible create/update the Deck");
		}	
		
	for(RelDeckCards rel : deck.getRel_deck_cards()) {
		
		if(rel.getCard_price() == null )
			rel.setCard_price(0.00);
		
		if(rel.getCard_raridade() == null || rel.getCard_raridade().isEmpty())
			rel.setCard_raridade(CardRarity.NOT_DEFINED.getCardRarity());
		
		if(rel.getCard_set_code() == null || rel.getCard_set_code().isEmpty())
			rel.setCard_set_code("Not Defined");
		
		int isSaved = dao.saveRelDeckUserCard(rel, userDeck.getId());
		
		if(isSaved == 0) {
			logger.error("IT WAS NOT POSSIBLE SAVE A CARD IN USERDECK");
			throw new SQLException("It was not possible save the card " + rel.getCard_set_code());
		}		
	}
		
 }

	@Override
	public List<Deck> searchByDeckName(String setName, String source) {
		
		this.validSearchByDeckName(setName, source);
		
		List<Deck> setsFound = null;
		
		if("K".equals(source)) {
			setsFound = this.deckRepository.findTop30ByNomeContaining(setName);
			
		} else if("U".equals(source)) {
			List<DeckUsers> deckUser = this.deckUserRepository.findTop30ByNomeContaining(setName);
			
			setsFound = deckUser.stream().map(du -> {
				Deck deck = Deck.deckFromDeckUser(du);			
				return deck;
			}).collect(Collectors.toList());
			
		}
		
		if(setsFound == null || setsFound.isEmpty())
			 return Collections.emptyList();
		
		return setsFound;				
	}
	
	private void validSearchByDeckName(String setName, String source) {
		
		if(setName.isEmpty() || setName.length() <= 5) {
			logger.error("Invalid set name for searching. Set name was = " + setName);
			throw new IllegalArgumentException("Invalid set name for searching");
		}	
		
		if(!"K".equals(source) && !"U".equals(source)) {
				logger.error("Invalid source for search a Set".toUpperCase());
				throw new IllegalAccessError("Invalid source for search a Set");
		}
	}

	@Override
	public Deck countQtdCardRarityInTheDeck(Deck deck) {
		
		deck.setQtd_cards(deck.getRel_deck_cards().stream().count());
		
		deck.setQtd_comuns(deck.getRel_deck_cards().stream().filter(card -> 
			card.getCard_raridade().equals(CardRarity.COMMON.getCardRarity())).count());
		deck.setQtd_raras(deck.getRel_deck_cards().stream().filter(card ->
			card.getCard_raridade().equals(CardRarity.RARE.getCardRarity())).count());
		deck.setQtd_super_raras(deck.getRel_deck_cards().stream().filter(card -> 
			card.getCard_raridade().equals(CardRarity.SUPER_RARE.getCardRarity())).count());
		deck.setQtd_ultra_raras(deck.getRel_deck_cards().stream().filter(card -> 
			card.getCard_raridade().equals(CardRarity.ULTRA_RARE.getCardRarity())).count());
		deck.setQtd_secret_raras(deck.getRel_deck_cards().stream().filter(card -> 
			card.getCard_raridade().equals(CardRarity.SECRET_RARE.getCardRarity())).count());
		
		return deck;
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public Deck saveKonamiDeck(Deck kDeck) {
		
		try {
			List<Deck> isAlreadyRegistered = deckRepository.findTop30ByNomeContaining(kDeck.getNome());
			
			if(isAlreadyRegistered == null || isAlreadyRegistered.size() == 0){
				kDeck = deckRepository.save(kDeck);
			} else {
				logger.error("Deck is already registered! Deck ID: ".toUpperCase() + isAlreadyRegistered.get(0).getId());
				throw new Exception("Deck is already registered");
			}
		}catch(Exception e) {
			e.getMessage();
		}
		
		return kDeck;
	}

	@Override
	public Page<Deck> findAll(Pageable pageable) {
		Page<Deck> decks = deckRepository.findAll(pageable);
		
		return decks;
	}
	
}
