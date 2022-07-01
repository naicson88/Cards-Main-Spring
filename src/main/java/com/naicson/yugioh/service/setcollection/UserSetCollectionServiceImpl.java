package com.naicson.yugioh.service.setcollection;

import java.time.LocalDateTime;

import javax.persistence.EntityNotFoundException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.naicson.yugioh.entity.sets.SetCollection;
import com.naicson.yugioh.entity.sets.UserSetCollection;
import com.naicson.yugioh.repository.UserSetCollectionRepository;
import com.naicson.yugioh.service.deck.RelDeckCardsServiceImpl;
import com.naicson.yugioh.service.deck.UserDeckServiceImpl;
import com.naicson.yugioh.util.exceptions.ErrorMessage;


@Service
public class UserSetCollectionServiceImpl {
	
	@Autowired
	private SetCollectionServiceImpl setService;
	
	@Autowired
	private RelDeckCardsServiceImpl relService;
	
	@Autowired
	private UserSetCollectionRepository userSetRepository;
	
	@Autowired
	private UserDeckServiceImpl userDeckService;
	
	Logger logger = LoggerFactory.getLogger(UserSetCollectionServiceImpl.class);
	
	@Transactional(rollbackFor = Exception.class)
	public UserSetCollection addSetCollectionInUsersCollection(Integer setId) {
			
		SetCollection set = setService.findById(setId);
		
//		set.getDecks().stream().forEach(deck -> {
//			deck.setRel_deck_cards(relService.findRelByDeckId(deck.getId()));
//		});
//		
		UserSetCollection userSet = UserSetCollection.convertToUserSetCollection(set);
		
		userSet.getUserDeck().forEach(deck -> {
			deck.setId(userDeckService.saveUserDeck(deck).getId());}
		);
				
		UserSetCollection setSaved = userSetRepository.save(userSet);
		
		setSaved.getUserDeck().stream().forEach(deck -> {
			userSetRepository.saveSetUserDeckRelation(setSaved.getId(), deck.getId());
		});
		
		if(setSaved == null || setSaved.getId() == null || setSaved.getId() == 0)
			throw new ErrorMessage("It was not possible save User SetCollection");
		
		logger.info("Saving Decks from SetCollection... {}", LocalDateTime.now());
	
		return setSaved;
	}

	public void removeSetCollectionInUsersCollection(Long setId) {
		
		if(setId == null || setId ==  0)
			throw new IllegalArgumentException("Invalid Set ID:" +  setId);
		
		UserSetCollection set = userSetRepository.findById(setId).orElseThrow(() -> new  EntityNotFoundException("Set not found with ID: " + setId));
		
		userSetRepository.deleteSetUserDeckRelation(setId);
		
		set.getUserDeck().stream().forEach(deck -> { 
			userDeckService.removeSetFromUsersCollection(deck.getId());
		});

		userSetRepository.delete(set);
		
		logger.info("Deleted User SetCollection ID: " + setId);
		
	}
}
