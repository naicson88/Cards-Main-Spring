package com.naicson.yugioh.service.deck;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javax.persistence.EntityNotFoundException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.naicson.yugioh.data.bridge.source.set.RelDeckCardsRelationBySource;
import com.naicson.yugioh.entity.Deck;
import com.naicson.yugioh.entity.RelDeckCards;
import com.naicson.yugioh.repository.RelDeckCardsRepository;
import com.naicson.yugioh.service.interfaces.RelDeckCardsDetails;
import com.naicson.yugioh.util.exceptions.ErrorMessage;

@Service
public class RelDeckCardsServiceImpl implements RelDeckCardsDetails, RelDeckCardsRelationBySource {
	
	@Autowired
	RelDeckCardsRepository relDeckCardsRepository;
	
	Logger logger = LoggerFactory.getLogger(RelDeckCardsServiceImpl.class);

	@Override
	@Transactional(rollbackFor = Exception.class)
	public List<RelDeckCards> saveRelDeckCards(List<RelDeckCards> listRelDeckCards) {
		
		if(listRelDeckCards == null || listRelDeckCards.isEmpty()) 
			throw new IllegalArgumentException("Invalid list of Rel Deck Cards");	
		
		List<RelDeckCards> relSaved = new ArrayList<>();
		
		relSaved = relDeckCardsRepository.saveAll(listRelDeckCards);
		
		if(listRelDeckCards.size() != relSaved.size()) 
			throw new ErrorMessage("It was not possible save all Relations");	
		
		return relSaved;
	}
	
	@Override
	public List<RelDeckCards> findRelationByDeckId(Long deckId){
		return relDeckCardsRepository.findByDeckId(deckId);	
	}
	
	@Transactional(rollbackFor = Exception.class)
	public void removeRelUserDeckByDeckId(Long deckId) {
		if(deckId == null || deckId == 0)
			throw new IllegalArgumentException("Invalid Deck Id to remove Relation");
		
		relDeckCardsRepository.deleteRelUserDeckByDeckId(deckId);
	}

	public List<RelDeckCards> findByCardSetCodeLike(String setCode) {
		return relDeckCardsRepository.findByCardSetCodeLike(setCode)
				.orElseThrow(() -> new EntityNotFoundException("Cannot find SetCode: " + setCode));
	}

	public void save(RelDeckCards relCopied) {
		relDeckCardsRepository.save(relCopied);
	}
	
}
