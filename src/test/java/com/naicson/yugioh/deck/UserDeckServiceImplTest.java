package com.naicson.yugioh.deck;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.AdditionalAnswers;
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
import com.naicson.yugioh.entity.Card;
import com.naicson.yugioh.entity.Deck;
import com.naicson.yugioh.entity.RelDeckCards;
import com.naicson.yugioh.entity.sets.UserDeck;
import com.naicson.yugioh.mocks.RelDeckCardsMock;
import com.naicson.yugioh.mocks.UserDeckMock;
import com.naicson.yugioh.repository.sets.UserDeckRepository;
import com.naicson.yugioh.service.deck.UserDeckServiceImpl;
import com.naicson.yugioh.service.deck.UserRelDeckCardsServiceImpl;
import com.naicson.yugioh.service.user.UserDetailsImpl;
import com.naicson.yugioh.util.ValidObjects;

@ActiveProfiles("test")
@ExtendWith(MockitoExtension.class) // Usado com Junit5 ao inves do RunWith
public class UserDeckServiceImplTest {
	
	@Spy
	@InjectMocks
	UserDeckServiceImpl deckService;
	
	@Mock
	UserDeckRepository userDeckRepository;
	@Mock
	DeckDAO dao;
	@Mock
	UserRelDeckCardsServiceImpl userRelService;
	
	List<RelDeckCards> listRel;
	@BeforeEach
	public void setup(){
	    MockitoAnnotations.initMocks(this); //without this you will get NPE
	    listRel = List.of(RelDeckCardsMock.relDeckCards(), RelDeckCardsMock.relDeckCards());
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
		Optional<UserDeck> du = Optional.of(UserDeckMock.generateValidUserDeck());
		
		Mockito.when(userDeckRepository.findById(deckId)).thenReturn(du);
		
		doReturn(mainDeck).when(dao).consultMainDeck(deckId);
		doReturn(sideDeck).when(dao).consultSideDeckCards(deckId, "User");
		doReturn(extraDeck).when(dao).consultExtraDeckCards(deckId, "User");
		doReturn(rel).when(dao).findRelationByDeckId(deckId);
		
		Deck deck = deckService.editUserDeck(deckId);
		
		assertNotNull(deck);
		assertThat(deck.getCards().contains(mainDeck.get(0)));
		assertThat(deck.getSideDeckCards().contains(sideDeck.get(0)));
		assertTrue(deck.getExtraDeck().isEmpty());
	}
	
	@Test
	public void removeSetFromUsersCollectionsSuccessfully() {
		
		Optional <UserDeck> opt = Optional.of(UserDeckMock.generateValidUserDeck()); 
		Long setId = 1L;
		
		Mockito.when(userDeckRepository.findById(setId)).thenReturn(opt);
		Mockito.when(dao.findRelationByDeckId(setId)).thenReturn(listRel);
		Mockito.when(dao.removeCardsFromUserSet(setId)).thenReturn(40);
		doNothing().when(userDeckRepository).deleteById(anyLong());
		
		int qtdRemoved = deckService.removeSetFromUsersCollection(setId);
		
		assertEquals(40, qtdRemoved);
		
	}
	
	@Test
	public void saveUserdeckWhenItIsNew() {
		mockAuth();
		UserDeck deck = ValidObjects.generateValidUserDeck();
		deck.setRelDeckCards(Collections.emptyList());
		deck.setId(null);
		
		Mockito.when(userDeckRepository.save(any())).then(AdditionalAnswers.returnsFirstArg());
		Mockito.when(userRelService.saveAll(anyList())).thenReturn(null);
		
		UserDeck userDeck = deckService.saveUserDeck(deck);
		
		assertNotNull(userDeck);
		assertEquals(deck.getNome(), userDeck.getNome());
		assertEquals("DECK", userDeck.getSetType());
		
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
}
