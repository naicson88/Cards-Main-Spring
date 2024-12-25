package com.naicson.yugioh.service;

import cardscommons.exceptions.ErrorMessage;
import com.naicson.yugioh.data.dto.cards.CardOfArchetypeDTO;
import com.naicson.yugioh.entity.Archetype;
import com.naicson.yugioh.repository.ArchetypeRepository;
import com.naicson.yugioh.service.card.CardServiceImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import javax.persistence.EntityNotFoundException;
import javax.persistence.Tuple;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class ArchetypeServiceImpl {
	
	@Autowired
	ArchetypeRepository archRepository;	
	@Autowired
	CardServiceImpl cardService;
	
	Logger logger = LoggerFactory.getLogger(ArchetypeServiceImpl.class);
	
	public Map<String, ArrayList<Archetype>> getAllArchetypes() {
		List<Archetype> archList =  archRepository.findAll(Sort.by(Sort.Direction.ASC, "arcName"));
		Map<String, ArrayList<Archetype>> archMap = new LinkedHashMap<>();

		for(Archetype arch : archList) {
			try {
				String firstChar = String.valueOf(arch.getArcName().charAt(0));

				if(archMap.containsKey(firstChar))
					archMap.get(firstChar).add(arch);
				else{
					archMap.put(firstChar, new ArrayList<>());
					archMap.get(firstChar).add(arch);
				}

			} catch (Exception e){
				logger.error(arch.getArcName());
				throw e;
			}
		}

		return archMap;
	}
	
	public Archetype getByArchetypeId(Integer archetypeId) {
		Archetype arch = archRepository.findById(archetypeId)
				.orElseThrow(() -> new EntityNotFoundException("Can't found Archetype with ID: " + archetypeId));
		
		List<CardOfArchetypeDTO> cards = cardService.findCardByArchetype(archetypeId);	
		arch.setArrayCards(cards);
		
		return arch;
	}
	
	public Archetype getCardArchetypeByName(String archetype) {		
		if (archetype == null || archetype.isBlank())		
			throw new IllegalArgumentException("Invalid Archetype name to get");
		
		return archRepository.findByArcName(archetype.trim());
	}
	
	public Archetype saveArchetype(String archetype) {	
		if (archetype == null || archetype.isBlank())		
			throw new IllegalArgumentException("Invalid Archetype name to save");
		
		return archRepository.save(new Archetype(archetype.trim()));
	}

	public List<String> getFirstLetterAllArchetypes() {
		List<Tuple> tuple = archRepository.getFirstLetterAllArchetypes();
		if(tuple == null || tuple.isEmpty())
			throw new ErrorMessage(" #getFirstLetterAllArchetypes -> Error when finding Archetypes");

		return tuple.stream().map(it ->  it.get(0, String.class)).collect(Collectors.toList());
	}
}
