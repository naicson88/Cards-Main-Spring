package com.naicson.yugioh.service.deck;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.naicson.yugioh.controller.UserRelDeckCards;
import com.naicson.yugioh.entity.RelDeckCards;
import com.naicson.yugioh.repository.UserRelDeckCardsRepository;
import com.naicson.yugioh.util.exceptions.ErrorMessage;

@Service
public class UserRelDeckCardsServiceImpl {
	
	@Autowired
	UserRelDeckCardsRepository userRelRepository;
	
	@Autowired
	RelDeckCardsServiceImpl relDeckCardsService;
	

	
	@Transactional(rollbackFor = Exception.class)
	public List<UserRelDeckCards>  addCardsToUserDeck(Long originalDeckId, Long generatedDeckId) {
		
		if (originalDeckId == null && generatedDeckId == null)
			new IllegalArgumentException("Original deck or generated deck is invalid.");
		
		List<RelDeckCards> relDeckCards = relDeckCardsService.findRelByDeckId(originalDeckId);
		
		if(relDeckCards == null || relDeckCards.size() == 0)
			throw new NoSuchElementException("Can't find cards for Konami Deck: " + originalDeckId);
		
		 List<UserRelDeckCards> listUserRel = relDeckCards.stream().map(rel -> {
			UserRelDeckCards userRel = new UserRelDeckCards();
			BeanUtils.copyProperties(rel, userRel);
			
			userRel.setId(null);
			userRel.setDt_criacao(new Date());
			userRel.setDeckId(generatedDeckId);
			
			return userRel;
			
		}).collect(Collectors.toList());

		return userRelRepository.saveAll(listUserRel);	
	}
	
	@Transactional(rollbackFor = { Exception.class, ErrorMessage.class})
	public List<UserRelDeckCards> saveAll(List<UserRelDeckCards> list){
		
		if(list == null || list.size() == 0)
			return Collections.emptyList();
		
		List<UserRelDeckCards> listSaved = userRelRepository.saveAll(list);
		
		if(listSaved == null || listSaved.isEmpty())
			throw new ErrorMessage(HttpStatus.INTERNAL_SERVER_ERROR, "It was not possible save UserRelDeckCards");
		
		return listSaved;
	}
}