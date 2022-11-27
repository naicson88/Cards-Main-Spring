package com.naicson.yugioh.service.deck;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.persistence.EntityManager;
import javax.persistence.EntityNotFoundException;
import javax.persistence.PersistenceContext;
import javax.persistence.Tuple;

import org.apache.commons.lang3.SerializationUtils;
import org.apache.commons.lang3.StringUtils;
import org.json.JSONArray;
import org.json.JSONObject;
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
	
	@Autowired
	SetsUtils utils;

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

		Deck deck = deckRepository.findById(Id)
				.orElseThrow(() -> new EntityNotFoundException("Deck not found."));

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

		Deck deck = this.returnDeckWithCards(deckId, deckSource);

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
		
		if ("konami".equalsIgnoreCase(deckSource))
			 deck = this.findById(deckId);

		else if ("user".equalsIgnoreCase(deckSource)) {
			UserDeck deckUser = userDeckRepository.findById(deckId).orElseThrow(() -> new EntityNotFoundException());
			 deck = Deck.deckFromDeckUser(deckUser);

		} else
			throw new IllegalArgumentException("Invalid Deck Source: " + deckSource);

		String table = ("konami").equalsIgnoreCase(deckSource) ? "tab_rel_deck_cards" : "tab_rel_deckusers_cards";

		List<Card>  mainDeck = this.cardsOfDeck(deckId, table);
		List<RelDeckCards> relDeckCards = this.relDeckCards(deckId, deckSource);

		deck.setCards(mainDeck);
		deck.setRel_deck_cards(relDeckCards);

		return deck;
	}

	@Override
	public Page<DeckSummaryDTO> searchBySetName(String setName) {		

		if (setName.isEmpty() || setName.length() <= 3) 
			throw new IllegalArgumentException("Invalid set name for searching");
		
		List<Tuple> setsFoundTuple = this.deckRepository.searchSetsByName(setName.trim());
		
		List<DeckSummaryDTO> summaryList = setsFoundTuple.stream().filter(set -> set.get(0) != null).map(set -> {
			
			DeckSummaryDTO summary = new DeckSummaryDTO(set);
					
					return summary;	
						
		}).collect(Collectors.toList());
		
		Page<DeckSummaryDTO> summaryPage = new PageImpl<>(summaryList);

		return summaryPage;

	}


	@Override
	public Deck countCardRaritiesOnDeck(Deck deck) {
		
		List<RelDeckCards> listRel =  deck.getRel_deck_cards();	

		deck.setQtd_cards(listRel.size());
		
		Map<String, Long> mapRarities = listRel.stream().collect(Collectors.groupingBy(
				card -> card.getCard_raridade(), Collectors.counting()
				));
		
		deck = setMappedDeckRarities(deck, mapRarities);
		
		return deck;
	}

	private Deck  setMappedDeckRarities(Deck deck, Map<String, Long> mapRarities) {
		
		if(deck == null || mapRarities == null)
			throw new IllegalArgumentException("Invalid information to map rarities");
		
		mapRarities.forEach((key, value) ->{
			ECardRarity rarity = ECardRarity.getRarityByName(key);
			switch (rarity) {		
				case COMMON: deck.setQtd_comuns(value); break;				
				case RARE: deck.setQtd_raras(value); break;			
				case SUPER_RARE: deck.setQtd_super_raras(value); break;			
				case ULTRA_RARE: deck.setQtd_ultra_raras(value); break;		
				case SECRET_RARE: deck.setQtd_secret_raras(value); break;				
				case ULTIMATE_RARE: deck.setQtd_ultimate_raras(value); break;
				case GOLD_RARE: deck.setQtd_gold_raras(value); break;		
				case PARALLEL_RARE: deck.setQtd_parallel_raras(value); break;				
				case GHOST_RARE: deck.setQtd_ghost_raras(value); break;					
				default:
					throw new IllegalArgumentException("Invalid Rarity informed! " + key);
			}
		});
		
		return deck;
	}

	@Override
	@Transactional(rollbackFor = {Exception.class, ErrorMessage.class})
	public Deck saveKonamiDeck(Deck kDeck) {
		
		if(kDeck == null || StringUtils.isBlank(kDeck.getNome()))
			throw new IllegalArgumentException("Invalid Deck for consuting");

		List<Deck> isAlreadyRegistered = findByNome(kDeck.getNome());

		if (isAlreadyRegistered != null && !isAlreadyRegistered.isEmpty())
			throw new ErrorMessage(HttpStatus.NOT_ACCEPTABLE, "Deck is already registered: " + kDeck.getNome());
		
		kDeck = deckRepository.save(kDeck);	

		return kDeck;
	}

	public List<Deck> findByNome(String nome) {
		if(StringUtils.isBlank(nome))
			throw new IllegalArgumentException("Invalid Nome for consuting");
		
		return deckRepository.findByNome(nome);
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
	
	public List<Deck> findAllByIds(List<Long> ids){
		if(ids == null)
			throw new IllegalArgumentException("Invalid IDs for consulting Decks");
		
		List<Deck> decks = deckRepository.findAllById(ids);
		
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

	public List<DeckAndSetsBySetTypeDTO> getAllDecksName(boolean includeCollectionsDeck) {
			
		List<Tuple> tuple =	includeCollectionsDeck == false ?  deckRepository.getAllDecksName() : deckRepository.getAllDecksNameIncludeCollections();
		
		List<DeckAndSetsBySetTypeDTO> listDto = tuple.stream().map(t -> {
			DeckAndSetsBySetTypeDTO dto = new DeckAndSetsBySetTypeDTO(
					Long.parseLong(String.valueOf(t.get(0))),
					String.valueOf(t.get(1))
			);
			return dto;
		}).collect(Collectors.toList());
		
		return listDto;
	}

	public void updateCardsQuantity(String setCodes) {
		if(setCodes == null)
			throw new IllegalArgumentException("Invalid Set Codes Payload");
		
		int counter = 0;
		JSONArray array = new JSONArray(setCodes); 
		logger.info("Starting update Cards Quantity...");
		
		for(Object obj: array) {
			JSONObject setAndQuantity = (JSONObject) obj;
			
			if(!JSONObject.NULL.equals(setAndQuantity.get("setcode")) && !JSONObject.NULL.equals(setAndQuantity.get("quantity"))) {
				String setCode = (String) setAndQuantity.get("setcode");
				String quantity = (String) setAndQuantity.get("quantity");
				Integer qtd = GeneralFunctions.parseValueToInt(quantity);	
							
				if(!setCode.isBlank() && qtd > 1) {
					this.updateSetCodeQuantity(setCode, qtd);
					counter++;
				}
			}			
		}	
		
		logger.info("Ending update Cards Quantity. " + counter + " cards were updated!");		
	}
	
	@Transactional(rollbackFor = {Exception.class, ErrorMessage.class})
	private void updateSetCodeQuantity(String setCode, Integer quantity) {	
		if(quantity == null || setCode == null)
			throw new IllegalAccessError("Invalid information to update SetCode Quantity");
		
		List<RelDeckCards> rel = relDeckCardsRepository.findByCardSetCodeLike(setCode)
				.orElseThrow(() -> new EntityNotFoundException("Cannot find SetCode: " + setCode));
		
		if(rel.size() < quantity) {
			for(int i = rel.size(); i < quantity; i++) {
				RelDeckCards relCopied = SerializationUtils.clone(rel.get(0)); 
				relCopied.setId(null);
				relDeckCardsRepository.save(relCopied);
			}	
		}
	}
	
//	public Map<String, Long> getDeckRaritiesInMap(Deck deck){
//		if(deck == null)
//			throw new IllegalArgumentException("Invalid Deck informed to consult rarities!");
//		
//		for(ECardRarity rarity : ECardRarity.values()) {
//			if(rarity.equals(ECardRarity.COMMON))
//				if(deck.getQtd_comuns() > 0)
//					
//		}
//	}

}
