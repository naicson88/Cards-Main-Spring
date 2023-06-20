package com.naicson.yugioh.collection;

import static org.mockito.ArgumentMatchers.any;

import java.nio.charset.Charset;
import java.time.LocalDateTime;
import java.util.Random;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.ActiveProfiles;

import com.naicson.yugioh.entity.sets.SetCollection;
import com.naicson.yugioh.entity.sets.UserDeck;
import com.naicson.yugioh.repository.UserSetCollectionRepository;
import com.naicson.yugioh.service.deck.UserDeckServiceImpl;
import com.naicson.yugioh.service.setcollection.SetCollectionServiceImpl;
import com.naicson.yugioh.service.setcollection.UserSetCollectionServiceImpl;

@ActiveProfiles("test")
@ExtendWith(MockitoExtension.class) // Usado com Junit5 ao inves do RunWith
public class UserSetCollectionTest {
	
	@Spy
	@InjectMocks
	UserSetCollectionServiceImpl userSetService;
	@Mock
	UserSetCollectionRepository setColRepository;
	@Mock
	SetCollectionServiceImpl setService;
	@Mock
	UserDeckServiceImpl userDeckService;
	
	@BeforeEach
	public void setup(){
	    MockitoAnnotations.openMocks(this); //without this you will get NPE
	}
	
	@Test
	public void addSetCollectionInUsersCollection() {
		Integer id = 1;
		UserDeck us = new UserDeck();
		us.setId(1L);
		SetCollection set = Mockito.mock(SetCollection.class);
		
		Mockito.when(setService.findById(id)).thenReturn(set);
		Mockito.when(userDeckService.saveUserDeck(any(UserDeck.class))).thenReturn(us);
		
		userSetService.addSetCollectionInUsersCollection(id);
		
	}
	
}
