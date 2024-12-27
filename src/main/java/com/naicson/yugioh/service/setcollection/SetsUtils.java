package com.naicson.yugioh.service.setcollection;

import com.naicson.yugioh.data.dto.GenericTypeDTO;
import com.naicson.yugioh.data.dto.cards.CardRarityDTO;
import com.naicson.yugioh.data.dto.cards.CardSetDetailsDTO;
import com.naicson.yugioh.data.dto.set.SetDetailsDTO;
import com.naicson.yugioh.data.dto.set.SetStatsDTO;
import com.naicson.yugioh.entity.Atributo;
import com.naicson.yugioh.entity.RelDeckCards;
import com.naicson.yugioh.entity.TipoCard;
import com.naicson.yugioh.util.enums.CardProperty;
import com.naicson.yugioh.util.enums.GenericTypesCards;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class SetsUtils {
	
	Logger logger = LoggerFactory.getLogger(SetsUtils.class);
	
	public SetDetailsDTO getSetStatistics(SetDetailsDTO detailDTO) {
		
		if(detailDTO == null)
			throw new IllegalArgumentException("Invalid DetailDTO informed");
		
		SetStatsDTO statsDTO = new SetStatsDTO();
		
		List<CardSetDetailsDTO> listDetails = detailDTO.getInsideDecks().stream().flatMap(card -> card.getCards().stream()).collect(Collectors.toList());
		
		statsDTO.setListLevel(this.infoLevel(listDetails));
		
		statsDTO.setListProperties(this.infoProperties(listDetails));
		
		statsDTO.setGenericTypes(this.setQuantityByGenericType(listDetails));
		
		statsDTO.setTipoCard(this.setQuantityByCardType(listDetails));
		
		statsDTO.setListAtk(this.infoAtk(listDetails));
		
		statsDTO.setListDef(this.infoDef(listDetails));
		
		statsDTO.setAtributos(setNewQuantityByAttribute(listDetails));
		
		statsDTO.setListMostValuable(this.listMostValuable(detailDTO, listDetails));
		
		detailDTO.setSetStats(statsDTO);
		 
		return detailDTO;
	}
	
	private List<Atributo> setNewQuantityByAttribute(List<CardSetDetailsDTO> listDetails){
		
		List<Atributo> attrListAux = listDetails.stream()
				.map(CardSetDetailsDTO::getAtributo)
				.filter(at -> !at.getName().equals(GenericTypesCards.SPELL.toString()) && !at.getName().equals(GenericTypesCards.TRAP.toString()))
				.toList();
		
		Map<Long, Long> quantityByAttributeId = attrListAux.stream().collect(Collectors.groupingBy(Atributo::getId, Collectors.counting()));
		
		List<Atributo> finalAttrList = attrListAux.stream().distinct().collect(Collectors.toList());
		
		Long totalAttributes = quantityByAttributeId.values().stream().mapToLong(Long::longValue).sum();
		
		for (Atributo attr : finalAttrList) {
			attr.setQuantity(quantityByAttributeId.get(attr.getId()).intValue());
			attr.setPercentage(this.calculatePercentage(attr.getQuantity(), totalAttributes.intValue()));
		}
		
		return finalAttrList;
	}
	
	private List<TipoCard> setQuantityByCardType(List<CardSetDetailsDTO> list){	
		
		List<CardSetDetailsDTO> listDetails = list.stream().filter(c -> c.getTipo() != null && c.getTipo().getId() > 1).toList();
		
		Map<Long, Long> quantityByTipo = listDetails.stream()
				.map(CardSetDetailsDTO::getTipo)
				.collect(Collectors.groupingBy(TipoCard::getId, Collectors.counting()));
		
		return  listDetails.stream().map(CardSetDetailsDTO::getTipo)
				.distinct()
				.map(tipo -> {
					int quantity = quantityByTipo.get(tipo.getId()).intValue();
					return new TipoCard(tipo.getId(), tipo.getName(), tipo.getTipoCardImgPath(), quantity);
				})
				.sorted((v1, v2) -> Long.compare(v2.getQuantity(), v1.getQuantity()))
				.toList();

//		for (TipoCard tipo : tipoList) {
//			tipo.setQuantity(quantityByTipo.get(tipo.getId()).intValue());
//		}
//
//		return tipoList.stream().sorted((v1, v2) -> Long.compare(v2.getQuantity(), v1.getQuantity())).collect(Collectors.toList());

	}
	
	private List<GenericTypeDTO> setQuantityByGenericType(List<CardSetDetailsDTO> listDetails){
		
		Map<String, Long> mapCardsByType = listDetails.stream()
				.collect(Collectors.groupingBy(CardSetDetailsDTO::getGenericType, Collectors.counting()));
		
		List<GenericTypeDTO> listTypes = new ArrayList<>();
		
		for(Map.Entry<String, Long> entry : mapCardsByType.entrySet()) {			
			GenericTypesCards generic = GenericTypesCards.valueOf(entry.getKey());		
			listTypes.add(new GenericTypeDTO(entry.getKey(), generic.getPath(), entry.getValue().intValue()));
		}
		
		return listTypes;
		
	}

	private List<Map<String, Integer>> infoAtk(List<CardSetDetailsDTO> listDetails) {
		
		Map<Integer, Long> mapAtk = listDetails.stream()
				.filter(c -> c.getAtk() != null)
				.collect(Collectors.groupingBy(CardSetDetailsDTO::getAtk, Collectors.counting()));
		
		mapAtk = new TreeMap<>(mapAtk);
		
		return mapAttackAndDefense(mapAtk);		
	}
	
	private List<Map<String, Object>> infoProperties(List<CardSetDetailsDTO> listDetails){
		Map<String, Long> mapProperties = listDetails.stream()
				.filter(c -> c.getPropriedade() != null && !c.getPropriedade().isBlank())
				.filter(c -> !c.getPropriedade().equalsIgnoreCase("NORMAL"))
				.collect(Collectors.groupingBy(CardSetDetailsDTO::getPropriedade, Collectors.counting()));
		
		List<Map<String, Object>> infoMap = new ArrayList<>();
		
		for(Map.Entry<String, Long> entry : mapProperties.entrySet()) {
			CardProperty prop = CardProperty.getByValue(entry.getKey());
			Map<String, Object> mapAux = new HashMap<>(3);
			mapAux.put("name", prop.getCardProperty());
			mapAux.put("path", prop.getPath());
			mapAux.put("quantity", entry.getValue());
			infoMap.add(mapAux);
		}
		
		return infoMap;
	}
	
	private List<Map<String, Object>> infoLevel(List<CardSetDetailsDTO> listDetails){
		
		Map<Integer, Long> mapLevel = listDetails.stream()
				.filter(c -> c.getNivel() != null && c.getNivel() > 0)
				.collect(Collectors.groupingBy(CardSetDetailsDTO::getNivel, Collectors.counting()));
		
		List<Map<String, Object>> mapInfo = new ArrayList<>();
		Long totalLevel = mapLevel.values().stream().mapToLong(Long::longValue).sum();
		
		for(Map.Entry<Integer, Long> entry : mapLevel.entrySet()) {
			Map<String, Object> mapAux = new HashMap<>(3);
			int qtd = entry.getValue().intValue();
			mapAux.put("value", entry.getKey());
			mapAux.put("quantity", qtd);
			mapAux.put("percentage", this.calculatePercentage(qtd, totalLevel.intValue()));
			mapInfo.add(mapAux);
		}
		
		return mapInfo;
				
	}
	
	private List<Map<String, Integer>> infoDef(List<CardSetDetailsDTO> listDetails) {	
		Map<Integer, Long> defMap = listDetails.stream()
				.filter(c -> c.getDef() != null)
				.collect(Collectors.groupingBy(CardSetDetailsDTO::getDef, Collectors.counting()));
		
		defMap = new TreeMap<>(defMap);
		
		return mapAttackAndDefense(defMap);		
	}

	private List<Map<String, Integer>> mapAttackAndDefense(Map<Integer, Long> mapAtk) {
		List<Map<String, Integer>> mapList = new ArrayList<>();
		
		for(Map.Entry<Integer, Long> entry : mapAtk.entrySet()) {
			Map<String, Integer> mapInfo = new HashMap<>(2);
			mapInfo.put("value", entry.getKey());
			mapInfo.put("quantity", entry.getValue().intValue());
			mapList.add(mapInfo);
		}
		
		return mapList;
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
	
	private List<Map<String, Object>> listMostValuable(SetDetailsDTO detailDTO, List<CardSetDetailsDTO> listDetails){
		
		List<RelDeckCards> listRel = detailDTO.getInsideDecks().stream().flatMap(card -> card.getRelDeckCards().stream()).toList();
		
		Map<Long, Double> mapVal = listRel.stream().sorted((v1, v2) -> Double.compare(v2.getCard_price(), v1.getCard_price())).limit(6)
				.collect(Collectors.toMap(RelDeckCards::getCardNumber, RelDeckCards::getCard_price
						,(x, y) -> y, LinkedHashMap::new));
		
		List<Map<String, Object>> mapInfo = new ArrayList<>();
		
		for (Map.Entry<Long, Double> entry : mapVal.entrySet()) {
			CardSetDetailsDTO dto = listDetails.stream().filter(c -> c.getNumero().equals(entry.getKey())).findFirst().get();
			Map<String, Object> mapAux = new HashMap<>(4);
			mapAux.put("name", dto.getNome());
			mapAux.put("value", entry.getValue());
			mapAux.put("number", entry.getKey().toString());
			mapAux.put("rarity", listRel.stream().filter(r -> r.getCardNumber().equals(entry.getKey())).findFirst().get().getCard_raridade());
			mapInfo.add(mapAux);			
		}
		
		return mapInfo;
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
