package com.naicson.yugioh.controller;

import java.time.LocalDateTime;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.naicson.yugioh.data.dto.cards.CardOfArchetypeDTO;
import com.naicson.yugioh.entity.Archetype;
import com.naicson.yugioh.repository.ArchetypeRepository;
import com.naicson.yugioh.service.card.CardServiceImpl;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.Authorization;

@RestController
@RequestMapping({"yugiohAPI/arch"})
@CrossOrigin(origins = "http://localhost:4200", maxAge = 3600)
public class ArchetypeController {
	
	@Autowired
	ArchetypeRepository archRepository;	
	@Autowired
	CardServiceImpl cardService;
	
	Logger logger = LoggerFactory.getLogger(ArchetypeController.class);
	
	@Cacheable("archetypes")
	@ApiOperation(value="Return all Archetypes", authorizations = { @Authorization(value="JWT") })
	@GetMapping("/all")
	public List<Archetype> consultar(){
		logger.info("Started consulting all archetypes..." + LocalDateTime.now());
		return archRepository.findAll();
	}
	
	@ApiOperation(value="Return a single Archetype by ID.", authorizations = { @Authorization(value="JWT") })
	@GetMapping("/archetype/{archId}")
	public Archetype consultarPorId( @PathVariable("archId") Integer archId) {
		
		Archetype arch = archRepository.findById(archId).get();
		System.out.println(arch.toString());
		List<CardOfArchetypeDTO> cards = cardService.findCardByArchetype(archId);
		
		arch.setArrayCards(cards);
		System.out.println(arch.toString());
		
		return arch;
	}
	
	
}
