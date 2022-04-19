
package com.naicson.yugioh.deck;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockitoSession;
import static org.mockito.Mockito.when;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import javax.persistence.EntityNotFoundException;

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
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.ActiveProfiles;

import com.naicson.yugioh.data.dao.DeckDAO;
import com.naicson.yugioh.data.dto.RelUserCardsDTO;
import com.naicson.yugioh.data.dto.RelUserDeckDTO;
import com.naicson.yugioh.data.dto.set.DeckDTO;
import com.naicson.yugioh.entity.Card;
import com.naicson.yugioh.entity.Deck;
import com.naicson.yugioh.entity.RelDeckCards;
import com.naicson.yugioh.entity.sets.DeckUsers;
import com.naicson.yugioh.mocks.DeckUsersMock;
import com.naicson.yugioh.mocks.RelUserDeckDTOMock;
import com.naicson.yugioh.repository.DeckRepository;
import com.naicson.yugioh.repository.RelDeckCardsRepository;
import com.naicson.yugioh.repository.sets.DeckUsersRepository;
import com.naicson.yugioh.service.DeckServiceImpl;
import com.naicson.yugioh.service.UserDetailsImpl;
import com.naicson.yugioh.service.interfaces.DeckDetailService;
import com.naicson.yugioh.util.ValidObjects;
import com.naicson.yugioh.util.exceptions.ErrorMessage;


@ActiveProfiles("test")
@ExtendWith(MockitoExtension.class) // Usado com Junit5 ao inves do RunWith
public class DeckServiceImplTest {
	
	@InjectMocks
	@Spy
	DeckServiceImpl deckService;
	
	@Mock
	DeckRepository deckRepository;
	@Mock
	RelDeckCardsRepository relDeckCardsRepository;
	@Mock
	DeckUsersRepository deckUserRepository;
	@Mock
	DeckDAO dao;
	
//	@BeforeEach
//	public void setUp() {
//		this.deckService = new DeckServiceImpl(deckRepository, relDeckCardsRepository, dao,  deckUserRepository);
//		this.mockAuth();
//		
//	}
	
	@BeforeEach
	public void setup(){
	    MockitoAnnotations.initMocks(this); //without this you will get NPE
	}
	
	private UserDetailsImpl mockAuth() {
		UserDetailsImpl user = ValidObjects.generateValidUser();
		
		Authentication authentication = mock(Authentication.class);
        SecurityContext securityContext = mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);
        when(SecurityContextHolder.getContext().getAuthentication().getPrincipal()).thenReturn(user);
        
        return user;
	}
	
	@Test
	public void findADeckByTheId() throws Exception {
		Deck deck = ValidObjects.generateValidDeck();
		deck.setId(1L);
		
		Mockito.when(deckRepository.findById(deck.getId())).thenReturn(Optional.of(deck));
		
		Deck deckFound = deckService.findById(deck.getId());
		
		assertThat(deckFound).isNotNull();
		assertThat(deck.getId()).isEqualTo(deck.getId());
		assertThat(deck.getNome()).isEqualTo("Deck Teste");
	}
	
	@Test
	public void returnCardsOfaDeck() {
		
		List<RelDeckCards> rels = new ArrayList<>();		
		RelDeckCards rel = ValidObjects.generateRelDeckCards();
		rel.setId(1L);
		rels.add(rel);
		
		Deck deck = ValidObjects.generateValidDeck();
		deck.setId(1L);
		
		Mockito.when(relDeckCardsRepository.findByDeckId(anyLong())).thenReturn(rels);
		
		List<RelDeckCards> relReturned = deckService.relDeckCards(deck.getId(), "konami");
		
		assertThat(relReturned).isNotEmpty();
		assertThat(relReturned).isNotNull();
		assertThat(relReturned.get(0).getId()).isEqualTo(rel.getId());
		assertTrue(relReturned.stream().anyMatch(item -> "YYYY-1111".equals(rel.getCard_set_code())));
		
	}

	@Test
	public void addSetToUsersCollection() throws SQLException, ErrorMessage, Exception {
		this.mockAuth();
		Long originalDeckId = 1L;
		
		Deck deckOrigem = ValidObjects.generateValidDeck();
		deckOrigem.setId(1L);
		
		DeckUsers newDeck = new DeckUsers();
		newDeck.setId(2L);
		
		DeckUsers generatedDeckId = new DeckUsers();
		generatedDeckId.setId(2L);
		
		DeckDetailService deckMock = Mockito.spy(deckService);
		
		Mockito.when(deckRepository.findById(originalDeckId)).thenReturn(Optional.of(deckOrigem));
		Mockito.when(deckUserRepository.save(any(DeckUsers.class))).thenReturn(generatedDeckId);
		Mockito.when(deckService.addCardsToUserDeck(originalDeckId, generatedDeckId.getId())).thenReturn(1);
		//Mockito.when(deckMock.addOrRemoveCardsToUserCollection(originalDeckId, 1, "A")).thenReturn(40);
		Mockito.doReturn(40).when(deckMock).addOrRemoveCardsToUserCollection(originalDeckId, 1, "A");
		
		int qtdCardsAdded = deckMock.addSetToUserCollection(originalDeckId);
		
		assertThat(qtdCardsAdded == 40);
		
	}
	
	@Test
	public void addingCardToUserCollection() throws SQLException, ErrorMessage {		
		this.mockAuth();
		UserDetailsImpl user = ValidObjects.generateValidUser();
		Long originalDeckId = 1L;
		int itemAtualizado = 1;
		
		DeckDetailService deckMock = Mockito.spy(deckService);		
		
		Mockito.when(dao.verifyIfUserAleadyHasTheDeck(originalDeckId, user.getId())).thenReturn(0);
		Mockito.when(dao.addDeckToUserCollection(originalDeckId, user.getId())).thenReturn(itemAtualizado);
		Mockito.doReturn(1).when(deckMock).addOrRemoveCardsToUserCollection(originalDeckId, 1, "A");
		
		int retorno = deckMock.ImanegerCardsToUserCollection(originalDeckId, "A");
		
		assertThat(retorno >= 1);
		
	}
	
	@Test
	public void validUserDontHaveDeckAndFlagAreRemove() throws SQLException, ErrorMessage {
		this.mockAuth();
		Long originalDeckid = 1L;			
		Mockito.when(dao.verifyIfUserAleadyHasTheDeck(originalDeckid,1)).thenReturn(0);	
		int retorno = deckService.ImanegerCardsToUserCollection(originalDeckid, "R");
		
		assertThat(retorno == 0);		
		
	}
	
	@Test
	public void addCardToUserCollectionHavingCardFalse() throws SQLException, ErrorMessage {
	
		Long originalDeckId = 1L;
		UserDetailsImpl user = ValidObjects.generateValidUser();
		
		List<DeckDTO> listDeckDTO = new ArrayList<>();
		
		DeckDTO deckOne = ValidObjects.generateValidDeckDTO(1);		
		DeckDTO deckTwo = ValidObjects.generateValidDeckDTO(2);
		
		listDeckDTO.add(deckOne);
		listDeckDTO.add(deckTwo);
		
		Mockito.when(dao.relationDeckAndCards(originalDeckId)).thenReturn(listDeckDTO);
		Mockito.when(dao.verifyIfUserAleadyHasTheCard(anyLong(), anyString())).thenReturn(false);
		Mockito.when(dao.insertCardToUserCollection(any())).thenReturn(1);
		
		int qtdCardsAddedOrRemoved = deckService.addOrRemoveCardsToUserCollection(originalDeckId, user.getId(), "A");
		
		assertThat(qtdCardsAddedOrRemoved == 2);
		
	}
	
	@Test
	public void addCardToUserCollectionHavingCardTrue() throws SQLException, ErrorMessage {
	
		Long originalDeckId = 1L;
		UserDetailsImpl user = ValidObjects.generateValidUser();
		
		List<DeckDTO> listDeckDTO = new ArrayList<>();
		
		DeckDTO deckOne = ValidObjects.generateValidDeckDTO(1);		
		DeckDTO deckTwo = ValidObjects.generateValidDeckDTO(2);
		
		listDeckDTO.add(deckOne);
		listDeckDTO.add(deckTwo);
		
		Mockito.when(dao.relationDeckAndCards(originalDeckId)).thenReturn(listDeckDTO);
		Mockito.when(dao.verifyIfUserAleadyHasTheCard(anyLong(), anyString())).thenReturn(true);
		Mockito.when(dao.changeQuantityOfEspecifCardUserHas(anyLong(), anyString(), anyString())).thenReturn(1);
		Mockito.lenient().when(dao.insertCardToUserCollection(any(RelUserCardsDTO.class))).thenReturn(1);
		
		int qtdCardsAddedOrRemoved = deckService.addOrRemoveCardsToUserCollection(originalDeckId, user.getId(), "A");
		
		assertThat(qtdCardsAddedOrRemoved == 2);
		
	}
	
	@Test
	public void entityNotFoundWhenTryingAddSetToUserCollection() {
		this.mockAuth();
		Optional<Deck> opt = Optional.ofNullable(null);
		
		when(deckRepository.findById(anyLong())).thenReturn(opt);
		
		EntityNotFoundException exception = Assertions.assertThrows(EntityNotFoundException.class, () -> {
			deckService.addSetToUserCollection(1L);		
		  
		});
		
		String expected = "No Set found with this code.";
		String actual = exception.getMessage();
		
		assertTrue(actual.contains(expected));
	}
	
	@Test
	public void invalidDeckInfoWhenTryingAddSetToUserCollection() {
		this.mockAuth();
		Deck deck = ValidObjects.generateValidDeck();
		
		Optional<Deck> opt = Optional.of(deck);
		
		when(deckRepository.findById(anyLong())).thenReturn(opt);
		
		opt.get().setNome(null);

		IllegalArgumentException exception = Assertions.assertThrows(IllegalArgumentException.class, () -> {
			deckService.addSetToUserCollection(1L);		
		  
		});
		
		String expected = "Invalid Deck name, can't be customized!";
		String actual = exception.getMessage();
		
		assertTrue(actual.contains(expected));
	}
	
	@Test
	public void searchAllDecksUserHaveContainingOneOrMore() {
		this.mockAuth();
		Long[] list = {10L, 30L, 40L};
		List<RelUserDeckDTO> dto = List.of(RelUserDeckDTOMock.generateValidRelUserDeckDTO(), RelUserDeckDTOMock.generateValidRelUserDeckDTO());
		
		Mockito.when(dao.searchForDecksUserHave(anyLong(), anyString())).thenReturn(dto);
		
		List<RelUserDeckDTO> rel = deckService.searchForDecksUserHave(list);
		
		assertNotNull(rel);
		assertEquals(2, rel.size());
		assertThat(dto.get(0).getQuantity() == rel.get(0).getQuantity());
	}
	
	@Test
	public void removeSetFromUsersCollectionsSuccessfully() {
		
		Optional <DeckUsers> opt = Optional.of(DeckUsersMock.generateValidDeckUsers()); 
		
		Mockito.when(deckUserRepository.findById(anyLong())).thenReturn(opt);
		Mockito.when(dao.removeCardsFromUserSet(anyLong())).thenReturn(40);
		doNothing().when(deckUserRepository).deleteById(anyLong());
		
		int qtdRemoved = deckService.removeSetFromUsersCollection(1L);
		
		assertEquals(40, qtdRemoved);
		
	}
	
	@Test
	public void removeSetFromUsersCollectionsError() {
		Long setId = 1L;
		Optional <DeckUsers> opt = Optional.ofNullable(null);
		
		Mockito.when(deckUserRepository.findById(anyLong())).thenReturn(opt);
		
		NoSuchElementException exception = Assertions.assertThrows(NoSuchElementException.class, () -> {
			deckService.removeSetFromUsersCollection(setId);
		  
		});
		
		String expected = "Set not found with this code. Id = " + setId;
		String actual = exception.getMessage();
		
		assertTrue(actual.contains(expected));
		
	}
	
	@Test
	public void returnDeckAndRespectiveCardsSuccessfullyWhenSourceIsUser() {
		
		Long deckId = 1L;
		String deckSource = "user";	
		Optional<DeckUsers> du = Optional.of(DeckUsersMock.generateValidDeckUsers());
		List<Card> mainDeck = List.of(ValidObjects.generateValidCard(1), ValidObjects.generateValidCard(2));
		List<RelDeckCards> rel = List.of(ValidObjects.generateRelDeckCards(),ValidObjects.generateRelDeckCards());
		
		Mockito.when(deckUserRepository.findById(1L)).thenReturn(du);
		Mockito.doReturn(mainDeck).when(deckService).cardsOfDeck(deckId, "tab_rel_deckusers_cards");
		Mockito.doReturn(rel).when(deckService).relDeckCards(deckId, deckSource);
		
		Deck deck = deckService.deckAndCards(deckId, deckSource);
		
		assertNotNull(deck);
		assertEquals(deck.getCards().size(), mainDeck.size());
		assertEquals(deck.getRel_deck_cards().size(), rel.size());
			
	}
	
	@Test
	public void returnDeckAndRespectiveCardsSuccessfullyWhenSourceIsKonami() {
		
		Long deckId = 1L;
		String deckSource = "konami";	
		Deck deck = ValidObjects.generateValidDeck();
		List<Card> mainDeck = List.of(ValidObjects.generateValidCard(1), ValidObjects.generateValidCard(2));
		List<RelDeckCards> rel = List.of(ValidObjects.generateRelDeckCards(),ValidObjects.generateRelDeckCards());
		
		Mockito.doReturn(deck).when(deckService).findById(deckId);
		Mockito.doReturn(mainDeck).when(deckService).cardsOfDeck(deckId, "tab_rel_deck_cards");
		Mockito.doReturn(rel).when(deckService).relDeckCards(deckId, deckSource);
		
		Deck deckReturned = deckService.deckAndCards(deckId, deckSource);
		
		assertNotNull(deckReturned);
		assertEquals(deckReturned.getCards().size(), mainDeck.size());
		assertEquals(deckReturned.getRel_deck_cards().size(), rel.size());
			
	}
	
	@Test
	public void testDeckAndCardWithInvalidSource() {
		
		Long deckId = 1L;
		String deckSource = "Invalid Source";	
		
		IllegalArgumentException exception = Assertions.assertThrows(IllegalArgumentException.class, () -> {
			deckService.deckAndCards(deckId, deckSource);	  
		});
		
		String expected = "Deck Source invalid: " + deckSource;
		String actual = exception.getMessage();
		
		assertTrue(actual.contains(expected));
			
	}
	
	@Test
	public void editUserDeckSuccessfully() {
		UserDetailsImpl user = this.mockAuth();
		user.setId(1);
		Long deckId = 1L;
		
		List<Card> mainDeck = List.of(ValidObjects.generateValidCard(1), ValidObjects.generateValidCard(2));
		List<Card> sideDeck = List.of(ValidObjects.generateValidCard(3), ValidObjects.generateValidCard(4));
		List<Card> extraDeck = Collections.emptyList();
		List<RelDeckCards> rel = List.of(ValidObjects.generateRelDeckCards(),ValidObjects.generateRelDeckCards(),
				ValidObjects.generateRelDeckCards(),ValidObjects.generateRelDeckCards());
		Optional<DeckUsers> du = Optional.of(DeckUsersMock.generateValidDeckUsers());
		
		Mockito.when(deckUserRepository.findById(deckId)).thenReturn(du);
		doReturn(mainDeck).when(deckService).consultMainDeck(deckId);
		doReturn(sideDeck).when(deckService).consultSideDeckCards(deckId, "User");
		doReturn(extraDeck).when(deckService).consultExtraDeckCards(deckId, "User");
		doReturn(rel).when(deckService).relDeckUserCards(deckId);
		
		Deck deck = deckService.editUserDeck(deckId);
		
		assertNotNull(deck);
		assertThat(deck.getCards().contains(mainDeck.get(0)));
		assertThat(deck.getSideDeckCards().contains(sideDeck.get(0)));
		assertTrue(deck.getExtraDeck().isEmpty());
	}
	
	
	

}
