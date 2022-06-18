package com.naicson.yugioh.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.springframework.stereotype.Service;

import com.naicson.yugioh.data.dto.set.InsideDeckDTO;
import com.naicson.yugioh.data.dto.set.SetDetailsDTO;
import com.naicson.yugioh.util.enums.CardAttributes;
import com.naicson.yugioh.util.enums.CardProperty;
import com.naicson.yugioh.util.enums.GenericTypesCards;

@Service
public class SetsUtils {
	
	public SetDetailsDTO getSetStatistics(SetDetailsDTO detailDTO) {
		
		if(detailDTO == null)
			throw new IllegalArgumentException("Invalid DetailDTO informed");
		
		Map<CardAttributes, Integer> mapAttr = setQuantityByAttributeType(detailDTO.getInsideDeck());
		detailDTO.setStatsQuantityByAttribute(mapAttr);
		
		Map<Integer, Integer> mapCardAttribute = this.setQuantityStars(detailDTO.getInsideDeck());
		detailDTO.setStatsQuantityByLevel(mapCardAttribute);
		
		Map<String, Integer> setQuantityByProperty = this.setQuantityByProperty(detailDTO.getInsideDeck());
		detailDTO.setStatsQuantityByProperty(setQuantityByProperty);
		
		Map<String, Integer> setQuantityByGenericType= this.setQuantityByGenericType(detailDTO.getInsideDeck());
		detailDTO.setStatsQuantityByGenericType(setQuantityByGenericType);	
		
		Map<String, Integer> setQuantityByType = this.setQuantityByType(detailDTO.getInsideDeck());
		detailDTO.setStatsQuantityByType(setQuantityByType);
		
		Map<Integer, Integer> atkMap = this.infoAtk(detailDTO.getInsideDeck());
		detailDTO.setStatsAtk(atkMap);	
		
		Map<Integer, Integer> defMap = this.infoDef(detailDTO.getInsideDeck());
		detailDTO.setStatsDef(defMap);
		
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
	
	private Map<String, Integer> setQuantityByGenericType(List<InsideDeckDTO> insideDeck) {
		
		if(insideDeck == null || insideDeck.isEmpty())
			throw new IllegalArgumentException("The Inside Deck is empty");
		
			Map<String, Integer> mapCardGenericType= new HashMap<>();
			
			insideDeck.stream().forEach(i -> {
			
			for(int j = 0; j < i.getCards().size() ; j++) {
				if(i.getCards().get(j).getGenericType() != null) {
					
					GenericTypesCards generic = GenericTypesCards.valueOf(i.getCards().get(j).getGenericType());
					
					if(!mapCardGenericType.containsKey(generic.name())) 
						mapCardGenericType.put(generic.name(), 1);					
					else 				
						mapCardGenericType.put(generic.name(), mapCardGenericType.get(generic.name()) + 1);	
				}
							
			}
		});
		
		return mapCardGenericType;
	}
	
	private Map<String, Integer> setQuantityByType(List<InsideDeckDTO> insideDeck){
		if(insideDeck == null || insideDeck.isEmpty())
			throw new IllegalArgumentException("The Inside Deck is empty");
		
		Map<String, Integer> mapCardsByType = new HashMap<>();
		
		insideDeck.stream().forEach(i -> {
			for(int j = 0; j < i.getCards().size(); j++) {
				//Get the card type if exists; 
				String type = i.getCards().get(j).getTipo() != null && !i.getCards().get(j).getTipo().getName().isEmpty() ? i.getCards().get(j).getTipo().getName() : "";
				if(!type.isEmpty()) {
					if(!mapCardsByType.containsKey(type))
						mapCardsByType.put(type, 1);
					else
						mapCardsByType.put(type, mapCardsByType.get(type) + 1);
				}
			}
		});
		
		return mapCardsByType;
	}
	
	private Map<Integer, Integer> infoAtk(List<InsideDeckDTO> insideDeck) {
		
		if(insideDeck == null || insideDeck.isEmpty())
			throw new IllegalArgumentException("The Inside Deck is empty");
		
		Map<Integer, Integer> mapAtk = new TreeMap<>();
		
		insideDeck.stream().forEach(i -> {
			for(int j =0; j < i.getCards().size(); j++) {
				Integer atk = i.getCards().get(j).getAtk();
				if(atk != null) {
					
					if(!mapAtk.containsKey(atk))
						mapAtk.put(atk, 1);					
					else 				
						mapAtk.put(atk, mapAtk.get(atk) + 1);	
				}
			}
		});
		
		return mapAtk;
	}
	
	private Map<Integer, Integer> infoDef(List<InsideDeckDTO> insideDeck) {
		
		if(insideDeck == null || insideDeck.isEmpty())
			throw new IllegalArgumentException("The Inside Deck is empty");
		
		Map<Integer, Integer> mapDef = new TreeMap<>();
		
		insideDeck.stream().forEach(i -> {
			for(int j =0; j < i.getCards().size(); j++) {
				Integer def = i.getCards().get(j).getDef();
				
				if(def != null) {				
					if(!mapDef.containsKey(def))
						mapDef.put(def, 1);					
					else 				
						mapDef.put(def, mapDef.get(def) + 1);	
				}
			}
		});
		
		return mapDef;
	}
		
}
