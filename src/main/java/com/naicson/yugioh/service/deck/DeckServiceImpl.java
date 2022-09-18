package com.naicson.yugioh.service.deck;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

import javax.persistence.EntityManager;
import javax.persistence.EntityNotFoundException;
import javax.persistence.PersistenceContext;
import javax.persistence.Tuple;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.naicson.yugioh.data.dao.DeckDAO;
import com.naicson.yugioh.data.dto.cards.CardSetDetailsDTO;
import com.naicson.yugioh.data.dto.set.AutocompleteSetDTO;
import com.naicson.yugioh.data.dto.set.DeckAndSetsBySetTypeDTO;
import com.naicson.yugioh.data.dto.set.DeckSummaryDTO;
import com.naicson.yugioh.data.dto.set.InsideDeckDTO;
import com.naicson.yugioh.data.dto.set.SetDetailsDTO;
import com.naicson.yugioh.entity.Card;
import com.naicson.yugioh.entity.Deck;
import com.naicson.yugioh.entity.RelDeckCards;
import com.naicson.yugioh.entity.sets.UserDeck;
import com.naicson.yugioh.repository.DeckRepository;
import com.naicson.yugioh.repository.RelDeckCardsRepository;
import com.naicson.yugioh.repository.sets.UserDeckRepository;
import com.naicson.yugioh.service.interfaces.DeckDetailService;
import com.naicson.yugioh.service.setcollection.SetsUtils;
import com.naicson.yugioh.util.GeneralFunctions;
import com.naicson.yugioh.util.enums.ECardRarity;
import com.naicson.yugioh.util.exceptions.ErrorMessage;

@Service
public class DeckServiceImpl implements DeckDetailService {

	@PersistenceContext
	EntityManager em;

	@Autowired
	DeckDAO dao;

	@Autowired
	DeckRepository deckRepository;
	
	@Autowired
	RelDeckCardsRepository relDeckCardsRepository;
	
	@Autowired
	UserDeckRepository userDeckRepository;


	Logger logger = LoggerFactory.getLogger(DeckServiceImpl.class);

	public DeckServiceImpl(DeckRepository deckRepository, RelDeckCardsRepository relDeckCardsRepository, DeckDAO dao) {
		this.deckRepository = deckRepository;
		this.relDeckCardsRepository = relDeckCardsRepository;
		this.dao = dao;

	}

	public DeckServiceImpl() {

	}

	@Override
	public Deck findById(Long Id) {
		if (Id == null || Id == 0)
			throw new IllegalArgumentException("Deck Id informed is invalid.");

		Deck deck = deckRepository.findById(Id).orElseThrow(() -> new NoSuchElementException("Deck not found."));

		return deck;
	}

	@Override
	public List<RelDeckCards> relDeckCards(Long deckId, String setSource) {

		if (deckId == null || deckId == 0)
			throw new IllegalArgumentException("Deck Id informed is invalid.");

		List<RelDeckCards> relation = new ArrayList<>();

		if ("konami".equalsIgnoreCase(setSource))
			relation = relDeckCardsRepository.findByDeckId(deckId);
		else if ("user".equalsIgnoreCase(setSource))
			relation = dao.relDeckUserCards(deckId);
		else
			throw new IllegalArgumentException("Informed Set Source is invalid!" + setSource);

		if (relation == null || relation.size() == 0) 
			return Collections.emptyList();

		return relation;

	}

	@Override
	public List<Card> cardsOfDeck(Long deckId, String table) {

		List<Card> cards = dao.cardsOfDeck(deckId, table);
		
		if(cards == null || cards.size() == 0)
			return Collections.emptyList();
					
		return cards;
	}


	@Override
	@Transactional(rollbackFor = Exception.class)
	public Long addDeck(Deck deck) {

		if (deck == null)
			throw new IllegalArgumentException("Invalid Deck informed");

		dao = new DeckDAO();
		Long id = dao.addDeck(deck);

		return id;
	}


	@Override
	public SetDetailsDTO deckAndCards(Long deckId, String deckSource) {

		if (!("Konami").equalsIgnoreCase(deckSource) && !("User").equalsIgnoreCase(deckSource))
			throw new IllegalArgumentException("Deck Source invalid: " + deckSource);

		Deck deck = new Deck();

		SetsUtils utils = new SetsUtils();

		deck = this.returnDeckWithCards(deckId, deckSource);
		//deck = this.countQtdCardRarityInTheDeck(deck);

		SetDetailsDTO dto = convertDeckToSetDetailsDTO(deck);

		dto = utils.getSetStatistics(dto);

		return dto;
	}

	private SetDetailsDTO convertDeckToSetDetailsDTO(Deck deck) {

		SetDetailsDTO dto = new SetDetailsDTO();
		InsideDeckDTO insideDeck = new InsideDeckDTO();
		SetsUtils setsUtils = new SetsUtils();
		
		BeanUtils.copyProperties(deck, dto);		
		
		List<CardSetDetailsDTO> cardDetailsList = deck.getCards().stream().map(c -> {			
			CardSetDetailsDTO cardDetail = new CardSetDetailsDTO();		
			BeanUtils.copyProperties(c, cardDetail);
			
			cardDetail.setListCardRarity(setsUtils.listCardRarity(cardDetail, deck.getRel_deck_cards()));

			return cardDetail;

		})//.sorted(Comparator.comparingInt(CardSetDetailsDTO::getId))
		  .collect(Collectors.toList());
		
		insideDeck.setCards(cardDetailsList);
		dto.setInsideDecks(List.of(insideDeck));

		return dto;
	}

	@Override
	public Deck returnDeckWithCards(Long deckId, String deckSource) {

		if (deckId == null || deckId == 0)
			throw new IllegalArgumentException("Invalid Deck Id. deckId = " + deckId);

		Deck deck = new Deck();
		List<Card> mainDeck = null;

		if ("konami".equalsIgnoreCase(deckSource))
			deck = this.findById(deckId);

		else if ("user".equalsIgnoreCase(deckSource)) {
			UserDeck deckUser = userDeckRepository.findById(deckId).orElseThrow(() -> new EntityNotFoundException());
			deck = Deck.deckFromDeckUser(deckUser);

		} else
			throw new IllegalArgumentException("Invalid Deck Source: " + deckSource);

		if (deck == null)
			throw new EntityNotFoundException("Deck not found. Id informed: " + deckId);

		String table = ("konami").equalsIgnoreCase(deckSource) ? "tab_rel_deck_cards" : "tab_rel_deckusers_cards";

		mainDeck = this.cardsOfDeck(deckId, table);
		List<RelDeckCards> relDeckCards = this.relDeckCards(deckId, deckSource);

		deck.setCards(mainDeck);
		deck.setRel_deck_cards(relDeckCards);

		return deck;
	}

	@Override
	public Page<DeckSummaryDTO> searchBySetName(String setName) {		

		if (setName.isEmpty() || setName.length() <= 3) 
			throw new IllegalArgumentException("Invalid set name for searching");
		
		setName = setName.trim();

		List<Tuple> setsFoundTuple = this.deckRepository.searchSetsByName(setName);
		
		List<DeckSummaryDTO> summaryList = setsFoundTuple.stream().filter(set -> set.get(0) != null).map(set -> {
							
			Long id = Long.parseLong(String.valueOf(set.get(0)));
			Integer qtd = Integer.parseInt(String.valueOf(set.get(6)));
			
			DeckSummaryDTO summary = new DeckSummaryDTO(
					id,
					set.get(1, String.class),
					set.get(2, String.class),
					set.get(3, String.class),
					set.get(4, Date.class),
					set.get(5, String.class),
					qtd);
					
					return summary;	
						
		}).collect(Collectors.toList());
		
		Page<DeckSummaryDTO> summaryPage = new PageImpl<>(summaryList);

		return summaryPage;

	}


	@Override
	public Deck countQtdCardRarityInTheDeck(Deck deck) {

		deck.setQtd_cards(deck.getRel_deck_cards().stream().count());
		
		deck.setQtd_comuns(deck.getRel_deck_cards().stream()
				.filter(card -> card.getCard_raridade().equals(ECardRarity.COMMON.getCardRarity())).count());
		deck.setQtd_raras(deck.getRel_deck_cards().stream()
				.filter(card -> card.getCard_raridade().equals(ECardRarity.RARE.getCardRarity())).count());
		deck.setQtd_super_raras(deck.getRel_deck_cards().stream()
				.filter(card -> card.getCard_raridade().equals(ECardRarity.SUPER_RARE.getCardRarity())).count());
		deck.setQtd_ultra_raras(deck.getRel_deck_cards().stream()
				.filter(card -> card.getCard_raridade().equals(ECardRarity.ULTRA_RARE.getCardRarity())).count());
		deck.setQtd_secret_raras(deck.getRel_deck_cards().stream()
				.filter(card -> card.getCard_raridade().equals(ECardRarity.SECRET_RARE.getCardRarity())).count());
		deck.setQtd_ultimate_raras(deck.getRel_deck_cards().stream()
				.filter(card -> card.getCard_raridade().equals(ECardRarity.ULTIMATE_RARE.getCardRarity())).count());
		deck.setQtd_gold_raras(deck.getRel_deck_cards().stream()
				.filter(card -> card.getCard_raridade().equals(ECardRarity.GOLD_RARE.getCardRarity())).count());
		deck.setQtd_parallel_raras(deck.getRel_deck_cards().stream()
				.filter(card -> card.getCard_raridade().equals(ECardRarity.PARALLEL_RARE.getCardRarity())).count());
		deck.setQtd_ghost_raras(deck.getRel_deck_cards().stream()
				.filter(card -> card.getCard_raridade().equals(ECardRarity.GHOST_RARE.getCardRarity())).count());

		return deck;
	}

	@Override
	@Transactional(rollbackFor = {Exception.class, ErrorMessage.class})
	public Deck saveKonamiDeck(Deck kDeck) {

			List<Deck> isAlreadyRegistered = deckRepository.findTop30ByNomeContaining(kDeck.getNome());

			if (isAlreadyRegistered == null || isAlreadyRegistered.size() == 0) {
				kDeck = deckRepository.save(kDeck);
			} else 
				throw new ErrorMessage(HttpStatus.NOT_ACCEPTABLE, "Deck is already registered");
			

		return kDeck;
	}

	@Override
	public Page<Deck> findAll(Pageable pageable) {
		Page<Deck> decks = deckRepository.findAll(pageable);

		return decks;
	}

	public Page<Deck> findAllBySetType(Pageable pageable, String setType) {
		Page<Deck> decks = deckRepository.findAllBySetTypeOrderByLancamentoDesc(pageable, setType);

		return decks;
	}

	public List<AutocompleteSetDTO> autocompleteSet() {
		List<Tuple> tuple = deckRepository.autocompleteSet();
		
		List<AutocompleteSetDTO> listSetNames = tuple.stream().map(c -> new AutocompleteSetDTO(
				c.get(0, BigInteger.class),
				c.get(1, String.class)
				)).collect(Collectors.toList());
		
		return listSetNames;
	}

	public List<DeckAndSetsBySetTypeDTO> getAllDecksName() {

		List<Tuple> tuple =	deckRepository.getAllDecksName();
		
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
