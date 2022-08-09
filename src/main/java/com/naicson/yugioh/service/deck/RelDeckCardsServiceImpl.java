package com.naicson.yugioh.service.deck;

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
		
				relSaved = relDeckCardsRepository.saveAll(listRelDeckCards);
				
				if(listRelDeckCards.size() != relSaved.size()) {
					throw new ErrorMessage("It was not possible save all Relations");
				}	
				
			return relSaved;
	}
	
	public List<RelDeckCards> findRelByDeckId(Long deckId){
		List<RelDeckCards> list = relDeckCardsRepository.findByDeckId(deckId);	
		return list;
	}
	
	@Transactional(rollbackFor = Exception.class)
	public void removeRelUserDeckByDeckId(Long deckId) {
		if(deckId == null || deckId == 0)
			throw new IllegalArgumentException("Invalid Deck Id to remove Relation");
		
		relDeckCardsRepository.deleteRelUserDeckByDeckId(deckId);
	}
	
	public void saveAllRelDeckUserCards(List<RelDeckCards> listRel) {
		
		if(listRel != null && listRel.size() > 0) {
			listRel.stream().forEach(rel -> {
				try {
					relDeckCardsRepository.saveRelUserDeckCards(rel.getDeckId(), rel.getCardNumber(), rel.getCard_raridade(), rel.getCard_set_code(),
							rel.getCard_price(), rel.getDt_criacao(), rel.getIsSideDeck(), rel.getCardId(), rel.getIsSpeedDuel(), rel.getQuantity());
					
				}catch (Exception e) {
					logger.error("It was not possible save Rel: {} ", rel.toString());
				}
			});
		}	
	}
	
}
