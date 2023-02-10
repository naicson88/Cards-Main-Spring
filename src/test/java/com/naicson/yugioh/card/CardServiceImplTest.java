package com.naicson.yugioh.card;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Optional;

import javax.persistence.Tuple;

import org.hibernate.jpa.spi.NativeQueryTupleTransformer;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.ActiveProfiles;

import com.naicson.yugioh.data.dao.CardDAO;
import com.naicson.yugioh.data.dto.RelUserCardsDTO;
import com.naicson.yugioh.data.dto.cards.CardDetailsDTO;
import com.naicson.yugioh.data.dto.cards.CardOfArchetypeDTO;
import com.naicson.yugioh.data.dto.cards.CardOfUserDetailDTO;
import com.naicson.yugioh.data.dto.cards.CardsSearchDTO;
import com.naicson.yugioh.entity.Card;
import com.naicson.yugioh.entity.CardAlternativeNumber;
import com.naicson.yugioh.entity.RelDeckCards;
import com.naicson.yugioh.entity.stats.CardPriceInformation;
import com.naicson.yugioh.mocks.CardAlternativeNumberMock;
import com.naicson.yugioh.mocks.CardPriceInformationMock;
import com.naicson.yugioh.mocks.RelDeckCardsMock;
import com.naicson.yugioh.repository.CardAlternativeNumberRepository;
import com.naicson.yugioh.repository.CardRepository;
import com.naicson.yugioh.repository.DeckRepository;
import com.naicson.yugioh.repository.RelDeckCardsRepository;
import com.naicson.yugioh.service.card.CardPriceInformationServiceImpl;
import com.naicson.yugioh.service.card.CardServiceImpl;
import com.naicson.yugioh.service.card.CardViewsInformationServiceImpl;
import com.naicson.yugioh.service.user.UserDetailsImpl;
import com.naicson.yugioh.util.ValidObjects;
import com.naicson.yugioh.util.exceptions.ErrorMessage;
import com.naicson.yugioh.util.search.CardSpecification;
import com.naicson.yugioh.util.search.SearchCriteria;
import com.naicson.yugioh.util.search.SearchOperation;

@ActiveProfiles("test")
@ExtendWith(MockitoExtension.class) // Usado com Junit5 ao inves do RunWith
public class CardServiceImplTest {
	
	@InjectMocks
	@Spy
	CardServiceImpl cardService;
	
	@Mock
	private CardRepository cardRepository;	
	@Mock
	private CardDAO dao;
	@Mock
	private RelDeckCardsRepository relDeckCardsRepository;
	@Mock
	private DeckRepository deckRepository;	
	@Mock
	CardAlternativeNumberRepository alternativeRepository;
	@Mock
	Pageable pageable;
	@Mock
	CardViewsInformationServiceImpl viewsService;
	@Mock
	CardPriceInformationServiceImpl cardPriceService;
	
//	@BeforeEach
//	public void setUp() {
//		this.impl = new CardServiceImpl(cardRepository, dao, relDeckCardsRepository, deckRepository);
//		this.mockAuth();
//	}
	
	@BeforeEach
	public void setup(){
	    MockitoAnnotations.initMocks(this); //without this you will get NPE
	}
	
	@Test
	public void searchCardsUserHave() {
		
		int[] cardsNumbers = new int[] {111,222};
		
		RelUserCardsDTO rel1 = ValidObjects.generateRelUserCardsDTO(111); 
		RelUserCardsDTO rel2 = ValidObjects.generateRelUserCardsDTO(222);		
		List<RelUserCardsDTO> list = List.of(rel1, rel2);
		this.mockAuth();
		
		Mockito.when(dao.searchForCardsUserHave(anyLong(), anyString())).thenReturn(list);
		
		List<RelUserCardsDTO> listReturned = cardService.searchForCardsUserHave(cardsNumbers);
		
		assertEquals(2, listReturned.size());
		assertThat(listReturned.get(0).getCardNumero() == 111);
		assertThat(listReturned.get(1).getCardNumero() == 222);
		
	}	
	
//	@Test
//	public void cardOfUserDetail() {
//		this.mockAuth();
//	//	Card card = ValidObjects.generateValidCard(1);
//		
//		List<Tuple> tupleList = createTupleOfCardsOfUserSetsDTO();
//		  
//		//Mockito.when(cardRepository.findById(anyInt())).thenReturn(Optional.of(card));
//		Mockito.when(dao.listCardOfUserDetails(anyInt(), anyLong())).thenReturn(tupleList);
//		
//		CardOfUserDetailDTO dto = cardService.cardOfUserDetails(1);
//		
//		assertEquals("Set Name", dto.getSetsWithThisCard().get(0).getSetName());
//		assertEquals( 2.0, dto.getSetsWithThisCard().get(0).getPrice());
//		assertNotNull(dto.getSetsWithThisCard());
//		assertNotNull(dto.getRarity());		
//	}
	
	private List<Tuple> createTupleOfCardsOfUserSetsDTO() {
		
		List<Tuple> tupleList = new ArrayList<>();
		NativeQueryTupleTransformer nativeQueryTupleTransformer = new NativeQueryTupleTransformer();
		
		tupleList.add((Tuple)nativeQueryTupleTransformer
				.transformTuple(
						   new Object[]{new  String("Set Name"), new String("Set Code"), new String("Rarity"), 2.0,
								   new  String("12"), new String("11"), new String("Set Tipe")},
						new String[]{"AAA", "BBB","CCC", "DDD","FFF", "RRR", "YUYU"}));
		
		return tupleList;
	}
	
	@Test
	public void getByGenericType() {
		List<Card> cardList = List.of(ValidObjects.generateValidCard(1), ValidObjects.generateValidCard(2));	
		Page<Card> pageCard = new PageImpl<Card>(cardList);
		
		Mockito.when(cardRepository.getByGenericType(pageable,"teste", 1)).thenReturn(pageCard);
		
		List<CardsSearchDTO> dto = cardService.getByGenericType(pageable,"teste", 1);
		
		assertNotNull(dto);
		assertFalse(dto.isEmpty());
	}
	
	@Test
	public void cardSearchSuccess() {
		
		SearchCriteria criteria = Mockito.mock(SearchCriteria.class);
		List<SearchCriteria> criteriaList = List.of(criteria);
		List<Card> cardList = List.of(ValidObjects.generateValidCard(1), ValidObjects.generateValidCard(2));
		Page<Card> pageCard = new PageImpl<Card>(cardList);
				
		Mockito.doReturn(pageCard).when(cardService).findAll(any(), any()); 
		//Mockito.when(impl.findAll(any(), any())).thenReturn(pageCard);
		List<CardsSearchDTO> dto = cardService.cardSearch(criteriaList, null, pageable);
		
		assertThat(!dto.isEmpty());
		assertEquals(dto.get(0).getNome(), cardList.get(0).getNome());
	}
	
	@Test
	public void cardSearchByNameUserCollection() {
		
		List<Card> cardList = List.of(ValidObjects.generateValidCard(1), ValidObjects.generateValidCard(2));	
		Page<Card> pageCard = new PageImpl<Card>(cardList);
		
		UserDetailsImpl user = ValidObjects.generateValidUser();
		user.setId(1);
		
		Mockito.when(cardRepository.cardSearchByNameUserCollection("teste", user.getId(), pageable )).thenReturn(pageCard);
		
		List<CardsSearchDTO> dto = cardService.cardSearchByNameUserCollection("teste", pageable);
		
		assertNotNull(dto);
		assertThat(!dto.isEmpty());
		assertThat(dto.size() > 0);
		
	}
	
	@Test
	public void findWithSuccessArchetypeByItsName() {
		List<Card> cardList = List.of(ValidObjects.generateValidCard(1), ValidObjects.generateValidCard(2));
		
		Mockito.when(cardRepository.findByArchetype(anyInt())).thenReturn(Optional.of(cardList));
		
		List<CardOfArchetypeDTO> dto = cardService.findCardByArchetype(1);
		
		assertEquals(2, dto.size());
		assertEquals(cardList.get(0).getNome(), dto.get(0).getNome());
		
	}
	
	@Test
	public void errorWhenTryingSearchArchetypeInvalidName() {
		
		Mockito.when(cardRepository.findByArchetype(anyInt())).thenReturn(Optional.empty());
		Integer archId = 1;
		
		NoSuchElementException exception = Assertions.assertThrows(NoSuchElementException.class, () -> {
			cardService.findCardByArchetype(archId);			
		  
		});
		
		String expected = "It was not possible found cards of Archetype: " + archId;
		String actual = exception.getMessage();
		
		assertTrue(actual.contains(expected));
	}
	
	@Test
	public void findCardByItsNumberAndRespectiveDecks() {
		
		Card card = ValidObjects.generateValidCard(1);
		
		List<CardPriceInformation> priceInfo = List.of(CardPriceInformationMock.generateValidCardPriceInfo(1L));
		
		List<CardAlternativeNumber> listAlternativeNumber = List.of(CardAlternativeNumberMock.generateValidCardAlternativeNumber(1L));
		
		//Map<String, Integer> mapKonami = Map.of("AAA",1 ,"BBB",2);
		Map<String, Integer> mapUser = Map.of("CCC",1 ,"DDD",2);
		
		Mockito.when(cardRepository.findByNumero(anyLong())).thenReturn(Optional.of(card));		
	
		Mockito.when(alternativeRepository.findAllByCardId(anyInt())).thenReturn(listAlternativeNumber);
		Mockito.when(cardPriceService.getAllPricesOfACardById(anyInt())).thenReturn(priceInfo);
		
		doReturn(mapUser).when(cardService).findQtdCardUserHaveByCollection(anyInt());
		//doReturn(mapKonami).when(cardService).findQtdCardUserHaveByCollection(anyInt(), eq("konami"));
		
		CardDetailsDTO dto = cardService.findCardByNumberWithDecks(1L);
		
		assertNotNull(dto.getCard());
		//assertNotNull(dto.getQtdUserHaveByKonamiCollection().get("AAA"));
		assertNotNull(dto.getQtdUserHaveByUserCollection().get("CCC"));
					
	}
	
	
	@Test
	public void findQtdCardUserHaveByCollectionUSER() {
		this.mockAuth();
		
		List<Tuple> tupleList = new ArrayList<>();
		NativeQueryTupleTransformer nativeQueryTupleTransformer = new NativeQueryTupleTransformer();
		
		tupleList.add((Tuple)nativeQueryTupleTransformer
				.transformTuple(new Object[]{new String("123"), new String("AAA")}, new String[]{"AAA", "BBB"}));
		
		Mockito.when(cardRepository.findQtdUserHaveByUserCollection(anyInt(), anyLong())).thenReturn(tupleList);
		
		Map<String, List<String>> map = cardService.findQtdCardUserHaveByCollection(1);
		
		assertNotNull(map);
		//assertNotNull(map.get("AAA"));
	}
	
	@Test
	public void searchCardDetailedWithCriteriasSuccess() {
		
		SearchCriteria c1 = new SearchCriteria();
		c1.setKey("AAA");
		c1.setOperation(SearchOperation.EQUAL);
		c1.setOrPredicate(false);	
		List<SearchCriteria> list = List.of(c1);
		
		List<Card> cardList = List.of(ValidObjects.generateValidCard(1), ValidObjects.generateValidCard(2));	
		Page<Card> pageCard = new PageImpl<Card>(cardList);
		
		doReturn(pageCard).when(cardService).findAll(any(CardSpecification.class), any(Pageable.class));
		
		Page<Card> page = cardService.searchCardDetailed(list, null, pageable);
		
		assertNotNull(page);
		assertEquals(page.getSize(), 2);
		assertEquals(page.getContent().get(0).getNome(), cardList.get(0).getNome());	
	}
	
	@Test
	public void searchCardDetailedWithCriteriasError() {	
		
		IllegalArgumentException exception = Assertions.assertThrows(IllegalArgumentException.class, () -> {
			cardService.searchCardDetailed(null, null, pageable);		
		  
		});
		
		String expected = "Criterias is invalid";
		String actual = exception.getMessage();
		
		assertTrue(actual.contains(expected));
	
	}
	
	@Test
	public void findCardsNotRegisteredWhenThereIsCardNotRegistered() {
		
		List<Long> allNumbers = List.of(1000L,5000L, 3000L, 2000L, 4000L,9000L,8000L);
		List<Long> registered = List.of(1000L,5000L, 3000L, 2000L);
		
		Mockito.when(cardRepository.findAllCardsByListOfCardNumbers(anyList())).thenReturn(registered);
		
		List<Long> notRegistered = cardService.findCardsNotRegistered(allNumbers);
		
		assertNotNull(notRegistered);
		assertTrue(notRegistered.size() == 3);
		assertTrue(notRegistered.containsAll(List.of(4000L,9000L,8000L)));
			
	}
	
	@Test
	public void findCardsNotRegisteredWhenThereIsNOCardNotRegistered() {
		List<Long> allNumbers = List.of(1000L,5000L, 3000L, 2000L, 4000L,9000L,8000L);
		
		Mockito.when(cardRepository.findAllCardsByListOfCardNumbers(anyList())).thenReturn(null);
		
		List<Long> cardsNotRegistered = cardService.findCardsNotRegistered(allNumbers);
				
		assertNotNull(cardsNotRegistered);
		assertTrue(cardsNotRegistered.size() == allNumbers.size());
		assertTrue(cardsNotRegistered.containsAll(List.of(1000L,5000L, 3000L, 2000L, 4000L,9000L,8000L)));
		
	}
	
	@Test
	public void returnACardByItsName() {
		String cardName = "teste";
		Card card = ValidObjects.generateValidCard(1);
		
		Mockito.when(cardRepository.findByNome(cardName)).thenReturn(card);
		
		Card cardFound = cardService.findByCardNome(cardName);
		
		assertNotNull(cardFound);
		assertEquals(card.getId(), cardFound.getId());
		
	}
	
	@Test
	public void findAllRelDeckCardsByCardNumber() {
		Integer cardId = 1;
		List<RelDeckCards> rel = List.of(RelDeckCardsMock.relDeckCards(), RelDeckCardsMock.relDeckCards());
		Mockito.when(relDeckCardsRepository.findByCardId(cardId)).thenReturn(rel);
		
		List<RelDeckCards> listFound = cardService.findAllRelDeckCardsByCardNumber(cardId);
		assertNotNull(listFound);
		assertThat(listFound).isNotEmpty();
		assertEquals(1, rel.get(0).getId());
		
	}
	
	@Test
	public void returnListRandomCardsDetailed() {
		List<Card> cards = List.of(ValidObjects.generateValidCard(1), ValidObjects.generateValidCard(2));
		Mockito.when(cardRepository.findRandomCards()).thenReturn(cards);
		
		List<Card> cardsFound = cardService.randomCardsDetailed();
		
		assertNotNull(cardsFound);
		assertThat(cardsFound).isNotEmpty();
		assertEquals(1, cards.get(0).getId());
	}
	
	@Test
	public void returnErrorListRandomCardsDetailed() {
		List<Card> cards = null;
		Mockito.when(cardRepository.findRandomCards()).thenReturn(cards);
		
		ErrorMessage exception = Assertions.assertThrows(ErrorMessage.class, () -> {
			cardService.randomCardsDetailed();			  
		});
		
		String expected = "Can't find Random cards";
		String actual = exception.getMsg();
		
		assertTrue(actual.contains(expected));
	}
	
	

	private void mockAuth() {
		UserDetailsImpl user = ValidObjects.generateValidUser();
		
		Authentication authentication = mock(Authentication.class);
        SecurityContext securityContext = mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);
        when(SecurityContextHolder.getContext().getAuthentication().getPrincipal()).thenReturn(user);
	}
	
	

	
}
