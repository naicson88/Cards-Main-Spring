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
import org.apache.commons.lang3.text.WordUtils;
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

import com.naicson.yugioh.data.bridge.source.SourceTypes;
import com.naicson.yugioh.data.bridge.source.set.RelDeckCardsRelationBySource;
import com.naicson.yugioh.data.dao.DeckDAO;
import com.naicson.yugioh.data.dto.cards.CardSetDetailsDTO;
import com.naicson.yugioh.data.dto.set.AutocompleteSetDTO;
import com.naicson.yugioh.data.dto.set.DeckAndSetsBySetTypeDTO;
import com.naicson.yugioh.data.dto.set.DeckSummaryDTO;
import com.naicson.yugioh.data.dto.set.InsideDeckDTO;
import com.naicson.yugioh.data.dto.set.SetDetailsDTO;
import com.naicson.yugioh.data.dto.set.SetEditDTO;
import com.naicson.yugioh.entity.Card;
import com.naicson.yugioh.entity.Deck;
import com.naicson.yugioh.entity.DeckRarityQuantity;
import com.naicson.yugioh.entity.RelDeckCards;
import com.naicson.yugioh.entity.sets.UserDeck;
import com.naicson.yugioh.repository.DeckRepository;
import com.naicson.yugioh.repository.sets.UserDeckRepository;
import com.naicson.yugioh.service.interfaces.DeckDetailService;
import com.naicson.yugioh.service.setcollection.SetsUtils;
import com.naicson.yugioh.util.GeneralFunctions;
import com.naicson.yugioh.util.enums.ECardRarity;

@Service
public class DeckServiceImpl implements DeckDetailService {

	@PersistenceContext
	EntityManager em;

	@Autowired
	DeckDAO dao;

	@Autowired
	DeckRepository deckRepository;
	
	@Autowired
	RelDeckCardsServiceImpl relService;
	
	@Autowired
	UserDeckRepository userDeckRepository;
	
	@Autowired
	SetsUtils setsUtils;

	Logger logger = LoggerFactory.getLogger(DeckServiceImpl.class);
	
	public DeckServiceImpl(DeckRepository repo) {
		this.deckRepository = repo;
	}
	
	public DeckServiceImpl( ) {}

	@Override
	public Deck findById(Long id) {
		if (id == null || id == 0)
			throw new IllegalArgumentException("Deck Id informed is invalid.");

		return deckRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("Deck not found."));
	}

	@Override
	public List<RelDeckCards> relDeckCards(Long deckId, SourceTypes setSource) {
		if (deckId == null || deckId == 0)
			throw new IllegalArgumentException("Deck Id informed is invalid.");
		
		RelDeckCardsRelationBySource src = setSource.getRelationCards(Map.of(SourceTypes.KONAMI, relService, SourceTypes.USER, dao));
		
		List<RelDeckCards> relation = src.findRelationByDeckId(deckId);
		
		if (relation == null || relation.isEmpty()) 
			return Collections.emptyList();

		return relation;

	}

	@Override
	public List<Card> cardsOfDeck(Long deckId, String table) {

		List<Card> cards = dao.cardsOfDeck(deckId, table);
		
		if(cards == null || cards.isEmpty())
			return Collections.emptyList();
					
		return cards;
	}


	@Override
	@Transactional(rollbackFor = Exception.class)
	public Long addDeck(Deck deck) {

		if (deck == null)
			throw new IllegalArgumentException("Invalid Deck informed");

		return new DeckDAO().addDeck(deck);
	}


	@Override
	public SetDetailsDTO deckAndCards(Long deckId, SourceTypes deckSource, String table) {

		Deck deck = this.returnDeckWithCards(deckId, deckSource, table);

		SetDetailsDTO dto = convertDeckToSetDetailsDTO(deck);
		
		dto.setQuantity(this.countDeckRarityQuantity(dto));
		dto.setQuantityUserHave(quantityUserHaveDeck(deckId));

		return dto;
	}

	public Integer quantityUserHaveDeck(Long deckId) {
		return userDeckRepository.countQuantityOfADeckUserHave(deckId, GeneralFunctions.userLogged().getId());
	}
	
	public SetDetailsDTO convertDeckToSetDetailsDTO(Deck deck) {

		SetDetailsDTO dto = new SetDetailsDTO();
		InsideDeckDTO insideDeck = new InsideDeckDTO();
		
		BeanUtils.copyProperties(deck, dto);		
		
		List<CardSetDetailsDTO> cardDetailsList = deck.getCards().stream().map(c -> {			
			CardSetDetailsDTO cardDetail = new CardSetDetailsDTO();		
			BeanUtils.copyProperties(c, cardDetail);
			
			cardDetail.setListCardRarity(setsUtils.listCardRarity(cardDetail, deck.getRel_deck_cards()));
			cardDetail.setFullCardTypeDescription(fullCardTypeDescription(cardDetail));

			return cardDetail;

		})//.sorted(Comparator.comparingInt(CardSetDetailsDTO::getId))
		  .collect(Collectors.toList());
		
		insideDeck.setCards(cardDetailsList);
		insideDeck.setRelDeckCards(deck.getRel_deck_cards());
		dto.setInsideDecks(List.of(insideDeck));
		dto.setDescription(deck.getDescription());

		return dto;
	}
	
	private String fullCardTypeDescription(CardSetDetailsDTO card) {
		StringBuilder builder = new StringBuilder();
		builder.append("[");
		if(card.getTipo() != null)
			builder.append(card.getTipo().getName()+" ");
		if(card.getCategoria() != null && !card.getCategoria().isBlank()) {
			if(card.getCategoria().equals("Tuner"))
				builder.append("/ Tuner ");
			if(card.getCategoria().contains("Flip Effect"))
				builder.append("/ Flip Effect ");
			if(card.getCategoria().equals("Effect Monster"))
				builder.append("/ Effect ");			
		}	
		builder.append(" / " + WordUtils.capitalizeFully(card.getGenericType()));
		builder.append("]");
		
		return builder.toString();
	}
	
	public Map<String, Long> countDeckRarityQuantity(SetDetailsDTO dto){
		
		List<CardSetDetailsDTO> listCards = new ArrayList<>();
			
		dto.getInsideDecks().forEach(in -> { listCards.addAll(in.getCards()); });
				
		return listCards.stream()
				.collect(Collectors.groupingBy(
				card -> card.getListCardRarity().get(0).getCard_raridade(), Collectors.counting()
				));

	}

	@Override
	public Deck returnDeckWithCards(Long deckId, SourceTypes deckSource, String table) {

		if (deckId == null || deckId == 0)
			throw new IllegalArgumentException("Invalid Deck Id. deckId = " + deckId);
		
		Deck deck = new Deck();
		
		if (deckSource.equals(SourceTypes.KONAMI))
			 deck = this.findById(deckId);

		else if (deckSource.equals(SourceTypes.USER)) {
			UserDeck deckUser = userDeckRepository.findById(deckId).orElseThrow(() -> new EntityNotFoundException("UserDeck not found! Id: "+deckId));
			 deck = Deck.deckFromDeckUser(deckUser);
		}
		
		List<Card>  mainDeck = this.cardsOfDeck(deckId, table);
		List<RelDeckCards> relDeckCards = this.relDeckCards(deckId, deckSource);

		deck.setCards(mainDeck);
		deck.setRel_deck_cards(relDeckCards);

		return deck;
	}
	
	public SetDetailsDTO constructDeckDetails(Long deckId, Deck deck, String table, SourceTypes source, boolean withStats) {
		
		deck.setCards(this.cardsOfDeck(deckId, table));
		deck.setRel_deck_cards(this.relDeckCards(deckId, source));
		
		SetDetailsDTO dto = this.convertDeckToSetDetailsDTO(deck);
		dto.setQuantity(this.countDeckRarityQuantity(dto));
		dto.setQuantityUserHave(this.quantityUserHaveDeck(deckId));
		
		if(withStats) {
			dto = setsUtils.getSetStatistics(dto);
			dto.setInsideDecks(null);
		}
			
		return dto;
	}

	@Override
	public Page<DeckSummaryDTO> searchBySetName(String setName, SourceTypes source) {		

		if (setName == null || setName.length() <= 3) 
			throw new IllegalArgumentException("Invalid set name for searching");
		
		List<Tuple> setsFoundTuple = source.getSetsByName(deckRepository, setName);
		
		if(setsFoundTuple == null)
			 return new PageImpl<>(Collections.emptyList());
		
		List<DeckSummaryDTO> summaryList = setsFoundTuple.stream().filter(set -> set.get(0) != null)
				.map(DeckSummaryDTO::new).collect(Collectors.toList());
		
		return new PageImpl<>(summaryList);

	}


	@Override
	public Deck countCardRaritiesOnDeck(Deck deck) {
		
		List<RelDeckCards> listRel =  deck.getRel_deck_cards();	
		
		Map<String, Long> mapRarities = listRel.stream().collect(Collectors.groupingBy(
				RelDeckCards::getCard_raridade, Collectors.counting()
				));
		
		setMappedDeckRarities(deck, mapRarities);
		
		deck.getQuantity().setTotal(listRel.size());
		
		return deck;
	}
	
	private Deck setMappedDeckRarities(Deck deck, Map<String, Long> mapRarities) {
	
		if(deck == null || mapRarities == null)
			throw new IllegalArgumentException("Invalid information to map rarities");
	
		DeckRarityQuantity quantity = new DeckRarityQuantity();
		
		mapRarities.forEach((key, value) ->{
			ECardRarity rarity = ECardRarity.getRarityByName(key);
			switch (rarity) {		
				case COMMON: quantity.setCommon(value); break;				
				case RARE: quantity.setRare(value); break;			
				case SUPER_RARE: quantity.setSuperRare(value); break;			
				case ULTRA_RARE: quantity.setUltraRare(value); break;		
				case SECRET_RARE: quantity.setSecretRare(value); break;				
				case ULTIMATE_RARE: quantity.setUltimateRare(value); break;
				case GOLD_RARE: quantity.setGoldRare(value); break;		
				case PARALLEL_RARE: quantity.setParallelRare(value); break;				
				case GHOST_RARE: quantity.setGhostRare(value); break;					
				default:
					throw new IllegalArgumentException("Invalid Rarity informed! " + key + value);
			}
		});
		
		deck.setQuantity(quantity);
		return deck;
}

	@Override
	@Transactional(rollbackFor = {Exception.class, RuntimeException.class})
	public Deck saveKonamiDeck(Deck kDeck) {
		
		if(kDeck == null || StringUtils.isBlank(kDeck.getNome()))
			throw new IllegalArgumentException("Invalid Deck for consuting");

		List<Deck> isAlreadyRegistered = findByNome(kDeck.getNome());

		if (isAlreadyRegistered != null && !isAlreadyRegistered.isEmpty())
			throw new IllegalArgumentException("Deck is already registered: " + kDeck.getNome());
		
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
		return deckRepository.findAll(pageable);
	}

	public Page<Deck> findAllBySetType(Pageable pageable, String setType) {
		return deckRepository.findAllBySetTypeOrderByLancamentoDesc(pageable, setType);
	}
	
	public List<Deck> findAllByIds(List<Long> ids){
		if(ids == null)
			throw new IllegalArgumentException("Invalid IDs for consulting Decks");
		
		return deckRepository.findAllById(ids);
	}
	
	public List<AutocompleteSetDTO> autocompleteSet() {
		List<Tuple> tuple = deckRepository.autocompleteSet();
		
		return tuple.stream().map(c -> new AutocompleteSetDTO(
				c.get(0, BigInteger.class),
				c.get(1, String.class)
				)).collect(Collectors.toList());
	}

	public List<DeckAndSetsBySetTypeDTO> getAllDecksName(boolean includeCollectionsDeck) {
			
		List<Tuple> tuple =	includeCollectionsDeck == false ?  deckRepository.getAllDecksName() : deckRepository.getAllDecksNameIncludeCollections();
		
		return tuple.stream().map(t -> {
			return new DeckAndSetsBySetTypeDTO(
					Long.parseLong(String.valueOf(t.get(0))),
					String.valueOf(t.get(1))
			);
		}).collect(Collectors.toList());
		
	}
	
	@Transactional(rollbackFor = Exception.class)
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
		
		logger.info("Ending update Cards Quantity {} cards were updated!", counter);		
	}
	
	@Transactional(rollbackFor = {Exception.class, RuntimeException.class})
	public void updateSetCodeQuantity(String setCode, Integer quantity) {	
		if(quantity == null || setCode == null)
			throw new IllegalAccessError("Invalid information to update SetCode Quantity");
		
		List<RelDeckCards> rel = relService.findByCardSetCodeLike(setCode);
		
		if(rel.size() < quantity) {
			for(int i = rel.size(); i < quantity; i++) {
				RelDeckCards relCopied = SerializationUtils.clone(rel.get(0)); 
				relCopied.setId(null);
				relService.save(relCopied);
			}	
		}
	}

	public SetEditDTO getDeckToEdit(Integer deckId) {
		Deck deck = this.findById(deckId.longValue());
		
		return this.converDeckInSetDetailsToEdit(deck);
	}
	
	private SetEditDTO converDeckInSetDetailsToEdit(Deck deck) {
		SetEditDTO dto = new SetEditDTO();
		
		BeanUtils.copyProperties(deck, dto);		
		
		dto.setRelDeckCards(relService.findRelationByDeckId(deck.getId()));

		return dto;
	}
	
	@Transactional
	public Deck editDeck(SetEditDTO dto) {
		Deck deck = findById(dto.getId());
		
		deck.setNome(dto.getNome().trim());
		deck.setLancamento(dto.getLancamento());
		deck.setImagem(dto.getImagem().trim());
		deck.setSetType(dto.getSetType().trim());
		deck.setSetCode(dto.getSetCode().trim());
		deck.setIsSpeedDuel(dto.getIsSpeedDuel());
		deck.setDescription(dto.getDescription());
		
		return deckRepository.save(deck);
	}
}
