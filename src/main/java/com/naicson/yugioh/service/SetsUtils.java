package com.naicson.yugioh.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.naicson.yugioh.data.dto.set.InsideDeckDTO;
import com.naicson.yugioh.data.dto.set.SetDetailsDTO;
import com.naicson.yugioh.util.enums.CardAttributes;
import com.naicson.yugioh.util.enums.CardProperty;

public class SetsUtils {
	
	public SetDetailsDTO getSetStatistics(SetDetailsDTO detailDTO) {
		
		if(detailDTO == null)
			throw new IllegalArgumentException("Invalid DetailDTO informed");
		
		Map<CardAttributes, Integer> mapAttr = setQuantityByAttributeType(detailDTO.getInsideDeck());
		detailDTO.setStatisticQuantityByAttribute(mapAttr);
		
		Map<Integer, Integer> mapCardAttribute = this.setQuantityStars(detailDTO.getInsideDeck());
		detailDTO.setStatisticQuantityByLevel(mapCardAttribute);
		
		Map<String, Integer> setQuantityByProperty = this.setQuantityByProperty(detailDTO.getInsideDeck());
		detailDTO.setStaticQuantityByProperty(setQuantityByProperty);
		
		return detailDTO;
	}

	private Map<CardAttributes, Integer> setQuantityByAttributeType(List<InsideDeckDTO> insideDeck) {
		
		if(insideDeck == null || insideDeck.isEmpty())
			throw new IllegalArgumentException("The Inside Deck is empty");
		
		Map<CardAttributes, Integer> mapCardAttribute = new HashMap<>();
		
		insideDeck.stream().forEach(i -> {
			
			for(int j = 0; j < i.getCards().size() ; j++) {
				CardAttributes attr = CardAttributes.valueOf(i.getCards().get(j).getAtributo().getName());
				
				if(!mapCardAttribute.containsKey(attr)) 
					mapCardAttribute.put(attr, 1);					
				else 				
					mapCardAttribute.put(attr, mapCardAttribute.get(attr) + 1);				
			}
		});
		
		return mapCardAttribute;
	}
	
	private Map<Integer, Integer> setQuantityStars(List<InsideDeckDTO> insideDeck) {
		
		Map<Integer, Integer> mapQuantityStars = new HashMap<>();
		
		insideDeck.stream().forEach(i -> {
			
			for(int j = 0; j < i.getCards().size() ; j++) {
				if(i.getCards().get(j).getNivel() != null) {
					
					int nivel = i.getCards().get(j).getNivel();
					
					if(!mapQuantityStars.containsKey(nivel))
						mapQuantityStars.put(nivel, 1);					
					else 				
						mapQuantityStars.put(nivel, mapQuantityStars.get(nivel) + 1);	
				}							
			}
		});
		
		return mapQuantityStars;
	}
	
	private Map<String, Integer> setQuantityByProperty(List<InsideDeckDTO> insideDeck) {
			
			if(insideDeck == null || insideDeck.isEmpty())
				throw new IllegalArgumentException("The Inside Deck is empty");
			
			Map<String, Integer> mapCardProperty= new HashMap<>();
			
			insideDeck.stream().forEach(i -> {
				
				for(int j = 0; j < i.getCards().size() ; j++) {
					if(i.getCards().get(j).getPropriedade() != null) {
						
						CardProperty prop = CardProperty.getByValue(i.getCards().get(j).getPropriedade());
						
						if(!mapCardProperty.containsKey(prop.name())) 
							mapCardProperty.put(prop.name(), 1);					
						else 				
							mapCardProperty.put(prop.name(), mapCardProperty.get(prop.name()) + 1);	
					}
								
				}
			});
			
			return mapCardProperty;
		}
	
	
	
	
}
