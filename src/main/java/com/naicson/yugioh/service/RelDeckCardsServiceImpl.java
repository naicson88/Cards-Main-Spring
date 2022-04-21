package com.naicson.yugioh.service;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.naicson.yugioh.entity.RelDeckCards;
import com.naicson.yugioh.repository.RelDeckCardsRepository;
import com.naicson.yugioh.service.interfaces.RelDeckCardsDetails;
import com.naicson.yugioh.util.exceptions.ErrorMessage;

@Service
public class RelDeckCardsServiceImpl implements RelDeckCardsDetails {
	
	@Autowired
	RelDeckCardsRepository relDeckCardsRepository;
	
	Logger logger = LoggerFactory.getLogger(RelDeckCardsServiceImpl.class);

	@Override
	@Transactional(rollbackFor = Exception.class)
	public List<RelDeckCards> saveRelDeckCards(List<RelDeckCards> listRelDeckCards) {
		
		List<RelDeckCards> relSaved = new ArrayList<>();
		
		if(listRelDeckCards == null || listRelDeckCards.size() == 0) 
			throw new IllegalArgumentException("Invalid list of Rel Deck Cards");		
		
//				listRelDeckCards.stream().forEach(rel -> {
//					
//					if(relDeckCardsRepository.findByCardSetCode(rel.getCard_set_code()) == null ||
//							relDeckCardsRepository.findByCardSetCode(rel.getCard_set_code()).isEmpty()	) {
//						relSaved.add(relDeckCardsRepository.save(rel));
//						
//					} else {
//						throw new IllegalArgumentException(" Card set code already registered: " + rel.getCard_set_code());
//					}
//
//				});
		
				relSaved = relDeckCardsRepository.saveAll(listRelDeckCards);
				
				if(listRelDeckCards.size() != relSaved.size()) {
					throw new ErrorMessage("It was not possible save all Relations");
				}	
				
			return relSaved;
	}
	
}
