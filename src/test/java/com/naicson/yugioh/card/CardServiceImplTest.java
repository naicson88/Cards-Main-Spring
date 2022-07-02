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
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

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
import com.naicson.yugioh.entity.Deck;
import com.naicson.yugioh.entity.stats.CardPriceInformation;
import com.naicson.yugioh.mocks.CardAlternativeNumberMock;
import com.naicson.yugioh.mocks.CardPriceInformationMock;
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
	public void searchCardsUserHave() throws SQLException, ErrorMessage {
		
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
	
	@Test
	public void cardOfUserDetail() throws SQLException, Exception {
		this.mockAuth();
		Card card = ValidObjects.generateValidCard(1);
	
		Tuple mockedTuple = Mockito.mock(Tuple.class); 
		List<Tuple> tupleList = List.of(mockedTuple);
		  
		Mockito.when(cardRepository.findByNumero(anyLong())).thenReturn(card);
		Mockito.when(dao.listCardOfUserDetails(anyLong(), anyLong())).thenReturn(tupleList);
		
		CardOfUserDetailDTO dto = cardService.cardOfUserDetails(1L);
		
		assertEquals(dto.getCardName(), card.getNome());
		assertEquals(dto.getCardNumber(), card.getNumero());
		assertNotNull(dto.getSetsWithThisCard());
		assertNotNull(dto.getRarity());
		
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
		
		Mockito.when(cardRepository.findByArchetype(anyInt())).thenReturn(cardList);
		
		List<CardOfArchetypeDTO> dto = cardService.findCardByArchetype(1);
		
		assertEquals(2, dto.size());
		assertEquals(cardList.get(0).getNome(), dto.get(0).getNome());
		
	}
	
	@Test
	public void errorWhenTryingSearchArchetypeInvalidName() {
		
		Mockito.when(cardRepository.findByArchetype(anyInt())).thenReturn(null);
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
		
		List<Deck> listDeck = List.of(ValidObjects.generateValidDeck(), ValidObjects.generateValidDeck());
		List<CardAlternativeNumber> listAlternativeNumber = List.of(CardAlternativeNumberMock.generateValidCardAlternativeNumber(1L));
		
		Map<String, Integer> mapKonami = Map.of("AAA",1 ,"BBB",2);
		Map<String, Integer> mapUser = Map.of("CCC",1 ,"DDD",2);
		
		Mockito.when(cardRepository.findByNumero(anyLong())).thenReturn(card);		
		Mockito.when(relDeckCardsRepository.findByDeckIdAndCardNumber(any(), any()))
			.thenReturn(List.of(ValidObjects.generateRelDeckCards()));		
		Mockito.when(alternativeRepository.findAllByCardId(anyInt())).thenReturn(listAlternativeNumber);
		Mockito.when(cardPriceService.getAllPricesOfACardById(anyInt())).thenReturn(priceInfo);
		
		doReturn(listDeck).when(cardService).cardDecks(anyLong());
		doReturn(mapUser).when(cardService).findQtdCardUserHaveByCollection(anyInt(), eq("user"));
		doReturn(mapKonami).when(cardService).findQtdCardUserHaveByCollection(anyInt(), eq("konami"));
		
		CardDetailsDTO dto = cardService.findCardByNumberWithDecks(1L);
		
		assertNotNull(dto.getCard());
		assertNotNull(dto.getQtdUserHaveByKonamiCollection().get("AAA"));
		assertNotNull(dto.getQtdUserHaveByUserCollection().get("CCC"));
					
	}
	
	@Test
	public void findQtdCardUserHaveByCollectionKONAMI() {
		this.mockAuth();
		
		String collectionSource = "konami"; 
		
		List<Tuple> tupleList = new ArrayList<>();
		NativeQueryTupleTransformer nativeQueryTupleTransformer = new NativeQueryTupleTransformer();
		
		tupleList.add((Tuple)nativeQueryTupleTransformer
				.transformTuple(new Object[]{new BigInteger("123"), new String("AAA")}, new String[]{"AAA", "BBB"}));
		
		Mockito.when(cardRepository.findQtdUserHaveByKonamiCollection(anyInt(), anyLong())).thenReturn(tupleList);
		
		Map<String, Integer> map = cardService.findQtdCardUserHaveByCollection(1, collectionSource);
		
		assertNotNull(map);
		assertNotNull(map.get("AAA"));
		
	}
	
	@Test
	public void findQtdCardUserHaveByCollectionUSER() {
		this.mockAuth();
		String collectionSource = "user"; 
		
		List<Tuple> tupleList = new ArrayList<>();
		NativeQueryTupleTransformer nativeQueryTupleTransformer = new NativeQueryTupleTransformer();
		
		tupleList.add((Tuple)nativeQueryTupleTransformer
				.transformTuple(new Object[]{new BigInteger("123"), new String("AAA")}, new String[]{"AAA", "BBB"}));
		
		Mockito.when(cardRepository.findQtdUserHaveByUserCollection(anyInt(), anyLong())).thenReturn(tupleList);
		
		Map<String, Integer> map = cardService.findQtdCardUserHaveByCollection(1, collectionSource);
		
		assertNotNull(map);
		assertNotNull(map.get("AAA"));
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
	

	private void mockAuth() {
		UserDetailsImpl user = ValidObjects.generateValidUser();
		
		Authentication authentication = mock(Authentication.class);
        SecurityContext securityContext = mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);
        when(SecurityContextHolder.getContext().getAuthentication().getPrincipal()).thenReturn(user);
	}

	
}
