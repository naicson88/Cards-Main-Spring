package com.naicson.yugioh.service.setcollection;

import java.time.LocalDateTime;

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
	public String addSetCollectionInUsersCollection(Integer setId) {
			
		SetCollection set = setService.findById(setId);
		
		set.getDecks().stream().forEach(deck -> {
			deck.setRel_deck_cards(relService.findRelByDeckId(deck.getId()));
		});
		
		UserSetCollection userSet = UserSetCollection.convertToUserSetCollection(set);
		
//		userSet.getUserDeck().forEach(deck -> {
//			userDeckService.saveUserDeck(deck);
//		});
		
		UserSetCollection setSaved = userSetRepository.save(userSet);
		
		if(setSaved == null || setSaved.getId() == null || setSaved.getId() == 0)
			throw new ErrorMessage("It was not possible save User SetCollection");
		
		logger.info("Saving Decks from SetCollection... {}", LocalDateTime.now());
	
		return null;
	}
}
