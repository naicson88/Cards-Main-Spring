package com.naicson.yugioh.service.setcollection;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import com.naicson.yugioh.data.dto.GenericTypeDTO;
import com.naicson.yugioh.data.dto.cards.CardRarityDTO;
import com.naicson.yugioh.data.dto.cards.CardSetDetailsDTO;
import com.naicson.yugioh.data.dto.set.InsideDeckDTO;
import com.naicson.yugioh.data.dto.set.SetDetailsDTO;
import com.naicson.yugioh.data.dto.set.SetStatsDTO;
import com.naicson.yugioh.entity.Atributo;
import com.naicson.yugioh.entity.RelDeckCards;
import com.naicson.yugioh.entity.TipoCard;
import com.naicson.yugioh.util.enums.CardProperty;
import com.naicson.yugioh.util.enums.GenericTypesCards;

@Service
public class SetsUtils {
	
	Logger logger = LoggerFactory.getLogger(SetsUtils.class);
	
	public SetDetailsDTO getSetStatistics(SetDetailsDTO detailDTO) {
		
		if(detailDTO == null)
			throw new IllegalArgumentException("Invalid DetailDTO informed");
		
		SetStatsDTO statsDTO = new SetStatsDTO();
		
		statsDTO.setStatsQuantityByLevel(this.setQuantityStars(detailDTO.getInsideDecks()));

		statsDTO.setStatsQuantityByProperty(this.setQuantityByProperty(detailDTO.getInsideDecks()));
		
		statsDTO.setGenericTypes(this.setQuantityByGenericType(detailDTO.getInsideDecks()));
		
		statsDTO.setTipoCard(this.setQuantityByCardType(detailDTO.getInsideDecks()));
//		statsDTO.setStatsQuantityByType(this.setQuantityByType(detailDTO.getInsideDecks()));
		
		statsDTO.setStatsAtk(this.infoAtk(detailDTO.getInsideDecks()));	
		
		statsDTO.setStatsDef(this.infoDef(detailDTO.getInsideDecks()));
		
		statsDTO.setAtributos(setNewQuantityByAttribute(detailDTO.getInsideDecks()));
		
		detailDTO.setSetStats(statsDTO);
		 
		return detailDTO;
	}
	
	private List<Atributo> setNewQuantityByAttribute(List<InsideDeckDTO> insideDeck){
		
		List<Atributo> attrListAux = insideDeck.stream().flatMap(card -> card.getCards().stream())
				.map(CardSetDetailsDTO::getAtributo)
				.filter(at -> !at.getName().equals("SPELL") && !at.getName().equals("TRAP"))
				.collect(Collectors.toList());
		
		Map<Long, Long> quantityByAttributeId = attrListAux.stream().collect(Collectors.groupingBy(Atributo::getId, Collectors.counting()));
		
		List<Atributo> finalAttrList = attrListAux.stream().distinct().collect(Collectors.toList());
		
		Long totalAttributes = quantityByAttributeId.values().stream().mapToLong(Long::longValue).sum();
		
		for (Atributo attr : finalAttrList) {
			attr.setQuantity(quantityByAttributeId.get(attr.getId()).intValue());
			attr.setPercentage(this.calculatePercentage(attr.getQuantity(), totalAttributes.intValue()));
		}
		
		return finalAttrList;
	}
	
	private List<TipoCard> setQuantityByCardType(List<InsideDeckDTO> insideDeck){	
		
		List<CardSetDetailsDTO> listDetails = insideDeck.stream().flatMap(card -> card.getCards().stream())
				.filter(c -> c.getTipo() != null && c.getTipo().getId() > 1).collect(Collectors.toList());
		
		Map<Long, Long> quantityByTipo = listDetails.stream()
				.map(CardSetDetailsDTO::getTipo)
				.collect(Collectors.groupingBy(TipoCard::getId, Collectors.counting()));
		
		List<TipoCard> tipoList =  listDetails.stream().map(CardSetDetailsDTO::getTipo).distinct().collect(Collectors.toList());
		
		for (TipoCard tipo : tipoList) {
			tipo.setQuantity(quantityByTipo.get(tipo.getId()).intValue());
		}
		
		return tipoList;
	
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
	
	
	private List<GenericTypeDTO> setQuantityByGenericType(List<InsideDeckDTO> insideDeck){
		
		Map<String, Long> mapCardsByType = insideDeck.stream()
				.flatMap(card -> card.getCards().stream())
				.collect(Collectors.groupingBy(CardSetDetailsDTO::getGenericType, Collectors.counting()));
		
		List<GenericTypeDTO> listTypes = new ArrayList<>();
		
		for(Map.Entry<String, Long> entry : mapCardsByType.entrySet()) {			
			GenericTypesCards generic = GenericTypesCards.valueOf(entry.getKey());		
			listTypes.add(new GenericTypeDTO(entry.getKey(), generic.getPath(), entry.getValue().intValue()));
		}
		
		return listTypes;
		
	}
	
//	
//	private Map<String, Integer> setQuantityByType(List<InsideDeckDTO> insideDeck){
//		if(insideDeck == null || insideDeck.isEmpty())
//			throw new IllegalArgumentException("The Inside Deck is empty");
//		
//		Map<String, Integer> mapCardsByType = new HashMap<>();
//		
//		insideDeck.stream().forEach(i -> {
//			for(int j = 0; j < i.getCards().size(); j++) {
//				//Get the card type if exists; 
//				String type = i.getCards().get(j).getTipo() != null && !i.getCards().get(j).getTipo().getName().isEmpty() ? i.getCards().get(j).getTipo().getName() : "";
//				if(!type.isEmpty()) {
//					if(!mapCardsByType.containsKey(type))
//						mapCardsByType.put(type, 1);
//					else
//						mapCardsByType.put(type, mapCardsByType.get(type) + 1);
//				}
//			}
//		});
//		
//		return mapCardsByType;
//	}
	
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
	
	public List<CardRarityDTO> listCardRarity(CardSetDetailsDTO cardDetail, List<RelDeckCards> listRelDeckCards ){
		List<CardRarityDTO> listRarity = new ArrayList<>();	
		
		listRelDeckCards.stream()
				.filter(rel -> rel.getCardId().equals(cardDetail.getId()))
				.forEach(rel -> {
						CardRarityDTO rarityDTO = new CardRarityDTO();
						BeanUtils.copyProperties(rel, rarityDTO);
						listRarity.add(rarityDTO);
						
			});
			return listRarity;
	}
	
	private double calculatePercentage(int value, int totalValue) {	
		double raturnedValue = 0.0;
		try {
			
			raturnedValue = value * 100  / totalValue;
			
		} catch (Exception e) {
			logger.error("Error when trying to calculate percentage: {} e {}", value, totalValue);
		}
		
		return raturnedValue;
	}
		
}
