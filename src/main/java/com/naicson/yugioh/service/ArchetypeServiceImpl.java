package com.naicson.yugioh.service;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.naicson.yugioh.data.dto.cards.CardOfArchetypeDTO;
import com.naicson.yugioh.entity.Archetype;
import com.naicson.yugioh.repository.ArchetypeRepository;
import com.naicson.yugioh.service.card.CardServiceImpl;

@Service
public class ArchetypeServiceImpl {
	
	@Autowired
	ArchetypeRepository archRepository;	
	
	@Autowired
	CardServiceImpl cardService;
	
	Logger logger = LoggerFactory.getLogger(ArchetypeServiceImpl.class);
	
	public List<Archetype> getAllArchetypes() {
		logger.info("Started consulting all archetypes...");		
		return archRepository.findAll();		
	}
	
	public Archetype getByArchetypeId(Integer archetypeId) {
		Archetype arch = archRepository.findById(archetypeId).get();
		List<CardOfArchetypeDTO> cards = cardService.findCardByArchetype(archetypeId);	
		arch.setArrayCards(cards);
		
		return arch;
	}
	
	public Archetype getCardArchetype(String archetype) {
		Archetype arch = new Archetype();

		if (archetype != null && !archetype.isEmpty()) {
			arch = archRepository.findByArcName(archetype.trim());
			
			if(arch == null || arch.getId() < 1) {
				arch = archRepository.save(new Archetype(archetype));
			}
		}			
		else
			arch = null;

		return arch;
	}
}
