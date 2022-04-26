package com.naicson.yugioh.service.setcollection;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import com.naicson.yugioh.entity.Deck;
import com.naicson.yugioh.entity.sets.SetCollection;
import com.naicson.yugioh.repository.DeckRepository;
import com.naicson.yugioh.repository.SetCollectionRepository;
import com.naicson.yugioh.util.enums.SetType;

import ch.qos.logback.core.pattern.Converter;

@Component
public class SetsBySetTypeImpl <T> implements ISetsByType<T>{
	
	@Autowired
	DeckRepository deckRepository;
	
	@Autowired
	SetCollectionRepository setRepository;
	
	@Override
	public Page<Deck> findAllSetsByType(Pageable pageable, SetType setType) {
		Page<SetCollection> pageSet = null;
		Page<Deck> pageDeck  = null;
		
		if(setType.equals(SetType.DECK)) 
			return pageDeck = deckRepository.findAll(pageable);
							
		else if(setType.equals(SetType.TIN)) 
			pageSet =  setRepository.findAllBySetType(pageable, setType.toString());	
				
		 pageDeck = this.convertPageSetToPageDeck(pageSet);
		
		 return pageDeck;
	}
	
	private Page<Deck> convertPageSetToPageDeck(Page<SetCollection> pageSet){
		
		Page<Deck> pageDeck = pageSet.map(originalPage -> {
			Deck deck =  this.convertToDeckFormat(originalPage);
			return deck;
		});
		
		return pageDeck;		
	}

	private Deck convertToDeckFormat(SetCollection set) {
		
		Deck deck = new Deck();
		deck.setLancamento(set.getReleaseDate());
		deck.setNome(set.getName());
		deck.setSetType(set.getSetCollectionType().toString());
		deck.setImagem(set.getImgPath());
		deck.setNomePortugues(set.getPortugueseName());
		
		return deck;
	}
}
